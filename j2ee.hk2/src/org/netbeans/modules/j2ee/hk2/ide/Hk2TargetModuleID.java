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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
        return ((Hk2Target)target).getServerUri () + path.replaceAll(" ", "%20");

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