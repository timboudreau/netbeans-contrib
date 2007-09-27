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
