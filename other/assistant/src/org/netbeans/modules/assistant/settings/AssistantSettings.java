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
