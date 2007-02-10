/*
 * ListListener.java
 *
 * Created on February 10, 2007, 1:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.javanavigators;

import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.java.source.UiUtils;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Tim Boudreau
 */
class ListListener implements ListSelectionListener {
    private final JList list;
    private boolean enabled;
    public ListListener(JList list) {
        this.list = list;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled (boolean val) {
        enabled = val;
    }
    
    public void valueChanged(ListSelectionEvent e) {
        AsynchListModel <Description> m = getModel (e);
        if (m != null) {
            Description d = (Description) list.getSelectedValue();
            if (d != null) {
                open (d);
            }
        }
    }
    
    private void open (Description d) {
        assert d.fileObject != null;
        assert d.elementHandle != null;
        System.err.println("Open " + d);
        UiUtils.open (d.fileObject, d.elementHandle);
        try {
        DataObject dob = DataObject.find (d.fileObject);
        EditorCookie ck = (EditorCookie) dob.getCookie (EditorCookie.class);
        if (ck != null) {
            ck.openDocument();
            JEditorPane[] p = ck.getOpenedPanes();
            if (p.length > 0) {
                //Need to do this since we're disabling the window system's
                //auto focus mechanism
                p[0].requestFocus();
            }
        }
        } catch (DataObjectNotFoundException e) {
            Exceptions.printStackTrace(e);
        } catch (IOException ioe) {
            Exceptions.printStackTrace (ioe);
        }
    }
    
    AsynchListModel <Description> getModel (ListSelectionEvent e) {
        ListModel m = list.getModel();
        if (m instanceof AsynchListModel) {
            return (AsynchListModel <Description>) m;
        }
        return null;
    }
}
