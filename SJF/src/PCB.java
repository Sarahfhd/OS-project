class PCB {
    String processID;
    int priority;
    int arrivalTime;
    int cpuBurstTime;
    int startTime;
    int terminationTime;
    int turnaroundTime;
    int waitingTime;
    int responseTime;


    public PCB(String processID, int priority, int arrivalTime, int cpuBurstTime) {
        this.processID = processID;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.cpuBurstTime = cpuBurstTime;
        this.startTime = 0;
        this.terminationTime = 0;
        this.turnaroundTime = 0;
        this.waitingTime = 0;
        this.responseTime = 0;
    }


    public void calculateTurnaroundTime() {
        this.turnaroundTime = this.terminationTime - this.arrivalTime;
    }


    public void calculateWaitingTime() {
        this.waitingTime = this.turnaroundTime - this.cpuBurstTime;
    }


    public void calculateResponseTime() {
        this.responseTime = this.startTime - this.arrivalTime;
    }
}