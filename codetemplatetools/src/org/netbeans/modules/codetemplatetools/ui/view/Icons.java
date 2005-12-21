/*
 * Icons.java
 *
 * Created on December 21, 2005, 1:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.codetemplatetools.ui.view;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.util.Utilities;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class Icons {
    public static final ImageIcon NEW_TEMPLATE_ICON           = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/newtemplate.gif")); // NOI18N
    public static final ImageIcon SHOW_TEMPLATES_ICON         = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/templates.gif")); // NOI18N
    public static final ImageIcon TEMPLATE_ICON               = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/template.gif")); // NOI18N
    public static final ImageIcon TEMPLATE_FOR_SELECTION_ICON = new ImageIcon(Utilities.loadImage("org/netbeans/modules/codetemplatetools/resources/templateforselection.gif")); // NOI18N
}