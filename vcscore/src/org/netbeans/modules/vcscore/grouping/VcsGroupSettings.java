/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.grouping;

import java.util.Hashtable;
import java.util.HashSet;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Properties;
import java.io.*;

import org.openide.util.NbBundle;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.openide.TopManager;
import org.openide.util.Utilities;
import org.openide.util.SharedClassObject;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/** Options for VcsGroup feature
*
* @author Milos Kleint
*/
public class VcsGroupSettings extends SystemOption {


    public static final String PROP_AUTO_ADDITION = "autoAddition";//NOI18N
    public static final String PROP_SHOW_LINKS = "showLinks";//NOI18N
    public static final String PROP_DISABLE_GROUPS = "disableGroups";
    
    public static final int ADDITION_MANUAL = 0;
    public static final int ADDITION_ASK = 2;
    public static final int ADDITION_TO_DEFAULT = 1;

    
    
    static final long serialVersionUID = -4620483329253274979L;

    /** Holds value of property showLinks. */
    private static boolean showLinks = false;
    
    /** Holds value of property autoAddition. */
    private static int autoAddition = 0;

    /** Holds value of property disableGroups. */
    private boolean disableGroups = false;    
    
    public VcsGroupSettings() {
    }
    

    /** human presentable name */
    public String displayName() {
        return NbBundle.getBundle(VcsGroupSettings.class).getString("CTL_VcsGroup_settings"); // NOI18N
    }


    public HelpCtx getHelpCtx () {
        return new HelpCtx (VcsGroupSettings.class);
    }

    

    /** Getter for property showLinks.
     * @return Value of property showLinks.
     */
    public boolean isShowLinks() {
        return this.showLinks;
    }
    
    /** Setter for property showLinks.
     * @param showLinks New value of property showLinks.
     */
    public void setShowLinks(boolean show) {
        if (this.showLinks != show) {
            boolean old = showLinks;
            this.showLinks = show;
            firePropertyChange(PROP_SHOW_LINKS, new Boolean(old), new Boolean(showLinks));
        }
            
    }
    
    /** Getter for property autoAddition.
     * @return Value of property autoAddition.
     */
    public int getAutoAddition() {
        return this.autoAddition;
    }
    
    /** Setter for property autoAddition.
     * @param autoAddition New value of property autoAddition.
     */
    public void setAutoAddition(int autoAddition) {
        if (autoAddition != this.autoAddition) {
            int old = this.autoAddition;
            this.autoAddition = autoAddition;
            firePropertyChange(PROP_AUTO_ADDITION, new Integer(old), new Integer(autoAddition));
        }
    }
    
    /** Getter for property disableGroups.
     * @return Value of property disableGroups.
     */
    public boolean isDisableGroups() {
        return this.disableGroups;
    }
    
    /** Setter for property disableGroups.
     * @param disableGroups New value of property disableGroups.
     */
    public void setDisableGroups(boolean disableGroups) {
        if (this.disableGroups != disableGroups) {
            boolean old = this.disableGroups;
            this.disableGroups = disableGroups;
            firePropertyChange(PROP_DISABLE_GROUPS, new Boolean(old), new Boolean(disableGroups));
        }
    }
    
}