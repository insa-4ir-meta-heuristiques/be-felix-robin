package jobshop.encodings;

public class Machine {

    public int end_time;

    public final int machine;

    public Machine(int machine) {
        this.machine = machine;
        this.end_time = 0;
    }

    public void incrementEndTime(int plus) {
        this.end_time += plus;
    }

}
