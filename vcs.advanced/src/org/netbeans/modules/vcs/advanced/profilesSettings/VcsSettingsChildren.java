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
import org.openide.util.WeakListener;
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
        factory.addPropertyChangeListener(WeakListener.propertyChange(this, factory));        
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
