import java.io.*;
import java.util.*;

class Driver {

    private List<PCB> Q1;
    private List<PCB> Q2;
    private String orderChart = "";

    int clockTime = 0;
    int quantum = 3;
    int Counter = 0;

    public Scheduler(List<PCB> Q1, List<PCB> Q2) {
        this.Q1 = Q1;
        this.Q2 = Q2;

        sortByArrivalTime(this.Q1);
        sortByArrivalTime(this.Q2);
    }

    public void run() {
        PCB excProcess = null;
        List<PCB> rQ1 = new ArrayList<>();
        List<PCB> rQ2 = new ArrayList<>();

        clockTime = 0;
        Counter = 0;
        orderChart = "";

        for (PCB process : Q1) {
            process.exceutionTime = 0;
            process.startTime = 0;
            process.terminationTime = 0;
        }

        for (PCB process : Q2) {
            process.exceutionTime = 0;
            process.startTime = 0;
            process.terminationTime = 0;
        }

        while (!Q1.isEmpty() || !Q2.isEmpty() || !rQ1.isEmpty() || !rQ2.isEmpty() || excProcess != null) {

            while (!Q1.isEmpty() && Q1.get(0).arrivalTime <= clockTime) {
                PCB process = Q1.remove(0);
                rQ1.add(process);
            }

            while (!Q2.isEmpty() && Q2.get(0).arrivalTime <= clockTime) {
                PCB process = Q2.remove(0);
                rQ2.add(process);
                sortByBurstTime(rQ2);
            }

            if (excProcess == null) {
                PCB process = null;
                if (!rQ1.isEmpty()) {
                    process = rQ1.remove(0);
                } else if (!rQ2.isEmpty()) {
                    process = rQ2.remove(0);
                }

                if (process != null)
                    excProcess = execute(process);

            } else {
                if (excProcess.priority == 1 && !rQ1.isEmpty() && Counter == quantum) {
                    rQ1.add(excProcess);
                    excProcess = execute(rQ1.remove(0));

                } else if (excProcess.priority == 2 && !rQ1.isEmpty()) {
                    rQ2.add(excProcess);
                    sortByBurstTime(rQ2);
                    excProcess = execute(rQ1.remove(0));
                }
            }

            clockTime++;

            if (excProcess != null) {
                excProcess.exceutionTime++;
                Counter++;
                if (excProcess.exceutionTime == excProcess.cpuBurst) {
                    terminate(excProcess);
                    excProcess = null;
                }
            }
        }
    }

    private PCB execute(PCB process) {
        Counter = 0;
        orderChart += process.id + " | ";

        if (process.exceutionTime == 0)
            process.startTime = clockTime;

        return process;
    }

    private void terminate(PCB process) {
        process.terminationTime = clockTime;
        process.waitingTime = process.terminationTime - process.arrivalTime - process.cpuBurst;
        process.responseTime = process.startTime - process.arrivalTime;
        process.turnAroundTime = process.terminationTime - process.arrivalTime;
    }

    public String getOrderChart() {
        return orderChart;
    }

    private void sortByBurstTime(List<PCB> array) {
        Collections.sort(array, Comparator.comparingInt(a -> a.cpuBurst));
    }

    private void sortByArrivalTime(List<PCB> array) {
        Collections.sort(array, Comparator.comparingInt(a -> a.arrivalTime));
    }
}