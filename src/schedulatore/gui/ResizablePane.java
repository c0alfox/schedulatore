package schedulatore.gui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.*;
import java.awt.*;

public class ResizablePane extends JPanel {
    private final JSplitPane splitPane;

    public ResizablePane(Component left, Component right, int initialDividerLocation) {
        super(new BorderLayout(0, 0));
        splitPane = new JSplitPane();
        splitPane.setLeftComponent(left);
        splitPane.setRightComponent(right);
        splitPane.setDividerLocation(initialDividerLocation);
        splitPane.setResizeWeight(.5);
        add(splitPane);
    }

    private static int getOrientedSize(JSplitPane sp) {
        return sp.getWidth() - sp.getDividerSize();
    }

    @Override
    public void doLayout() {
        int size = getOrientedSize(splitPane);
        double d = splitPane.getDividerLocation() / (double) size;
        BigDecimal bd = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
        super.doLayout();
        if (splitPane.isShowing()) {
            EventQueue.invokeLater(() -> {
                int s = getOrientedSize(splitPane);
                int iv = (int) (.5 + s * bd.doubleValue());
                splitPane.setDividerLocation(iv);
            });
        }
    }
}
