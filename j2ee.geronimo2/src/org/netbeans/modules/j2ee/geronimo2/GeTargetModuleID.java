/*
 * GeTargetModuleID.java
 * 
 * Created on Aug 30, 2007, 12:53:38 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.geronimo2;

import java.util.Vector;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;

/**
 *
 * @author ms159439
 */
public class GeTargetModuleID implements TargetModuleID {
private Target target;
    private String jar_name;
    private String context_url;
    private Vector childs = new Vector();
    private TargetModuleID  parent = null;
    
    public GeTargetModuleID(Target target) {
        this( target, "");
    }
    
    GeTargetModuleID(Target target, String jar_name) {
        this.target = target;
        this.setJARName(jar_name);
    }    
    
    public void setContextURL( String context_url) {
        this.context_url = context_url;
    }
    
    public void setJARName( String jar_name) {
        this.jar_name = jar_name;
    }
    
    public void setParent( GeTargetModuleID parent) {
        this.parent = parent;
        
    }
    
    public void addChild( GeTargetModuleID child) {
        childs.add( child );
        child.setParent( this );
    }
    
    public TargetModuleID[]  getChildTargetModuleID() {
        return (TargetModuleID[])childs.toArray(new TargetModuleID[childs.size()]);
    }
    
    //Retrieve a list of identifiers of the children of this deployed module.
    public String  getModuleID(){
        return jar_name;
    }
    
    //         Retrieve the id assigned to represent the deployed module.
    public TargetModuleID  getParentTargetModuleID() {
        return parent;
    }
    
    //Retrieve the identifier of the parent object of this deployed module.
    public Target  getTarget(){
        return target;
    }
    //Retrieve the name of the target server.
    public java.lang.String  getWebURL() {
        System.out.println("###");
        return context_url;//"http://" + module_id; //NOI18N
    }
    
    //If this TargetModulID represents a web module retrieve the URL for it.
    public java.lang.String  toString() {
        return getModuleID() +  hashCode();
    }
}
