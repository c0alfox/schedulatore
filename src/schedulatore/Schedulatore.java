package schedulatore;

import schedulatore.gui.InsertPanel;
import schedulatore.gui.ListPanel;
import schedulatore.gui.ResizablePane;

import javax.swing.*;
import java.awt.*;

public class Schedulatore extends JFrame {
    private ResizablePane split;
    private ListPanel lp;
    private InsertPanel ip;

    public Schedulatore(int w, int h) {
        setTitle("Schedulatore");
        setSize(w, h);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lp = new ListPanel();
        ip = new InsertPanel();

        split = new ResizablePane(ip, lp, w / 3);
        add(split);

        setVisible(true);
    }
}
