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

import org.netbeans.modules.vcs.advanced.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.ArrayList;

import org.openide.nodes.Children;
import org.openide.nodes.*;
import org.openide.cookies.FilterCookie;
import org.openide.filesystems.*;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataFilter;
import org.openide.util.WeakListener;

/** Implements children for basic source code patterns
 * 
 * @author Richard Gregor
 */
public class VcsSettingsChildren extends Children.Keys implements PropertyChangeListener {    

    private ProfilesFactory factory;
    private static final String ICON_BASE =
    "org/netbeans/modules/vcs/advanced/profilesSettings/vcsSettings"; // NOI18N  
    
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
            BeanNode node = new BeanNode(profile);
            node.setName(factory.getProfileDisplayName(profileName));
            //node.setIconBase(ICON_BASE);            
            return new Node[] { node };                        
        }
        catch ( java.beans.IntrospectionException e ) {
            // No node will be created
        }

        return new Node[0];
    }
    
    private Collection getVcsProfiles() {           
        debug("getVcsProfiles");
        ArrayList vcsProfiles = new ArrayList();
        String profileName[] = factory.getProfilesNames();         
        for(int i = 0; i < profileName.length; i++){  
            vcsProfiles.add(profileName[i]);
        }
        return vcsProfiles;
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
    
}
