package schedulatore.process.event;

public interface ProcessConcurrencyListener {
    void concurrencyChanged(ProcessConcurrencyEvent e);
}
