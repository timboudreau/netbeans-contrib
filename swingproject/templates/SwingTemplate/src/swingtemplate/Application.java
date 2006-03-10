/*
 * Application.java
 *
 * Created on March 3, 2006, 5:51 PM
 */

package swingtemplate;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


/**
 * A base class for Swing applications.  Subclasses override 
 * initialize() to build the initial GUI, show() to display
 * it on the screen, and [optionally] cleanup() to take
 * care of any housekeeping needed when then application exits.
 * <p>
 * Applications are started with the launch method, which is 
 * typically called from the application's main method:
 * <pre>
 * public class Main {
 *     public static void main(final String[] args) {
 *         Application.launch(MyApplication.class, args);
 *     }
 * } 
 * </pre>
 * <p>
 * Resources are automatically loaded from a ResourceBundle called
 * "Defaults" (see resources/Defaults.properties), before the
 * initialize() method is called.  Applications can lookup localized
 * default values for Strings and Icons and so on using the
 * Application.getDefaultXXX methods:
 * <pre>
 * Application.getDefaultString()
 * Application.getDefaultIcon()
 * Application.getDefaultFont()
 * Application.getDefaultColor()
 * Application.getDefaultObject()
 * </pre>
 * 
 */
public class Application {
    private static Application application = null;
    private static Logger logger;
    private final Map<String, Object> defaults;
    
    /**
     * Constructs the application's GUI.  
     * 
     * Called by Application.launch(), after defaults have been 
     * loaded from the Application's ResourceBundle and before
     * the Application is displayed with Application.show().
     * 
     * @see #launch
     * @see #show
     */
    protected void initialize(String[] args) {
    }


    /**
     * Show the GUI.  Typically this method will apply the following
     * boilerplate to each of the frames that's to appear when
     * the application starts:
     * <pre>
     * frame.pack();
     * frame.setVisible(true);
     * </pre>
     * 
     * @see #launch
     * @see #initialize
     */
    protected void show() {
    }

    /** 
     * Called when the application exits.  Subclasses may override
     * this method to do any cleanup neccessary before exiting.
     * Obviously, you'll want to try and do as little as possible 
     * at this point.  
     * 
     * @see #exit
     */
    protected void cleanup() {
    }

    /**
     * Calls cleanup() and then exits the Application with System.exit(0).
     * Errors thrown while running cleanup() are logged but otherwise ignored.
     * By default, exit() is Called if the user closes the mainFrame or
     * if they select the File/Exit menu item.
     * 
     * @see #cleanup
     */
    public void exit() {
	try {
	    cleanup();
	}
	catch (Exception ignore) { 
	    logger.log(Level.WARNING, "unexpected error in Application.cleanup()", ignore);
	}
	finally {
	    System.exit(0);
	}
    }


    /**
     * Creates an instance of the specified class, initializes the 
     * Application singleton, and then calls the initialize() and
     * show() methods.  All of this is done on the event dispatching 
     * thread.
     */
    public static void launch(final Class applicationClass,  final String[] args) {
	Runnable doCreateAndShowGUI = new Runnable() {
	    public void run() {
		try {
		    application = (Application)applicationClass.newInstance();
		    application.initializeDefaults();
		    application.initialize(args);
		    application.show();
		}
		catch (Exception e) {
                    e.printStackTrace();
		    logger.log(Level.SEVERE, "Application failed to launch", e);
		}
	    }
	};
	SwingUtilities.invokeLater(doCreateAndShowGUI);
    }


    protected Application() { 
	logger = Logger.getLogger(getClass().getName());
	defaults = new HashMap<String, Object>();
    }


    /* Copy the resources/Defaults ResourceBundle into the defaults
     * Map. This means that defaults.get() will return null for
     * missing resources, rather than throwing a MissingResourceException.
     */
    private void initializeDefaults() {
	String bundleName = getClass().getPackage().getName() + ".resources.Defaults";
	ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
	Enumeration<String> keys = bundle.getKeys();
	while(keys.hasMoreElements()) {
	    String key = keys.nextElement();
	    defaults.put(key, bundle.getObject(key));
	}
    }

    /**
     * Application is a singleton that's constructed by the 
     * launch() method.  This method returns the single
     * static Application object, or null if launch() hasn't
     * been called yet.
     */
    public static Application getInstance() { 
	return application; 
    }

    private static void defaultsPut(String key, Object value) {
	if (key == null) {
	    throw new IllegalArgumentException("invalid key - null");
	}
	Application.getInstance().defaults.put(key, value);
    }

    private static Object defaultsGet(String key) {
	if (key == null) {
	    throw new IllegalArgumentException("invalid key - null");
	}
	return Application.getInstance().defaults.get(key);
    }

    public static void putDefaultObject(String key, Object value) {
	defaultsPut(key, value);
    }

    public static Object getDefaultObject(String key) {
	return defaultsGet(key);
    }

