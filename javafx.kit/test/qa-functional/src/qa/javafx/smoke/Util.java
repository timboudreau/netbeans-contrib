/*
 * Util.java
 *
 * Created on July 10, 2007, 12:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package qa.javafx.smoke;

import java.awt.Component;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JProgressBarOperator;

/**
 *
 * @author Alexandr Scherbatiy sunflower@netbeans.org
 */


public class Util {

    
    
    
    protected static final String XTEST_DATA = "xtest.data";
    protected static final String XTEST_DATA_PATH = System.getProperty(XTEST_DATA);


    private static final boolean FLAG_SHOW_DETAIL_INFORMATION = true;
    private static final boolean FLAG_SHOW_CLASS_HIERARCHY = false;

    
    private static final int N = 3;
    private static final int WAIT_TIME = 2000;


    public static String getXtestDataPath() {
        return System.getProperty(XTEST_DATA) + "/data";
    }

    public static File getXtestNBMsPath() {
        return new File(new File(XTEST_DATA_PATH).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile(), "nbbuild/nbms/javafx");
    }

    public static String getSampleText(String example) {

        String examplePath = getXtestDataPath() + "/" + example;

        //File file = new File(examplePath);
        try {
            BufferedReader input = new BufferedReader(new FileReader(examplePath));

            String text = "";

            String line = null;
            while ((line = input.readLine()) != null) {
                text += line + "\n";
            }
            return text;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void showIDE() {
        showComponents(MainWindowOperator.getDefault().getSource());
    }


    public static void showComponents(ComponentOperator comp) {
        showComponents(comp.getSource());
    }

    /**
     *
     * @param comp
     */
    public static void showComponents(Component comp) {
        showComponents("", comp);
    }

    /**
     *
     * @param blank
     * @param comp
     */
    public static void showComponents(String blank, Component comp) {
        System.out.println(blank + comp);

        showDetailedInformatin(blank, comp);
        showClassHierarchy(blank, comp);


        if (comp instanceof Container) {
            Container cont = (Container) comp;
            Component[] comps = cont.getComponents();

            for (Component c : comps) {
                showComponents(blank + " ", c);
            }
        }
    }


    /**
     *
     * @param obj
     */
    public static void showClassHierarchy(Object obj) {
        showClassHierarchy("", obj);
    }

    /**
     *
     * @param blank
     * @param obj
     */
    public static void showClassHierarchy(String blank, Object obj) {
        if (FLAG_SHOW_CLASS_HIERARCHY) {
            showClassHierarchy(blank + " ", obj.getClass());
        }
    }

    /**
     *
     * @param cls
     */
    protected static void showClassHierarchy(Class cls) {
        showClassHierarchy("", cls);
    }

    /**
     *
     * @param blank
     * @param cls
     */
    protected static void showClassHierarchy(String blank, Class cls) {


        Class superClass = cls.getSuperclass();
        if (superClass != null) {
            showClassHierarchy(blank + "  ", superClass);
        }

        Class[] interfaces = cls.getInterfaces();
        if (interfaces != null) {
            for (Class i : interfaces) {
                showClassHierarchy(blank + "  ", i);
            }
        }

        char c = (cls.isInterface()) ? ' ' : '+';

        System.out.println(blank + c + "\"" + cls.getName() + "\"");
    }

    public static void showDetailedInformatin(String blank, Component comp) {

        if (FLAG_SHOW_DETAIL_INFORMATION) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                System.out.println(blank + "[button] { tooltip: " + button.getToolTipText() + " text:" + button.getText() + "}");
            } else if (comp instanceof JTextComponent) {
                JTextComponent textComponent = (JTextComponent) comp;
                System.out.println(blank + "[text]");
                System.out.println(textComponent.getText());
            } else if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                TableModel tableModel = table.getModel();
                System.out.println(blank + "[table] " + table.getRowCount() + ", " + table.getColumnCount());

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        System.out.println(blank + "  [" + i + "," + j + "] " + tableModel.getValueAt(i, j));
                    }
                }
            }
        }
    }



    public static void waitProgressBar(ComponentOperator comp) {

        Container src = (Container) comp.getSource();
        int n = 0;

        for (n = 0; n < N; n++) {
            if (JProgressBarOperator.findJProgressBar(src) != null) {
                while (JProgressBarOperator.findJProgressBar(src) != null) {
                    sleep();
                }
                break;
            }
            sleep();
        }
    }


// =================== Utility Operations  ===================
    static class ClassNameComponentChooser implements ComponentChooser {

        String text;

        ClassNameComponentChooser(String text) {
            this.text = text;
        }

        public boolean checkComponent(Component component) {
            return component.toString().contains(text);
        }

        public String getDescription() {
            return "ButtonComponentChooser: \"" + text + "\"";
        }
    }



    public static void sleep() {
        sleep(WAIT_TIME);
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
        }
    }
}
