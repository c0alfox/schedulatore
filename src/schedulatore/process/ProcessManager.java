package schedulatore.process;

import schedulatore.process.event.ProcessConcurrencyEvent;
import schedulatore.process.event.ProcessConcurrencyListener;
import schedulatore.process.event.ProcessSelectionEvent;
import schedulatore.process.event.ProcessSelectionListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class ProcessManager {
    private static volatile ProcessManager instance;

    // Il numero di processi che e' possibile eseguire concorrentemente
    private final int maximumConcurrency = 5;
    // Il numero di processi attualmente in esecuzione;
    private volatile int concurrency = 0;
    private volatile int first_available_pid = 0;

    private final ArrayList<ProcessSelectionListener> processSelectionListeners;
    private final ArrayList<ProcessConcurrencyListener> processConcurrencyListeners;
    private final ArrayList<Process> processi;
    private final JList<Process> list;
    private final DefaultListModel<Process> listModel;

    private ProcessManager() {
        processSelectionListeners = new ArrayList<>();
        processConcurrencyListeners = new ArrayList<>();
        processi = new ArrayList<>();
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);

        ProcessManagerListeners l = new ProcessManagerListeners();
        list.addMouseListener(l);
        list.addListSelectionListener(l);
    }

    public static ProcessManager getInstance() {
        ProcessManager result = instance;
        if (result != null) {
            return result;
        }

        synchronized(ProcessManager.class) {
            if (instance == null) {
                instance = new ProcessManager();
            }
            return instance;
        }
    }

    public JList<Process> getList() {
        return list;
    }

    private void setConcurrency(int concurrency) {
        this.concurrency = concurrency;

        int delta = maximumConcurrency - concurrency;
        dispatchConcurrencyEvent(new ProcessConcurrencyEvent(delta));
    }

    public synchronized void sort() {
        processi.sort((a, b) -> {
            int stato = Integer.compare(a.getStato().getValue(), b.getStato().getValue());
            int priorita = Integer.compare(b.getPriorita(), a.getPriorita());
            int tempo = Float.compare(a.getDurata(), b.getDurata());

            if (stato == 0) {
                if (priorita == 0) {
                    return tempo;
                } else {
                    return priorita;
                }
            }

            return stato;
        });

        refresh();
    }

    public synchronized void refresh() {
        listModel.setSize(processi.size());
        int i = 0;
        for (Process p : processi) {
            listModel.set(i, p);
            i++;
        }
    }

    public synchronized void add(ProcessBuilder pb) {
        // Questa funzione aggiunge un elemento alla lista e la ordina.
        pb.setID(first_available_pid++);
        Process p = pb.build();
        processi.add(p);
        p.start();
        sort();
    }

    public synchronized void remove(Process p) {
        processi.removeIf(oth -> oth.getID() == p.getID());
        sort();
    }

    public synchronized void setProcess(Process p) {
        int idx = processi.indexOf(p);
        if (idx == -1) {
            System.out.println("Process " + p + " not found");
            return;
        }
        listModel.set(idx, p);
        sort();
    }

    public synchronized void setProcess(int index, Process p) {
        processi.set(index, p);
        listModel.set(index, p);
        sort();
    }

    public synchronized void editSelectedProcess(ProcessLike pl) {
        int idx = list.getSelectedIndex();
        if (idx == -1) {
            System.out.println("No selected process");
            return;
        }

        Process p = processi.get(idx);
        p.edit(pl);
        setProcess(idx, p);
    }

    public synchronized void runProcessByPriority() {
        if (maximumConcurrency <= concurrency || listModel.isEmpty()) {
            return;
        }

        Process p = listModel.get(0);
        if (p == null) {
            System.out.println("[WARN] Calling Toggle Process with no available processes");
            return;
        }

        p.signal(Interrupt.START);
    }

    public synchronized void runSelectedProcess() {
        if (maximumConcurrency <= concurrency) {
            return;
        }

        Process p = list.getSelectedValue();
        if (p == null) {
            System.out.println("[WARN] Calling Start Process with Null Selected value");
            return;
        }

        if (p.getStato() == Stato.NUOVO || p.getStato() == Stato.PRONTO)
            p.signal(Interrupt.START);
    }

    public synchronized void pauseSelectedProcess() {
        Process p = list.getSelectedValue();
        if (p == null) {
            System.out.println("[WARN] Calling Toggle Process with Null Selected value");
            return;
        }

        p.signal(Interrupt.PAUSE);
    }

    public synchronized void stopSelectedProcess() {
        Process p = list.getSelectedValue();
        if (p == null) {
            System.out.println("[WARN] Calling Stop Process with null selected value");
            return;
        }

        p.signal(Interrupt.KILL);
    }

    public synchronized void removeSelectedProcess() {
        Process p = list.getSelectedValue();
        if (p == null) {
            System.out.println("[WARN] Calling Toggle Process with Null Selected value");
            return;
        }

        p.signal(Interrupt.KILL);
        processi.remove(p);
        sort();
    }

    private void dispatchConcurrencyEvent(ProcessConcurrencyEvent e) {
        for (ProcessConcurrencyListener l : processConcurrencyListeners) {
            l.concurrencyChanged(e);
        }
    }

    private void dispatchProcessSelectionEvent(ProcessSelectionEvent e) {
        for (ProcessSelectionListener l : processSelectionListeners) {
            l.processSelectionChanged(e);
        }
    }

    public synchronized void onProcessStart() {
        setConcurrency(concurrency + 1);
    }

    public synchronized void onProcessStop() {
        setConcurrency(concurrency - 1);
    }

    public void addListSelectionListener(ListSelectionListener l) {
        list.addListSelectionListener(l);
    }

    public void removeListSelectionListener(ListSelectionListener l) {
        list.removeListSelectionListener(l);
    }

    public void addProcessSelectionListener(ProcessSelectionListener l) {
        processSelectionListeners.add(l);
    }

    public void removeProcessSelectionListener(ProcessSelectionListener l) {
        processSelectionListeners.remove(l);
    }

    public void addProcessConcurrencyListener(ProcessConcurrencyListener l) {
        processConcurrencyListeners.add(l);
    }

    public void removeProcessConcurrencyListener(ProcessConcurrencyListener l) {
        processConcurrencyListeners.remove(l);
    }

    private final class ProcessManagerListeners implements MouseListener, ListSelectionListener {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() != MouseEvent.BUTTON3) {
                return;
            }

            list.clearSelection();
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) { }
        @Override
        public void mouseReleased(MouseEvent mouseEvent) { }
        @Override
        public void mouseEntered(MouseEvent mouseEvent) { }
        @Override
        public void mouseExited(MouseEvent mouseEvent) { }

        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            if (listSelectionEvent.getValueIsAdjusting()) {
                return;
            }

            Process p = list.getSelectedValue();
            dispatchProcessSelectionEvent(new ProcessSelectionEvent(p));
        }
    }
}