    public static String getDefaultString(String key, Object... args) {
	if (args.length == 0) {
	    return (String)defaultsGet(key);
	}
	else {
	    String pattern = (String)defaultsGet(key);
	    return MessageFormat.format(pattern, args);
	}
    }

    public static Icon getDefaultIcon(String key) {
	Object value = defaultsGet(key);
	if ((value == null) || (value instanceof Icon)) {
	    return (Icon)value;
	}
	else if (value instanceof String) {
	    String filename = "resources/" + (String)value;
	    URL url = Application.getInstance().getClass().getResource(filename);
	    if (url != null) {
		Icon icon = new ImageIcon(url);
		defaultsPut(key, icon);
		return icon;
	    }
	    else {
		logger.warning("couldn't find icon resource named: " + filename);
		return null;
	    }
	}
	else {
	    return null;
	}
    }

    public static Font getDefaultFont(String key) {
	Object value = defaultsGet(key);
	if ((value == null) || (value instanceof Font)) {
	    return (Font)value;
	}
	else if (value instanceof String) {
	    // face-STYLE-size, for example "Arial-PLAIN-12"
	    Font font = Font.decode((String)value);
	    defaultsPut(key, font);
	    return font;
	}
	else {
	    return null;
	}
    }

    public static Color getDefaultColor(String key) {
	Object value = defaultsGet(key);
	if ((value == null) || (value instanceof Color)) {
	    return (Color)value;
	}
	else if (value instanceof String) {
	    Color color = decodeColor((String)value);
	    defaultsPut(key, color);
	    return color;
	}
	else {
	    return null;
	}
    }


    /* An improved version of Color.decode() that supports colors
     * with an alpha channel and comma separated RGB[A] values.
     * Legal format for color resources are:
     * "#RRGGBB",  "#AARRGGBB", "R, G, B", "R, G, B, A"
     * Thanks to Romain Guy for the code.
     */
    private static Color decodeColor(String value) {
	Color color = null;
	if (value.startsWith("#")) {
	    switch (value.length()) {
		// RGB/hex color
	    case 7:
		color = Color.decode(value);
		break;
		// ARGB/hex color
	    case 9:
		int alpha = Integer.decode(value.substring(0, 3));
		int rgb = Integer.decode("#" + value.substring(3));
		color = new Color(alpha << 24 | rgb, true);
		break;
	    default:
		// TBD log an error
		return null;
	    }
	} 
	else {
	    String[] parts = value.split(",");
	    if (parts.length < 3 || parts.length > 4) {
		logger.warning("invalid R, G, B[, A] color resource: " + value);
		return null;
	    }
	    try {
		// with alpha component
		if (parts.length == 4) {
		    int r = Integer.parseInt(parts[0].trim());
		    int g = Integer.parseInt(parts[1].trim());
		    int b = Integer.parseInt(parts[2].trim());
		    int a = Integer.parseInt(parts[3].trim());
		    color = new Color(r, g, b, a);
		} else {
		    int r = Integer.parseInt(parts[0].trim());
		    int g = Integer.parseInt(parts[1].trim());
		    int b = Integer.parseInt(parts[2].trim());
		    color = new Color(r, g, b);
		}
	    } 
	    catch (NumberFormatException e) {
		logger.log(Level.WARNING, "invalid R, G, B[, A] color resource: " + value, e);
	    }
	}
	return color;
    }


    /**
     * A convenience method that computes the origin of the window so
     * that it will appear centered on the screen.  On a system with
     * multiple screens we try and locate the window on the screen
     * where the mouse is.
     */
    public static Point screenCenterWindowOrigin(Window window) {
	Point mouseXY = new Point(0,0);
	try {
	    mouseXY = MouseInfo.getPointerInfo().getLocation();
	}
	catch (Exception ignore) { 
	    // sandboxed apps aren't allowed to do this
	}
	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	for (GraphicsDevice device : env.getScreenDevices()) {
	    Rectangle screenBounds = device.getDefaultConfiguration().getBounds();
	    Rectangle windowBounds = window.getBounds();
	    if (screenBounds.contains(mouseXY)) {
		int x = (screenBounds.width - windowBounds.width) / 2;
		int y = (screenBounds.height - windowBounds.height) / 2;
		return new Point(x + screenBounds.x, y + screenBounds.y);
	    }
	}
	return new Point(0,0);
    }

    /**
     * A convenience method that computes the origin of the window2 so
     * that it will appear centered over window1.  
     */
    public static Point windowCenterWindowOrigin(Window window1, Window window2) {
	// TBD
	return screenCenterWindowOrigin(window2);
    }

    /**
     * A convenience method that sets the look and feel to match the
     * native operating system platform.
     * 
     * @see UIManager#getSystemLookAndFeelClassName
     */
    public static void setSystemLookAndFeel() {
	try {
	    String name = UIManager.getSystemLookAndFeelClassName();
	    UIManager.setLookAndFeel(name);
	} 
	catch (Exception ignore) { 
	    logger.log(Level.WARNING, "UIManager.setLookAndFeel() failed", ignore);
	}
    }
}
