/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.erd.wizard;

import java.beans.*;
import java.util.LinkedList;
import java.util.Vector;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.modules.dbschema.SchemaElement;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.netbeans.modules.dbschema.jdbcimpl.ConnectionProvider;

public class WizardContext {



    private boolean isConnection;

    private String url;
    private DataFolder targetFolder;
    private String erd;
    private String projectDir;
    
    
    public void setProjectDir(String projectDir){
        this.projectDir=projectDir;
    }
    
    public void setUrl(String url){
        this.url=url;
    }
    
    public String getUrl(){
        if(!isConnection){
            url=url.substring(projectDir.length());
        }
        return url;
    }
    
    public boolean isConnection(){
        return isConnection;
    }
    
    public void setIsConnection(boolean isConnection){
        this.isConnection=isConnection;
    }
     
    public void setTargetFolder(DataFolder tf){
        targetFolder=tf;
    }
    
    public DataFolder getTargetFolder(){
        return targetFolder;
    }
    
    public void setERDFile(String erdFile){
        this.erd=erdFile;
    }
    
    public String getERDFile(){
        return erd;
    }
}
