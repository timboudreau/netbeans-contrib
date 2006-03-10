package swingtemplate;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * A simple single-Frame application.  
 */
public class TemplateApplication extends Application {
    private JFrame mainFrame;

    @Override
    protected void initialize(String[] args) {
	/* Remove this line if you want the cross platform Java
	 * look and feel instead of the OS specific one.
	 */
	setSystemLookAndFeel();

	Action aboutAction = new LocalizedAction("showAboutBox");
	Action exitAction = new LocalizedAction("exit");
	JMenuBar menuBar = new JMenuBar();
	String fileMenuTitle = getDefaultString("fileMenu.title");
	JMenu fileMenu = menuBar.add(new JMenu(fileMenuTitle));
	fileMenu.add(aboutAction);
	fileMenu.addSeparator(); 
	fileMenu.add(exitAction);

	String mainFrameTitle = getDefaultString("mainFrame.title");
	mainFrame = new JFrame(mainFrameTitle);
	mainFrame.setJMenuBar(menuBar);
	TemplateMainPanel mainPanel = new TemplateMainPanel();
	mainFrame.add(mainPanel, BorderLayout.CENTER);
    }


    /**
     * Show the mainFrame in the center of the screen. 
     */
    @Override
    protected void show() {
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	mainFrame.addWindowListener(new MainFrameWindowListener());
	mainFrame.pack();
	mainFrame.setLocation(screenCenterWindowOrigin(mainFrame));
	mainFrame.setVisible(true);
    }

    /* When the user closes the mainFrame, call Application.exit().
     */
    private class MainFrameWindowListener extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
	    exit();
	}
    }

    @Override
    protected void cleanup() {
	mainFrame.setVisible(false);
    }

    public void showAboutBox() {
	String title = getDefaultString("aboutBoxFrame.title");
	JFrame aboutBoxFrame = new JFrame(title);
        aboutBoxFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	aboutBoxFrame.add(new AboutBoxPanel(), BorderLayout.CENTER);
	aboutBoxFrame.pack();
	aboutBoxFrame.setLocation(windowCenterWindowOrigin(mainFrame, aboutBoxFrame));
	aboutBoxFrame.setVisible(true);
    }
}