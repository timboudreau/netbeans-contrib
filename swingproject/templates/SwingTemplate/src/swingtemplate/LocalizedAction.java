
package swingtemplate;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;


/**
 * Creates an Action that calls:
 * <pre><code>
 *   Application.getInstance().methodName() 
 * </code></pre>
 * The Action's properties are loaded from defaults using keywords
 * created by appending the methodName and each of the standard
 * Action keywords.  For example if methodName was "foo", then
 * this Command's Action.SHORT_DESCRIPTION property would be the
 * value of
 * <pre><code>
 *   defaults.get("foo.SHORT_DESCRIPTION")
 * </code></pre>.
 * 
 */
public class LocalizedAction extends AbstractAction {
    private final static String actionKeys[] = {
	Action.NAME, 
	Action.SHORT_DESCRIPTION,
	Action.LONG_DESCRIPTION,
	Action.SMALL_ICON,
	Action.ACTION_COMMAND_KEY,
	Action.ACCELERATOR_KEY,
	Action.MNEMONIC_KEY,
    };
    private final String methodName;

    private KeyStroke getKeyStroke(String key) {
	String s =  Application.getDefaultString(key);
	return (s != null) ? KeyStroke.getKeyStroke(s) : null;
    }

    private Integer getKeyCode(String key) {
	KeyStroke ks = getKeyStroke(key);
	return (ks != null) ? new Integer(ks.getKeyCode()) : null;
    }

    public LocalizedAction(String methodName) {
	super(methodName);
	this.methodName = methodName;
	for(String k : actionKeys) {
	    String mk = this.methodName + "." + k;
	    if (k == Action.MNEMONIC_KEY) {
		putValue(k, getKeyCode(mk));
	    }
	    else if (k == Action.ACCELERATOR_KEY) {
		putValue(k, getKeyStroke(mk));
	    }
	    else if (k == Action.SMALL_ICON) {
		putValue(k, Application.getDefaultIcon(mk));
	    }
	    else {
		putValue(k, Application.getDefaultString(mk));
	    }
	}
    }

    /** 
     * Call target.[methodName]() if it exists.  If not, call
     * target.[methodName](actionEvent). If that doesn't exist,
     * or if anything else goes wrong, then call actionFailed().
     */
    public void actionPerformed(ActionEvent actionEvent) {
	Object target = Application.getInstance();
	Method m = null; 
	Class c = target.getClass();
	try {
	    m = c.getMethod(methodName);
	    m.invoke(target);
	}
	catch (NoSuchMethodException ignore) {
	    try {
		m = c.getMethod(methodName, ActionEvent.class);
		m.invoke(target, actionEvent);
	    }
	    catch (Exception e) {
		actionFailed(actionEvent, e);
	    }
	}
	catch (Exception e) {
	    actionFailed(actionEvent, e);
	}
    }

    /* Log enough output for a developer to figure out 
     * what went wrong.
     */
    private void actionFailed(ActionEvent actionEvent, Exception e) {
	// TBD Log an error
	System.err.println(actionEvent);
	e.printStackTrace();
    }
}

