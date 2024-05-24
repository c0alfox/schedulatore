package schedulatore.process.event;

public class ProcessConcurrencyEvent {
    private final int slotsAvailable;

    public ProcessConcurrencyEvent(int slotsAvailable) {
        this.slotsAvailable = slotsAvailable;
    }

    public int getSlotsAvailable() {
        return slotsAvailable;
    }
}
