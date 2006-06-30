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

package org.netbeans.modules.jemmysupport.bundlelookup;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import org.openide.cookies.EditorCookie;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/** Resource Bundle Lookup action class
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.1
 */
public class BundleLookupAction extends NodeAction {

    private static final long serialVersionUID = 2491826043823675616L;

    /** Not to show icon in main menu. */
    public BundleLookupAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    
    private String tryGetText(Node[] nodes) {
        for (int i=0; nodes!=null && i<nodes.length; i++) {
            EditorCookie cookie=(EditorCookie)nodes[i].getCookie(EditorCookie.class);
            if (cookie!=null) {
                JEditorPane panes[]=cookie.getOpenedPanes();
                for (int j=0; panes!=null && j<panes.length; j++) {
                    String text=panes[j].getSelectedText();
                    if (text!=null && text.length()>0) {
                        if (text.startsWith("\"") && text.endsWith("\"")) text=text.substring(1, text.length()-1);
                        return text;
                    }
                }
            }
        }
        return null;
    }        
    
    /** method performing the action
     * @param nodes selected nodes
     */    
    protected void performAction(Node[] nodes) {
        BundleLookupPanel.openPanel(tryGetText(nodes));
    }
    
    /** action is enabled for any selected node
     * @param node selected nodes
     * @return boolean true
     */    
    public boolean enable (Node[] node) {
        return true;
    }
    
    /** method returning name of the action
     * @return String name of the action
     */    
    public String getName() {
        return NbBundle.getMessage(BundleLookupAction.class, "Title"); // NOI18N
    }

    /** method returning icon for the action
     * @return String path to action icon
     */    
    protected String iconResource() {
       return "org/netbeans/modules/jemmysupport/resources/bundleLookup.png"; // NOI18N
    }
    
    /** method returning action Help Context
     * @return action Help Context
     */    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(BundleLookupAction.class);
    }
    
    /** Always return false - no need to run asynchronously. */
    protected boolean asynchronous() {
        return false;
    }
}

