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

package org.netbeans.spi.looks;

import javax.swing.Action;
import java.beans.BeanInfo;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import org.netbeans.modules.looks.RegistryBridge;
import org.openide.util.Utilities;
import org.openide.util.Lookup;

/**
 * Utility class for providing Looks. Some methods are linked together to
 * make it easier for subclasses to implement reasonable functionality
 * (iconBase, actionBase), etc.
 *
 * @author Jaroslav Tulach
 */
public abstract class DefaultLook extends Look {
    /** messages to create a resource identification for each type of
    * icon from the base name for the icon.
    */
    private static final MessageFormat[] icons = {
        // color 16x16
        new MessageFormat ("{0}.gif"), // NOI18N
        // color 32x32
        new MessageFormat ("{0}32.gif"), // NOI18N
        // mono 16x16
        new MessageFormat ("{0}.gif"), // NOI18N
        // mono 32x32
        new MessageFormat ("{0}32.gif"), // NOI18N
        // opened color 16x16
        new MessageFormat ("{0}Open.gif"), // NOI18N
        // opened color 32x32
        new MessageFormat ("{0}Open32.gif"), // NOI18N
        // opened mono 16x16
        new MessageFormat ("{0}Open.gif"), // NOI18N
        // opened mono 32x32
        new MessageFormat ("{0}Open32.gif"), // NOI18N
    };
    /** To index normal icon from previous array use
    *  + ICON_BASE.
    */
    private static final int ICON_BASE = -1;
    /** for indexing opened icons */
    private static final int OPENED_ICON_BASE = 3;

    private RegistryBridge registryBridge;
    
    /** Creates new instance of look does no work.
     * @param name the name to assign to the look
     */
    public DefaultLook(String name) {
        this( RegistryBridge.getDefault( null ), name );
    }
    
    DefaultLook(RegistryBridge registryBridge, String name) {
        super( name );
        this.registryBridge = registryBridge;
    }
    
    // Methods for STYLE -------------------------------------------------------

    /** Finds icon using the value returned from <link>iconBase</link>
     * @param representedObject Parameter is ignored.
     * @param type Icon type constant from {@link java.beans.BeanInfo}
     * @param env Parameter is ignored.
     * @return an icon or <CODE>null</CODE> if not found
     */
    public java.awt.Image getIcon(Object representedObject, int type, Lookup env ) {
        return findIcon ( representedObject, type, ICON_BASE, env);
    }

    /** Finds icon using the value returned from <link>iconBase</link>
     * @param representedObject Parameter is ignored.
     * @param type Icon type constant from {@link java.beans.BeanInfo}
     * @param env Parameter is ignored.
     * @return an icon or <CODE>null</CODE> if not found
     */
    public java.awt.Image getOpenedIcon(Object representedObject, int type, Lookup env ) {
        return findIcon ( representedObject, type, OPENED_ICON_BASE, env);
    }

    // Methods for ACTIONS & NEW TYPES -----------------------------------------

    /** Calls actionBase (substitute, false) and extracts actions
     * from that context.
     * @param representedObject Parameter is ignored.
     * @param env Parameter is ignored.
     * @return the actions at the context or <CODE>null</CODE>
     */
    public Action[] getActions(Object representedObject, Lookup env ) {
        return actionsForContext (registryBridge, actionBase (representedObject, false, env ));
    }

    /** Calls actionBase (substitute, true) and extracts actions
     * from that context.
     * @param representedObject Parameter is ignored.
     * @param env Parameter is ignored.
     * @return the actions at the context or <CODE>null</CODE>
     */
    public Action[] getContextActions(Object representedObject, Lookup env ) {
        return actionsForContext (registryBridge, actionBase (representedObject, true, env ));
    }

    /** Extracts the first action from getActions, if any.
     * @param representedObject Parameter is ignored.
     * @param env Parameter is ignored.
     * @return the action or <CODE>null</CODE>
     */
    public Action getDefaultAction(Object representedObject, Lookup env ) {
        Action[] arr = getActions (representedObject, env );
        return arr != null && arr.length > 0 ? arr[0] : null;
    }

    // Methods for PROPERTIES AND CUSTOMIZER -----------------------------------

    /** Check whether the customizer for the represented object is available.
     * I.e. whether
     * getCustomizer (substitute) returns non-null value
     * @param representedObject Parameter is ignored.
     * @param env Parameter is ignored.
     * @return true if the customizer is available, false otherwise
     */
    public boolean hasCustomizer(Object representedObject, Lookup env ) {
        return getCustomizer (representedObject, env ) != null;
    }


    // Icon management ---------------------------------------------------------

    /** Allows subclasses to specify an icon in easier way without need to
     * load the actual objects.
     *
     * <p>For example, if the returned base is <code>/resource/MyIcon</code>, the
     * following images may be used according to the icon state and
     * {@link java.beans.BeanInfo#getIcon presentation type}:
     *
     * <ul><li><code>resource/MyIcon.gif</code><li><code>resource/MyIconOpen.gif</code>
     * <li><code>resource/MyIcon32.gif</code><li><code>resource/MyIconOpen32.gif</code></ul>
     *
     * <P>
     * The default implementation returns null.
     * @param representedObject The substitute to locate icon for.
     * @param env Environement of the object.
     * @return the base for icon search (no initial slash) or <code>null</code> if this look does not provide an icon
     */
    protected String iconBase ( Object representedObject, Lookup env ) {
        return null;
    }

    /** Allows subclasses to specify actions in a easy way - by providing a
    * name of a context name where to find the javax.swing.Action objects.
    *
    * <p>By default the method returns name of this class (separated by slashes)
    * with a prefix Looks/Actions. So for a class <code>org.nb.mymod.MyLook</code>
    * the default action context is <em>Looks/Actions/org/nb/mymod/MyLook</em>.
    * As a result it is not necessary to override this method in many cases.
    *
    * @param representedObject The object to work on.
    * @param context false if <code>getActions</code> was called,
    *    true if <code>getContextActions</code> was called
    * @param env Environement for the object.
    * @return the name of a context
    */
    protected String actionBase ( Object representedObject, boolean context, Lookup env ) {
        return "Looks/Actions/" + getClass ().getName ().replace ('.', '/');  // NOI18N
    }

    // Private methods ---------------------------------------------------------

    /** Reads actions from a context.
     * @param name of the context.
     * @return array of actions
     */
    private static Action[] actionsForContext (RegistryBridge  registryBridge, String name) {        
        Enumeration en = registryBridge.getObjects(name, null);
        if (!en.hasMoreElements ()) {
            return null;
        }

        ArrayList arr = new ArrayList ();
        while (en.hasMoreElements()) {
            arr.add (en.nextElement ());
        }

        return (Action[])arr.toArray (new Action[arr.size ()]);        
    }

    /** Tries to find the right icon for the iconbase.
    * @param type type of icon (from BeanInfo constants)
    * @param ib base where to scan in the array
    * @return icon or null
    */
    private java.awt.Image findIcon ( Object representedObject, int type, int ib, Lookup env) {
        String[] base = { iconBase (representedObject, env ) };
        if (base[0] == null) {
            return null;
        }

        String res = icons[type + ib].format (base);
        java.awt.Image im = Utilities.loadImage (res);

        if (im != null) return im;

        // try the first icon
        res = icons[BeanInfo.ICON_COLOR_16x16 + ib].format (base);

        im = Utilities.loadImage (res);

        if (im != null) return im;

        if (ib == OPENED_ICON_BASE) {
            // try closed icon also
            return findIcon (representedObject, type, ICON_BASE, env);
        }

        // if still not found return default icon
        return null;
    }

}
