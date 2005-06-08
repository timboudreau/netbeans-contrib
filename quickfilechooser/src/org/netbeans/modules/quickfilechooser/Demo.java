package org.netbeans.modules.quickfilechooser;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
public class Demo {
    public static void main(String[] args) {
        Install.main(null);
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //chooser.setAccessory(new JLabel("extra"));
        chooser.showOpenDialog(null);
        System.out.println("Selected: " + Arrays.asList(chooser.getSelectedFiles()));
        System.exit(0);
    }
}
