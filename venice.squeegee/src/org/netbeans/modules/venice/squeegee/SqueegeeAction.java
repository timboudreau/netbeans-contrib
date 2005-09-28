package org.netbeans.modules.venice.squeegee;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.venice.model.Decorator;
import org.netbeans.modules.venice.model.Model;
import org.netbeans.modules.venice.sourcemodel.api.SrcConstants;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public final class SqueegeeAction extends CallableSystemAction {

    private static Squeegee squeegee = null;
    
    public void performAction() {
        if (squeegee != null) {
	    squeegee.setListener (null);
	    squeegee = null;
	} else {
	    squeegee = new Squeegee();
	    squeegee.setListener (new L());
	}
    }

    public String getName() {
        return "Test Squeegee";
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public boolean isEnabled() {
	return true;
	//XXX add dep on right module and check JavaMetamodel.foo().isScanInProgress()
    }

    protected boolean asynchronous() {
        return false;
    }
    static int ct = 0;
    private class L implements PropertyChangeListener {
	final InputOutput io;
	public L() {
	    io = IOProvider.getDefault().getIO("Squeegee " + (ct++), true);
	}
	
        public void propertyChange(PropertyChangeEvent evt) {
	    Node n = (Node) evt.getNewValue();
	    final String s = n == null ? "null" : n.getDisplayName();
	    io.getOut().println("Selection became " + (n == null ? "null" : n.getDisplayName()));
	    if (n != null && squeegee != null) {
		if (squeegee.accept(n)) {
		    Model m = squeegee.createModel(n);
		    if (m == null) {
			io.getOut().println ("UH OH!  Model should not have been null but was for " + s);
			return;
		    }
		    new CL (m, s, io);
		} else {
		   io.getOut().println("Could not find a source element on " + s);
		}
	    }
        }
    }
    
    private class CL implements ChangeListener, PropertyChangeListener {
        private Model mdl;
        private String name;
        private InputOutput io;
	private Object root;
	private int depth;
	public CL (Model mdl, String name, InputOutput io, Object root, int depth) {
	    this.mdl = mdl;
	    this.name = name;
	    this.io = io;
	    this.root = root;
	    this.depth = depth;
	    init();
	}
	
	public CL (Model mdl, String name, InputOutput io) {
	    this (mdl, name, io, mdl.getRoot(), 0);
	}
	
	private void out (String s) {
	    io.getErr().println(s);
	}
	
	private void init() {
	    out("Object " + name + " at depth " + depth + " model says its name is " + mdl.getDecorator(root).getDisplayName(root) + " identity " + root);
	    Decorator.ChildrenHandle ch = mdl.getDecorator(root).getChildren(root, SrcConstants.CHILDREN_MEMBERS, true);
	    findChildren (ch);
	    ch = mdl.getDecorator(root).getChildren(root, SrcConstants.CHILDREN_USAGES, true);
	    findChildren (ch);
	    ch = mdl.getDecorator(root).getChildren(root, SrcConstants.CHILDREN_CLOSURE, true);
	    findChildren (ch);
	    ch = mdl.getDecorator(root).getChildren(root, SrcConstants.CHILDREN_PARENTCLASS, true);
	    findChildren (ch);
	}
	
	private void findChildren (Decorator.ChildrenHandle ch) {
	    out ("  children of root: " + ch + " state " + (ch.getState() == ch.STATE_COLLECTING_CHILDREN ? "LOADING " : "READY "));
	    ch.addChangeListener (this);
	    if (ch.getState() == ch.STATE_COLLECTING_CHILDREN) {
//		ch.addChangeListener (this);
		out ("   adding listener so it will start gathering children");
	    } else {
		try {
		    System.err.println(ch + " had already completed fetching children");
		    stateChanged (new ChangeEvent (ch));
		} catch (IllegalStateException e) {
		    //Do nothing - in real code we would make sure we never
		    //remove a listener that may not be listening;  this will
		    //do for now
		}
	    }
	}

        public void stateChanged(ChangeEvent e) {
	    Decorator.ChildrenHandle ch = (Decorator.ChildrenHandle) e.getSource();
	    ch.removeChangeListener(this);
	    out ("  Getting child list completed for " + name + " ("+ ch + ")");
	    out ("  Children of " + name + ": " + ch.getChildren());
	    if (depth < 2) {
		int ix = 0;
		for (Iterator i=ch.getChildren().iterator(); i.hasNext();) {
		    Object o = i.next();
		    out ("   Child " + ix + " of kind " + ch.getKind() + " is " + o + " name " + mdl.getDecorator(o).getDisplayName(o));
		    Decorator dec = mdl.getDecorator(o); //Always really just returns the model impl, but we don't know that
		    new CL (mdl, mdl.getDecorator(o).getDisplayName(o), io, o, depth + 1);
		    ix++;
		}
	    }
        }

        public void propertyChange(PropertyChangeEvent evt) {
	    
        }
    }

}
