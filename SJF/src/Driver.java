import java.io.*;
import java.util.*;

class Driver {
    
    private List<PCB> Q1;
    private List<PCB> Q2;
    private String ordChart = "";

    int clTime = 0; int quantum = 3;  int Counter = 0;
    
    public Driver(List<PCB> Q1, List<PCB> Q2) {
        this.Q1 = Q1;
        this.Q2 = Q2;

        sortByArrivalTime(this.Q1);
        sortByArrivalTime(this.Q2);
    }

    public void executeSchedulingAlgorithms() {
        PCB excutingProcess = null;
        List<PCB> rQ1 = new ArrayList<>();
        List<PCB> rQ2 = new ArrayList<>();

        clTime = 0;
        Counter = 0;
        ordChart = "";

        for (PCB process : Q1) {
            process.exceutionTime = 0;
            process.StartTime = 0;
            process.TerminationTime = 0;
        }

        for (PCB process : Q2) {
            process.exceutionTime = 0;
            process.StartTime = 0;
            process.TerminationTime = 0;
        }

        while (!Q1.isEmpty() || !Q2.isEmpty() || !rQ1.isEmpty() || !rQ2.isEmpty() || excutingProcess != null) {

            while (!Q1.isEmpty() && Q1.get(0).ArrivalTime <= clTime) {
                PCB process = Q1.remove(0);
                rQ1.add(process);
            }

            while (!Q2.isEmpty() && Q2.get(0).ArrivalTime <= clTime) {
                PCB process = Q2.remove(0);
                rQ2.add(process);
                sortByBurstTime(rQ2);
            }

            if (excutingProcess == null) {
                PCB process = null;
                if (!rQ1.isEmpty()) {
                    process = rQ1.remove(0);
                } else if (!rQ2.isEmpty()) {
                    process = rQ2.remove(0);
                }

                if (process != null)
                    excutingProcess = execute(process);

            } else {
                if (excutingProcess.priority == 1 && !rQ1.isEmpty() && Counter == quantum) {
                    rQ1.add(excutingProcess);
                    excutingProcess = execute(rQ1.remove(0));

                } else if (excutingProcess.priority == 2 && !rQ1.isEmpty()) {
                    rQ2.add(excutingProcess);
                    sortByBurstTime(rQ2);
                    excutingProcess = execute(rQ1.remove(0));
                }
            }

            clTime++;

            if (excutingProcess != null) {
                excutingProcess.exceutionTime++;
                Counter++;
                if (excutingProcess.exceutionTime == excutingProcess.CPUBurst) {
                    terminate(excutingProcess);
                    excutingProcess = null;
                }
            }
        }
    }

    private PCB execute(PCB process) {
        Counter = 0;
        ordChart += process.id + " | ";

        if (process.exceutionTime == 0)
            process.StartTime = clTime;

        return process;
    }

    private void terminate(PCB process) {
        process.TerminationTime = clTime;
        process.WaitingTime = process.TerminationTime - process.ArrivalTime - process.CPUBurst;
        process.ResponseTime = process.StartTime - process.ArrivalTime;
        process.TurnAroundTime = process.TerminationTime - process.ArrivalTime;
    }

    public String getOrdChart() {
        return ordChart;
    }

    private void sortByBurstTime(List<PCB> array) {
        Collections.sort(array, Comparator.comparingInt(a -> a.CPUBurst));
    }

    private void sortByArrivalTime(List<PCB> array) {
        Collections.sort(array, Comparator.comparingInt(a -> a.ArrivalTime));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<PCB> Q1 = new ArrayList<>();
        List<PCB> Q2 = new ArrayList<>();

        Driver scheduler = new Driver(Q1, Q2);

        int choice;

        do {
            System.out.println("Menu:");
            System.out.println("1. Enter process details");
            System.out.println("2. Execute scheduling algorithm");
            System.out.println("3. Display process details and scheduling criteria");
            System.out.println("4. Write process details and scheduling criteria to a file");
            System.out.println("5. Exit the program");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    enterProcessDetails(scanner, Q1, Q2);
                    break;
                case 2:
                    scheduler.executeSchedulingAlgorithm();
                    break;
                case 3:
                    Display() ;
            }
        }
    }
    private static void enterProcessDetails(Scanner scanner, List<PCB> Q1, List<PCB> Q2) {
        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Enter details for Process " + (i + 1) + ":");

            System.out.print("Process ID: ");
            String id = scanner.nextLine();

            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            scanner.nextLine();

            System.out.print("CPU Burst Time: ");
            int cpuBurst = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Priority (1 for Q1, 2 for Q2): ");
            int priority = scanner.nextInt();
            scanner.nextLine();

            PCB process = new PCB(id, arrivalTime, cpuBurst, priority);

            if (priority == 1) {
                Q1.add(process);
            } else if (priority == 2) {
                Q2.add(process);
            } else {
                System.out.println("Invalid priority. Process not added.");
            }
        }

        System.out.println("Process details entered successfully.");
    }


    public static void Display() {
        System.out.println("Processes Details:");
        System.out.println("*******************");
        for (PCB process : array) {
            System.out.println(process);
            System.out.println(""); 
            }

        System.out.println();
        System.out.println("Scheduling order chart: | " + Driver.getOrdChart());
        System.out.println();

        int size = array.size();
        double FinalTurnAround = 0, FinalWait = 0, FinalResponse = 0;

        for (PCB process : array) {
            FinalWait += process.WaitingTime;
            FinalTurnAround += process.TurnAroundTime;
            FinalResponse += process.ResponseTime;
        }

        System.out.println("Processes Scheduling Criteria:");
        System.out.println("**************************************");
        System.out.printf("Average Turnaround Time : %.3f \n", FinalTurnAround / size);
        System.out.printf("Average Waiting Time    : %.3f \n", FinalWait / size);
        System.out.printf("Average Response Time   : %.3f \n", FinalResponse / size);
        System.out.println();
    }

    public static void WriteOnFile() {
        try {
            PrintWriter rep = new PrintWriter("Report.txt");

            rep.println("Processes Details:");
            rep.println("*******************");
            for (PCB process : array){
                pw.println(process);
                System.out.println(""); 
                 }

            rep.println();
            rep.println("Scheduling order chart:| " + Driver.getOrdChart());
            rep.println();

            int size = array.size();
            double FinalTurnAround = 0, FinalWait = 0, FinalResponse = 0;

            for (PCB process : array) {
                FinalWait += process.WaitingTime;
                FinalTurnAround += process.TurnAroundTime;
                FinalResponse += process.ResponseTime;
            }

            rep.println("Processes Scheduling Criteria:");
            rep.println("**************************************");
            rep.printf("Average Turnaround Time : %.3f \n", FinalTurnAround / size);
            rep.printf("Average Waiting Time    : %.3f \n", FinalWait / size);
            rep.printf("Average Response Time   : %.3f \n", FinalResponse / size);
            rep.println();

            rep.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}