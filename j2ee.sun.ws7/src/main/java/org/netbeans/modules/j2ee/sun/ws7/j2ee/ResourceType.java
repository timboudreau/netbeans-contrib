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
