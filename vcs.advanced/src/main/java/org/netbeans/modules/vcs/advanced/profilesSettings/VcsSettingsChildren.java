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

package org.netbeans.modules.vcs.advanced.profilesSettings;

import java.beans.IntrospectionException;
import org.netbeans.modules.vcs.advanced.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import org.openide.actions.CopyAction;
import org.openide.actions.DeleteAction;

import org.openide.nodes.Children;
import org.openide.nodes.*;
import org.openide.cookies.FilterCookie;
import org.openide.filesystems.*;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataFilter;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;

/** Implements children for basic source code patterns
 * 
 * @author Richard Gregor
 */
public class VcsSettingsChildren extends Children.Keys implements PropertyChangeListener {    

    private ProfilesFactory factory;
    private static final String ICON_BASE =
    "org/netbeans/modules/vcs/advanced/profilesSettings/vcsSettings"; // NOI18N  
    
    private Comparator profileComparator = new ProfileComparator();

    /** Create pattern children. The children are initilay unfiltered.
     * @param elemrent the atteached class. For this class we recognize the patterns 
     */ 

    public VcsSettingsChildren() {
        super();
        debug("VcsSettingsChildren Init");        
        factory = ProfilesFactory.getDefault();

        // Add factory listener
        factory.addPropertyChangeListener(WeakListeners.propertyChange(this, factory));        
    }

    /** Called when the preparation of nodes is needed
     */
    protected void addNotify() {        
        setKeys (getVcsProfiles());        
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys( java.util.Collections.EMPTY_SET );
    }
    
    /** Creates nodes for given key.
    */
    protected Node[] createNodes( final Object key ) {
        debug("create nodes");       
        
        try {             
            String profileName = (String)key;
            Profile profile = factory.getProfile(profileName);
            if (profile.isLocalizedCopy()) {
                return new Node[0];
            }
            BeanNode node = new ProfileBeanNode(profile);
            node.setName(factory.getProfileDisplayName(profileName));
            //node.setIconBase(ICON_BASE);            
            return new Node[] { node };                        
        }
        catch ( IntrospectionException e ) {
            // No node will be created
        }

        return new Node[0];
    }
    
    private String[] getVcsProfiles() {           
        debug("getVcsProfiles");
        String profileNames[] = factory.getProfilesNames();
        Arrays.sort(profileNames, profileComparator);
        return profileNames;
    }

    void refreshAll( ) {
        setKeys ( getVcsProfiles() );
    }
    
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     *
     */
    public void propertyChange(PropertyChangeEvent evt) {
       if(evt.getPropertyName().equals(ProfilesFactory.PROP_PROFILE_ADDED))
            setKeys(getVcsProfiles());
        else if(evt.getPropertyName().equals(ProfilesFactory.PROP_PROFILE_REMOVED))
            setKeys(getVcsProfiles());
    } 

    
    private boolean debug = false;
    private void debug(String msg){
        if(debug)
            System.err.println("VcsSettingsChildren: "+msg);
    }
    
    private static class ProfileBeanNode extends BeanNode {
        
        private Profile profile;
        
        public ProfileBeanNode(Profile profile) throws IntrospectionException {
            super(profile);
            this.profile = profile;
        }
        
        protected SystemAction[] createActions () {
            return new SystemAction[] {
                SystemAction.get(CopyAction.class),
                null,
                SystemAction.get(DeleteAction.class)
            };
        }
        
        public void destroy() throws java.io.IOException {
            ProfilesFactory.getDefault().removeProfile(profile.getName());
            super.destroy();
        }

    }
    
    /**
     * Compare the profiles according to their display names (if any).
     */
    private static final class ProfileComparator extends Object implements Comparator {
        
        public int compare(Object o1, Object o2) {
            String name1 = (String) o1;
            String name2 = (String) o2;
            String displayName1 = ProfilesFactory.getDefault().getProfileDisplayName(name1);
            String displayName2 = ProfilesFactory.getDefault().getProfileDisplayName(name2);
            if (displayName1 != null && displayName2 != null) {
                return displayName1.compareTo(displayName2);
            } else {
                return name1.compareTo(name2);
            }
        }
        
    }
    
}
