package schedulatore.gui;

import schedulatore.process.*;
import schedulatore.process.Process;
import schedulatore.process.ProcessBuilder;
import schedulatore.process.event.ProcessSelectionEvent;
import schedulatore.process.event.ProcessSelectionListener;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class InsertPanel extends JPanel implements ActionListener, ProcessSelectionListener {
    private final JTextField[] inputs;
    private final ProcessBuilder pb;
    private final ProcessTimeMonitor ptm;
    private final JButton[] process_actions;

    public InsertPanel() {
        pb = new ProcessBuilder();
        ptm = new ProcessTimeMonitor();

        setLayout(new BorderLayout(0, 20));

        process_actions = new JButton[3];

        process_actions[0] = new JButton("Apri");
        process_actions[1] = new JButton("Modifica");
        process_actions[1].setEnabled(false);
        process_actions[2] = new JButton("Elimina");
        process_actions[2].setEnabled(false);

        JPanel bottom_buttons = new JPanel();

        for (int i = 0; i < 3; i++) {
            process_actions[i].addActionListener(this);
            bottom_buttons.add(process_actions[i]);
        }

        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = 30;
        gbc.ipady = 15;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        inputs = new JTextField[5];
        inputs[0] = new JTextField("0", 5);
        inputs[1] = new JTextField("Word", 30);
        inputs[2] = new JTextField("10", 10);
        inputs[3] = new JTextField("3.5", 5);
        inputs[4] = new JTextField(String.valueOf(Stato.NUOVO));
        inputs[4].setEditable(false);

        for (int i = 0; i < 5; i++) {
            inputs[i].setMinimumSize(new Dimension(100, inputs[i].getPreferredSize().height));
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Priorità: ", JLabel.RIGHT), gbc);


        gbc.gridx = 1;
        gbc.gridy = 0;
        form.add(inputs[0], gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Nome: ", JLabel.RIGHT), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        form.add(inputs[1], gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Durata: ", JLabel.RIGHT), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(inputs[2], gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(new JLabel("Dimensione: ", JLabel.RIGHT), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        form.add(inputs[3], gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        form.add(new JLabel("Stato: ", JLabel.RIGHT), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        form.add(inputs[4], gbc);

        add(form, BorderLayout.CENTER);
        add(bottom_buttons, BorderLayout.SOUTH);
        add(ptm, BorderLayout.NORTH);
        ptm.setVisible(false);

        ProcessManager.getInstance().addProcessSelectionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "Apri":
                ProcessManager.getInstance().add(getProcessFromInputs());
                break;

            case "Modifica":
                ProcessManager.getInstance().editSelectedProcess(getProcessFromInputs());
                break;

            case "Elimina":
                ProcessManager.getInstance().removeSelectedProcess();
                break;
        }
    }

    private ProcessBuilder getProcessFromInputs() {
        pb.setNome(inputs[1].getText());

        try {
            pb.setPriorita(Integer.parseInt(inputs[0].getText()));
        } catch (Exception ex) {
            System.out.println("[ERROR] Priority conversion");
            ex.printStackTrace();
        }

        try {
            pb.setMaxDurata(Float.parseFloat(inputs[2].getText()));
        } catch (Exception ex) {
            System.out.println("[ERROR] Duration conversion");
            ex.printStackTrace();
        }

        try {
            pb.setDimensione(Float.parseFloat(inputs[3].getText()));
        } catch (Exception ex) {
            System.out.println("[ERROR] Dimension conversion");
            ex.printStackTrace();
        }

        return pb;
    }

    @Override
    public void processSelectionChanged(ProcessSelectionEvent e) {
        Process p = e.getSelectedProcess();

        if (p == null) {
            process_actions[0].setEnabled(true);
            process_actions[1].setEnabled(false);
            process_actions[2].setEnabled(false);

            inputs[4].setText(String.valueOf(Stato.NUOVO));
            ptm.setVisible(false);
            return;
        }

        process_actions[0].setEnabled(false);
        process_actions[1].setEnabled(true);
        process_actions[2].setEnabled(true);

        inputs[0].setText(String.valueOf(p.getPriorita()));
        inputs[1].setText(p.getNome());
        inputs[2].setText(String.valueOf(p.getMaxDurata()));
        inputs[3].setText(String.valueOf(p.getDimensione()));
        inputs[4].setText(String.valueOf(p.getStato()));
        ptm.setVisible(true);
    }
}
