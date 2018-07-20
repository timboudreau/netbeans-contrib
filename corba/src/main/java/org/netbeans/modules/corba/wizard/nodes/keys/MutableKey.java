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

package org.netbeans.modules.corba.wizard.nodes.keys;

/**
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
public class MutableKey extends Object {

    public final static int MODULE    = 1;
    public final static int INTERFACE = 2;
    public final static int OPERATION = 3;
    public final static int ATTRIBUTE = 4;
    public final static int CONSTANT  = 5;
    public final static int ALIAS     = 6;
    public final static int EXCEPTION = 7;
    public final static int STRUCT    = 8;
    public final static int UNION     = 9;
    public final static int ENUM      = 10;
    public final static int UNION_MBR = 11;
    public final static int STRUCT_MBR = 12;
    public final static int ENUM_MBR = 13;
    public final static int FORWARD_DCL = 14;
    public final static int VALUE_BOX = 15;
    public final static int VALUETYPE = 16;
    public final static int VALUE = 17;
    public final static int VALUE_FACTORY = 18;

    private int kind;  
  
    /** Creates new MutableKey */
    public MutableKey (int kind) {
        this.kind = kind;
    }
  
    public int kind () {
        return this.kind;
    }
  
    public String toString () {
        return "org.netbeans.modules.corba.wizard.nodes.keys.MutableKey:"+kind;  // No I18N
    }
  
}
