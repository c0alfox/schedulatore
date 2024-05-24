package schedulatore.gui;

import schedulatore.process.Process;
import schedulatore.process.ProcessManager;
import schedulatore.process.event.ProcessSelectionEvent;
import schedulatore.process.event.ProcessSelectionListener;

import javax.swing.*;

public class ProcessTimeMonitor extends JPanel implements ProcessSelectionListener {
    private final JLabel name;
    private final JLabel completionRatio;
    private final JProgressBar progressBar;

    private volatile boolean exit;
    private Thread bindingThread;

    public ProcessTimeMonitor() {
        name = new JLabel("Processo 1");
        completionRatio = new JLabel("10/1000");
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        add(name);
        add(progressBar);
        add(completionRatio);
        ProcessManager.getInstance().addProcessSelectionListener(this);
    }

    private void bind(Process p) {
        bindingThread = new Thread() {
            @Override
            public synchronized void run() {
                super.run();
                exit = false;

                while (!exit) {
                    fetch(p);

                    try {
                        wait(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        bindingThread.start();
    }

    private void fetch(Process p) {
        name.setText(p.getNome());

        int val = (int)p.getDurata();
        int max = (int)p.getMaxDurata();

        completionRatio.setText(String.format("%d / %d", val, max));
        progressBar.setMaximum(max);
        progressBar.setValue(val);
    }

    private void unbind() {
        if (bindingThread == null) {
            return;
        }

        exit = true;
        synchronized (bindingThread) {
            bindingThread.notify();
        }
        bindingThread = null;
    }

    @Override
    public void processSelectionChanged(ProcessSelectionEvent e) {
        unbind();

        Process p = e.getSelectedProcess();

        if (p != null) {
            bind(e.getSelectedProcess());
        }
    }
}