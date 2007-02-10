/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
