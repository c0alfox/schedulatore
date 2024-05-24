package schedulatore.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class IconButton extends JButton {
    // ...
    private String alias;
    private final Image original_icon;
    private final double icon_aspect_ratio;
    private final double inverse_aspect_ratio;

    public IconButton(String alias, String path) {
        ImageIcon img = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        int original_height = img.getIconHeight();
        int original_width = img.getIconWidth();
        icon_aspect_ratio = (double)original_width / (double)original_height;
        inverse_aspect_ratio = 1 / icon_aspect_ratio;

        original_icon = img.getImage();
        setIcon(img);

        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return alias;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        int ref = Math.min(preferredSize.height, preferredSize.width);
        Image tmp;
        if (icon_aspect_ratio > 1) {
            tmp = original_icon.getScaledInstance((int)(0.9*ref), (int)(0.9*ref*inverse_aspect_ratio), Image.SCALE_SMOOTH);
        } else {
            tmp = original_icon.getScaledInstance((int)(0.9*ref*icon_aspect_ratio), (int)(0.9*ref), Image.SCALE_SMOOTH);
        }
        setIcon(new ImageIcon(tmp));
    }

}