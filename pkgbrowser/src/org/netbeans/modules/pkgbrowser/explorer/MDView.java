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
package org.netbeans.modules.pkgbrowser.explorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
importorg.netbeans.modules.pkgbrowser.FilterHistory;
import org.netbeans.modules.pkgbrowser.Filterable;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ListView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * A basic master-detail view - has an explorer manager that syncs it's root
 * with the explorer manager in a parent component, and offers a place to put
 * a child component, inside its child split pane.
 *
 * @author Timothy Boudreau
 */
public class MDView extends JPanel implements ExplorerManager.Provider, PropertyChangeListener {
    
    private final ExplorerManager mgr = new ExplorerManager();
    private final JSplitPane jsp = new JSplitPane();
    
    /** Creates a new instance of MDView */
    public MDView(String filterKey) {
        setLayout (new BorderLayout());
        
        add (jsp, BorderLayout.CENTER);
        
        ListView lv = new ListView();
        
        //More border vileness...
        Border b = BorderFactory.createEmptyBorder();
        lv.setBorder(b);
        lv.setViewportBorder(b);
        jsp.setBorder (b);
        
        lv.setMinimumSize(new Dimension (220, 200));
        jsp.setLeftComponent(lv);
    }
    
    public void setRightView (JComponent view) {
        jsp.setRightComponent(view);
    }

    public ExplorerManager getExplorerManager() {
        return mgr;
    }
    
    private ExplorerManager.Provider provider = null;
    public void addNotify() {
        super.addNotify();
        provider = (ExplorerManager.Provider) SwingUtilities.getAncestorOfClass(ExplorerManager.Provider.class, this);
        if (provider != null) {
            provider.getExplorerManager().addPropertyChangeListener(this);
        }
    }
    
    public void removeNotify() {
        super.removeNotify();
        if (provider != null) {
            provider.getExplorerManager().removePropertyChangeListener(this);
            provider = null;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        ExplorerManager mgr = (ExplorerManager) evt.getSource();
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] n = mgr.getSelectedNodes();
            if (n.length == 1) {
                this.mgr.setRootContext(n[0]);
            } else {
                this.mgr.setRootContext (new AbstractNode(Children.LEAF));
            }
        }
    }
}
