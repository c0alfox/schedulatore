package schedulatore.process.event;

import schedulatore.process.Process;

public class ProcessSelectionEvent {
    private final Process selectedProcess;

    public ProcessSelectionEvent(Process selectedProcess) {
        this.selectedProcess = selectedProcess;
    }

    public Process getSelectedProcess() {
        return selectedProcess;
    }
}
