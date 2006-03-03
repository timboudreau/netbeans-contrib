/*
 * Main.java
 *
 * Created on March 3, 2006, 8:43 AM
 */

package swingtemplate;


public class Main {
    private JFrame mainFrame;
    private appDefaults = new UIDefaults();
    
    private void initialize(String[] args) {
	mainFrame = new JFrame("<Project Name>");
	MainPanel mainPanel = new MainPanel();
	mainFrame.add(mainPanel);
    }

    private void show() {
	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	mainFrame.pack();
	mainFrame.setVisible(true);
    }

    public static void main(final String[] args) {
	Runnable doCreateAndShowGUI = new Runnable() {
		public void run() {
		    try {
			Main app = new Main();
			app.initialize(args);
			app.show();
		    }
		    catch (Exception e) {
			// TBD log an error
		    }
		}
	    };
	SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
} 
