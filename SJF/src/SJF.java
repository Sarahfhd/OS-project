import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
public class SJF {
    static ArrayList<PCB> allProcesses = new ArrayList<>();
    static ArrayList<PCB> completedProcesses = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("Menu:");
            System.out.println("1. Enter process’ information.");
            System.out.println("2. Report detailed information about each process and different scheduling criteria.");
            System.out.println("3. Exit the program.");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    enterProcessInformation(scanner);
                    break;
                case 2:
                    reportDetailedInformation();
                    break;
                case 3:
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        } while (choice != 3);
    }


    static void enterProcessInformation(Scanner scanner) {
        System.out.print("Enter the total number of processes: ");
        int totalProcesses = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < totalProcesses; i++) {
            System.out.println("Enter details for process " + (i + 1) + ":");
            System.out.print("Priority (1 or 2): ");
            int priority = scanner.nextInt();
            scanner.nextLine();

            if (priority != 2) {
                System.out.println("Process skipped due to priority is not 2.");
                continue;
            }

            System.out.print("Arrival time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("CPU burst time: ");
            int cpuBurstTime = scanner.nextInt();
            scanner.nextLine();

            String processID = "P" + (i + 1);
            PCB process = new PCB(processID, priority, arrivalTime, cpuBurstTime);
            allProcesses.add(process);
        }

        Collections.sort(allProcesses, Comparator.comparingInt(p -> p.arrivalTime));
    }



    static void reportDetailedInformation() {
        try {
            FileWriter writer = new FileWriter("process_report.txt");


            StringBuilder schedulingOrder = new StringBuilder("[");
            int currentTime = 0;
            while (!allProcesses.isEmpty()) {
                PCB shortestJob = null;
                int shortestBurst = Integer.MAX_VALUE;
                int latestArrivalIndex = -1;

                for (int i = 0; i < allProcesses.size(); i++) {
                    PCB process = allProcesses.get(i);
                    if (process.arrivalTime <= currentTime && process.cpuBurstTime < shortestBurst) {
                        shortestJob = process;
                        shortestBurst = process.cpuBurstTime;
                        latestArrivalIndex = i;
                    } else if (process.arrivalTime <= currentTime && process.cpuBurstTime == shortestBurst) {

                        if (latestArrivalIndex == -1 || process.arrivalTime >= allProcesses.get(latestArrivalIndex).arrivalTime) {
                            shortestJob = process;
                            latestArrivalIndex = i;
                        }
                    }
                }

                if (shortestJob != null) {
                    schedulingOrder.append(shortestJob.processID).append(" | ");
                    shortestJob.startTime = currentTime;
                    shortestJob.terminationTime = currentTime + shortestJob.cpuBurstTime;
                    shortestJob.calculateTurnaroundTime();
                    shortestJob.calculateWaitingTime();
                    shortestJob.calculateResponseTime();
                    currentTime = shortestJob.terminationTime;
                    completedProcesses.add(shortestJob);
                    allProcesses.remove(latestArrivalIndex);
                    printProcessDetails(shortestJob, writer);
                } else {
                    currentTime++;
                }
            }
            schedulingOrder.setLength(schedulingOrder.length() - 3);
            schedulingOrder.append("]");
            writer.write("Scheduling order of processes: " + schedulingOrder + "\n");


            double avgTurnaroundTime = 0;
            double avgWaitingTime = 0;
            double avgResponseTime = 0;
            for (PCB process : completedProcesses) {
                avgTurnaroundTime += process.turnaroundTime;
                avgWaitingTime += process.waitingTime;
                avgResponseTime += process.responseTime;
            }
            avgTurnaroundTime /= completedProcesses.size();
            avgWaitingTime /= completedProcesses.size();
            avgResponseTime /= completedProcesses.size();

            writer.write("Average Turnaround Time: " + avgTurnaroundTime + "\n");
            writer.write("Average Waiting Time: " + avgWaitingTime + "\n");
            writer.write("Average Response Time: " + avgResponseTime + "\n");

            writer.close();
            System.out.println("Report written to process_report.txt");


            printConsoleReport();


            System.out.println("Scheduling order of processes: " + schedulingOrder);
            System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
            System.out.println("Average Waiting Time: " + avgWaitingTime);
            System.out.println("Average Response Time: " + avgResponseTime);

            // Reset StringBuilder for scheduling order
            schedulingOrder = new StringBuilder();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }





    static void printConsoleReport() {
        for (PCB process : completedProcesses) {
            System.out.println("Process ID: " + process.processID);
            System.out.println("Priority: " + process.priority);
            System.out.println("Arrival time: " + process.arrivalTime);
            System.out.println("CPU burst time: " + process.cpuBurstTime);
            System.out.println("Start time: " + process.startTime);
            System.out.println("Termination time: " + process.terminationTime);
            System.out.println("Turnaround time: " + process.turnaroundTime);
            System.out.println("Waiting time: " + process.waitingTime);
            System.out.println("Response time: " + process.responseTime);
            System.out.println();
        }
    }


    static void printProcessDetails(PCB process, FileWriter writer) throws IOException {
        writer.write("Process ID: " + process.processID + "\n");
        writer.write("Priority: " + process.priority + "\n");
        writer.write("Arrival time: " + process.arrivalTime + "\n");
        writer.write("CPU burst time: " + process.cpuBurstTime + "\n");
        writer.write("Start time: " + process.startTime + "\n");
        writer.write("Termination time: " + process.terminationTime + "\n");
        writer.write("Turnaround time: " + process.turnaroundTime + "\n");
        writer.write("Waiting time: " + process.waitingTime + "\n");
        writer.write("Response time: " + process.responseTime + "\n\n");
    }
}
