/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
