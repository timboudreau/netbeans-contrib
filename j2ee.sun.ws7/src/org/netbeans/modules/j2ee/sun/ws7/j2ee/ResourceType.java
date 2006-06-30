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
