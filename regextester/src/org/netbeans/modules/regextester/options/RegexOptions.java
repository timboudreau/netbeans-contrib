/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.regextester.options;

import java.util.MissingResourceException;
import org.netbeans.modules.editor.options.BaseOptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.modules.regextester.editor.RegexEditorKit;

/**
 *
 * @author Geertjan
 */
public class RegexOptions extends BaseOptions {
    
    public static String REGEX = "REGEX"; // NOI18N
    
    /** Name of property. */
    private static final String HELP_ID = "editing.editor.regex"; // NOI18N
    
    //no regex specific options at this time
    static final String[] REGEX_PROP_NAMES = new String[] {};
    
    
    public RegexOptions() {
        super(RegexEditorKit.class, REGEX);
    }
    
    /**
     * Determines the class of the default indentation engine, in this case
     * ManifestIndentEngine.class
     */
//    protected Class getDefaultIndentEngineClass() {
//        return ManifestIndentEngine.class;
//    }
    
    /**
     * Gets the help ID
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(HELP_ID);
    }
    
    /**
     * Look up a resource bundle message, if it is not found locally defer to
     * the super implementation
     */
    protected String getString(String key) {
        try {
            return NbBundle.getMessage(RegexOptions.class, key);
        } catch (MissingResourceException e) {
            return super.getString(key);
        }
    }
    
}