/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package util;

import java.lang.reflect.InvocationTargetException;
import junit.framework.AssertionFailedError;
import org.netbeans.modules.vcscore.runtime.RuntimeCommand;
import org.netbeans.modules.vcscore.runtime.RuntimeCommandsProvider;
import org.openide.filesystems.FileSystem;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;

public class History {
    
    FileSystem filesystem;
    RuntimeCommandsProvider provider;
    RuntimeCommand breakpoint;
    
    public History(FileSystem filesystem) {
        this.filesystem = filesystem;
        provider = RuntimeCommandsProvider.findProvider(filesystem);
    }
    
    public void breakpoint () {
        RuntimeCommand[] rc = provider.children();
        breakpoint = (rc != null  &&  rc.length > 0) ? rc[rc.length - 1] : null;
    }
    
    public RuntimeCommand getLastCommand (String name, String file) {
        RuntimeCommand[] rc = provider.children();
        if (rc == null)
            return null;
        for (int a = rc.length - 1; a >= 0; a --) {
            if (rc[a] == breakpoint)
                break;
            Node no = rc[a].getNodeDelegate();
            String s = getPropertyValue (no, "Properties", "Processed Files");
            if (name.equals (no.getDisplayName ())  &&  (file == null  ||  file.equals (s)))
                return rc[a];
        }
        return null;
    }
    
    private String getPropertyValue (Node node, String set, String prop) {
        PropertySet[] ps = node.getPropertySets ();
        if (ps == null)
            return null;
        for (int a = 0; a < ps.length; a ++) {
            Property[] p = ps[a].getProperties ();
            if (p == null  ||  !set.equals (ps[a].getDisplayName ()))
                continue;
            for (int b = 0; b < p.length; b ++) {
                if (!prop.equals (p[b].getDisplayName ()))
                    continue;
                try {
                    Object o = p[b].getValue ();
                    return (o != null) ? o.toString () : null;
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
        return null;
    }
    
    public RuntimeCommand getWaitCommand (String name, String file) {
        for (int a = 0; a < 60; a ++) {
            RuntimeCommand rc = getLastCommand (name, file);
            if (rc != null  &&  rc.getState() == RuntimeCommand.STATE_DONE) {
                breakpoint = rc;
                return rc;
            }
            try { Thread.sleep (1000); } catch (Exception e) { e.printStackTrace (); }
        }
        throw new AssertionFailedError ("Timeout: Command does not finished: Command: " + name);
    }
    
    public boolean waitCommand (String name, String file) {
        for (int a = 0; a < 60; a ++) {
            RuntimeCommand rc = getLastCommand (name, file);
            if (rc != null  &&  rc.getState() == RuntimeCommand.STATE_DONE) {
                breakpoint = rc;
                return rc.getExitStatus() == RuntimeCommand.SUCCEEDED;
            }
            try { Thread.sleep (1000); } catch (Exception e) { e.printStackTrace (); }
        }
        throw new AssertionFailedError ("Timeout: Command does not finished: Command: " + name);
    }
    
    public static boolean resultCommand (RuntimeCommand rc) {
        return rc.getExitStatus() == RuntimeCommand.SUCCEEDED;
    }
    
}
