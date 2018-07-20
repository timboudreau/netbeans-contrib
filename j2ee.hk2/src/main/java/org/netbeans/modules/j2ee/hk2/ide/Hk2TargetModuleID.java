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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.j2ee.hk2.ide;

import java.util.Vector;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;

/**
 *
 * @author ludo
 */
public class Hk2TargetModuleID implements TargetModuleID {
    private Target target;
    private String docBaseURI;
    private String context_url;
    private Vector childs = new Vector();
    private TargetModuleID  parent = null;
    private String path =null;
    

    

    
    Hk2TargetModuleID(Target target, String docBaseURI, String path) {
        this.target = target;
        this.docBaseURI=docBaseURI;
        this.path =path;
    }    
    

    
    public void setParent( Hk2TargetModuleID parent) {
        this.parent = parent;
        
    }
    
    public void addChild( Hk2TargetModuleID child) {
        childs.add( child );
        child.setParent( this );
    }
    
    public TargetModuleID[]  getChildTargetModuleID() {
        return (TargetModuleID[])childs.toArray(new TargetModuleID[childs.size()]);
    }
    
    //Retrieve a list of identifiers of the children of this deployed module.
    public String  getModuleID(){
        return docBaseURI;
    }
    
    //         Retrieve the id assigned to represent the deployed module.
    public TargetModuleID  getParentTargetModuleID() {
        return parent;
    }
    
    //Retrieve the identifier of the parent object of this deployed module.
    public Target  getTarget(){
        return target;
    }
    
    
    public String getWebURL () {
        return ((Hk2Target)target).getServerUri () +"/"+ path.replaceAll(" ", "%20");

    }
    
    public String toString () {
        return getModuleID ();
    }
    
    public String getPath(){
        return docBaseURI;
    }
     
    
    public String getDocBaseURI(){
        return docBaseURI;
    }
}