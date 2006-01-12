/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * ResourceType.java
 */

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

/**
 *
 * @author Administrator
 */
public class ResourceType {
    
    private static final int ENUM_JDBC=1;
    private static final int ENUM_EXT_JNDI=2;
    private static final int ENUM_CUSTOM=3;
    private static final int ENUM_MAIL=4;
    
    public static final ResourceType JDBC =new ResourceType(ENUM_JDBC);
    public static final ResourceType JNDI =new ResourceType(ENUM_EXT_JNDI);
    public static final ResourceType CUSTOM =new ResourceType(ENUM_CUSTOM);
    public static final ResourceType MAIL =new ResourceType(ENUM_MAIL);
    
    private int type;
    
    public ResourceType(int type) {
        this.type = type;
    }
    
    public int getType(){
        return type;
    }
    public boolean eqauls(ResourceType type){
        if(this.type == type.getType()){
            return true;
        }
        return false;
    }
    
    public String toString(){
        if(type==ENUM_JDBC){
            return "jdbc-resource";//No I18N
        }
        if(type==ENUM_EXT_JNDI){
            return "external-jndi-resource";//No I18N
        }
        if(type==ENUM_CUSTOM){
            return "custom-resource";//No I18N
        }
        if(type==ENUM_MAIL){
            return "mail-resource";//No I18N
        }
        
        return "UNKNOWN";//No I18N
    }    
}
