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

package org.netbeans.modules.latex.editor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.openide.util.HelpCtx;

import org.netbeans.modules.editor.options.BaseOptions;
import org.openide.util.NbBundle;

/**
* Options for the plain editor kit
*
* @author Miloslav Metelka
* @version 1.00
*/
public class TexOptions extends BaseOptions {

    public static final String PROP_FULL_SYNTACTIC_COLORING = "fullSyntacticColoring";
    public static final String PROP_LOCAL_CONNECTS_ONLY = "localConnectsOnly";
    public static final String PROP_REMOTE_HOST = "remoteHost";

    private ResourceBundle bundle = NbBundle.getBundle(TexOptions.class);
    
    public static final String TEX = "tex"; // NOI18N

    static final long serialVersionUID =-7082075147378689853L;

    private static final String HELP_ID = "editing.editor.tex"; // !!! NOI18N
    
    private boolean syntacticColoring = true;
    private boolean localConnectsOnly = true;
    private String  remoteHost        = "localhost";
    
    public TexOptions() {
        this(TexKit.class, TEX);
    }

    public TexOptions(Class kitClass, String typeName) {
        super(kitClass, typeName);
    }

    public HelpCtx getHelpCtx () {
        return new HelpCtx (HELP_ID);
    }
    
    protected String getString(String s) {
        return bundle.getString(s);
    }
    
    public boolean isFullSyntacticColoring() {
        return syntacticColoring;
    }
    
    public void setFullSyntacticColoring(boolean value) {
        this.syntacticColoring = value;
    }
    
    public boolean isLocalConnectsOnly() {
        return localConnectsOnly;
    }
    
    public void setLocalConnectsOnly(boolean value) {
        localConnectsOnly = value;
    }
    
    public String getRemoteHost() {
        return remoteHost;
    }
    
    public void setRemoteHost(String host) {
        remoteHost = host;
    }
    
}
