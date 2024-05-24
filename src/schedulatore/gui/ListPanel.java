package schedulatore.gui;

import schedulatore.process.Process;
import schedulatore.process.event.ProcessConcurrencyEvent;
import schedulatore.process.event.ProcessConcurrencyListener;
import schedulatore.process.ProcessManager;
import schedulatore.process.event.ProcessSelectionEvent;
import schedulatore.process.event.ProcessSelectionListener;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ListPanel extends Panel implements ActionListener, ProcessSelectionListener, ProcessConcurrencyListener {
    private final IconButton[] processActions;
    private boolean runSlotAvailable;
    private boolean processSelected;

    public ListPanel() {
        runSlotAvailable = true;
        processSelected = false;

        setLayout(new BorderLayout(0, 10));

        JPanel action_panel = new JPanel();
        processActions = new IconButton[3];

        processActions[0] = new IconButton("Play", "/img/play.png");
        processActions[0].setEnabled(false);
        processActions[1] = new IconButton("Pause", "/img/pause.png");
        processActions[1].setEnabled(false);
        processActions[2] = new IconButton("Stop", "/img/stop.png");
        processActions[2].setEnabled(false);

        for (int i = 0; i < 3; i++) {
            action_panel.add(processActions[i]);
            processActions[i].addActionListener(this);
            processActions[i].setPreferredSize(new Dimension(50, 50));
        }

        add(action_panel, BorderLayout.SOUTH);

        JList<Process> jl = ProcessManager.getInstance().getList();
        add(new JScrollPane(jl), BorderLayout.CENTER);

        ProcessManager.getInstance().addProcessSelectionListener(this);
        ProcessManager.getInstance().addProcessConcurrencyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getSource().toString()) {
            case "Play":
                ProcessManager.getInstance().runSelectedProcess();
                break;

            case "Pause":
                ProcessManager.getInstance().pauseSelectedProcess();
                break;

            case "Stop":
                ProcessManager.getInstance().stopSelectedProcess();
                break;
        }
    }

    @Override
    public void processSelectionChanged(ProcessSelectionEvent e) {
        Process p = e.getSelectedProcess();
        processSelected = p != null;

        if (p == null) {
            processActions[0].setEnabled(false);
            processActions[1].setEnabled(false);
            processActions[2].setEnabled(false);
            return;
        }

        processActions[0].setEnabled(runSlotAvailable);
        processActions[1].setEnabled(true);
        processActions[2].setEnabled(true);
    }

    @Override
    public void concurrencyChanged(ProcessConcurrencyEvent e) {
        if (e.getSlotsAvailable() == 0) {
            runSlotAvailable = false;
            processActions[0].setEnabled(false);
            return;
        }

        runSlotAvailable = true;
        processActions[0].setEnabled(processSelected);
    }
}
