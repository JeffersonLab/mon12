package org.clas.detectors;

import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Util {
    
    public static JLabel getImage(String path, double scale) {
        try {
            Image image = ImageIO.read(Util.class.getResourceAsStream(path));
            ImageIcon imageIcon = new ImageIcon(image);
            double width = imageIcon.getIconWidth() * scale;
            double height = imageIcon.getIconHeight() * scale;
            imageIcon = new ImageIcon(image.getScaledInstance((int) width, (int) height, Image.SCALE_SMOOTH));
            return new JLabel(imageIcon);
        } catch (Exception ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Picture upload from " + path + " failed");
            return new JLabel();
        }
    }

}
