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

package org.netbeans.modules.assistant.settings;

import org.openide.options.*;
import org.openide.util.NbBundle;
import java.io.File;
import java.util.LinkedList;

/**
 * 
 * @author  Richard Gregor
 */
public class AssistantSettings extends ContextSystemOption {    
    public static final String PROP_LINKS_PER_CATEGORY = "linksNumber"; // NOI18N
    public static final String PROP_HELP_CONTENT      = "helpContent"; // NOI18N
    public static final String PROP_SEARCH_TYPE       = "searchType"; // NOI18N
   
    public static final int SEARCH_KEYWORDS = 0;
    public static final int SEARCH_FULL_TEXT = 1;  
    
    public static final int CONTENT_EDITOR = 2;
    public static final int CONTENT_HELP = 3;
      
    static final long serialVersionUID = -3279219341164367270L;
    
    /** Initialize shared state.
     * Should use {@link #putProperty} to set up variables.
     * Subclasses should always call the super method.
     * <p>This method need <em>not</em> be called explicitly; it will be called once
     * the first time a given shared class is used (not for each instance!).
     */
    protected void initialize() {
        super.initialize();  
        setLinksNumber(10);
        setHelpContent(CONTENT_EDITOR);
        setSearchType(SEARCH_FULL_TEXT);        
    }    
    
    /** Get human presentable name */
    public String displayName() {
        return NbBundle.getBundle(AssistantSettings.class).getString("CTL_AssistantsSettings");
    }
    
    public void setLinksNumber(int num){
        putProperty(PROP_LINKS_PER_CATEGORY,new Integer(num),true);
    }
    
    public int getLinksNumber(){
        int num = ((Integer)getProperty(PROP_LINKS_PER_CATEGORY)).intValue();
        return num;
    }
    
    public void setHelpContent(int cont){        
        putProperty(PROP_HELP_CONTENT,(cont == CONTENT_EDITOR)? new Integer(CONTENT_EDITOR):new Integer(CONTENT_HELP),true) ;
    }
    
    public int getHelpContent(){
        return ((Integer)getProperty(PROP_HELP_CONTENT)).intValue();
    }
    
    public void setSearchType(int type){
        putProperty(PROP_SEARCH_TYPE,(type == SEARCH_FULL_TEXT)? new Integer(SEARCH_FULL_TEXT):new Integer(SEARCH_KEYWORDS),true);
    }

    public int getSearchType(){
        return((Integer)getProperty(PROP_SEARCH_TYPE)).intValue();
    }
    
}
