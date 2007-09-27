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

package org.netbeans.modules.corba.idl.src;

import java.util.Vector;

import org.netbeans.*;

/*
 * @author Karel Gardas
 */

public class IDLType {

    public String name;
    public int type;

    // for sequences and strings
    public IDLType of_type;
    public String of_name;
    public Vector dim;

    public static final int VOID = 0;
    public static final int BOOLEAN = 1;
    public static final int CHAR = 2;
    public static final int OCTET = 3;
    public static final int STRING = 4;
    public static final int SHORT = 5;
    public static final int USHORT = 6;
    public static final int LONG = 7;
    public static final int ULONG = 8;
    public static final int LONGLONG = 9;
    public static final int ULONGLONG = 10;
    public static final int FLOAT = 11;
    public static final int DOUBLE = 12;
    public static final int LONGDOUBLE = 13;
    public static final int FIXED = 14;

    // constructed
    public static final int ENUM = 100;
    public static final int STRUCT = 101;
    public static final int UNION = 102;

    // template
    public static final int SEQUENCE = 200;

    // scoped names
    public static final int SCOPED = 250;
    public static final int ASCOPED = 251;

    // interface
    public static final int INTERFACE = 300;


    // any
    public static final int ANY = 500;


    // CORBA 2.2 types
    public static final int WCHAR = 1000;
    public static final int WSTRING = 1010;
    public static final int NATIVE = 1020;

    // COBRA Object
    public static final int OBJECT = 2000;

    // CORBA 2.3 types
    public static final int VALUEBASE = 3000;

    public IDLType () {
        dim = new Vector ();
    }

    public IDLType (String _name) {
        name = _name;
        dim = new Vector ();
    }

    public IDLType (int _type, String _name) {
        type = _type;
        name = _name;
        dim = new Vector ();
    }

    public IDLType (int _type, String _name, IDLType _of_type, Vector _dim) {
        type = _type;
        name = _name;
        of_type = _of_type;
        dim = _dim;
    }

    public String getName () {
        return name;
    }

    public int getType () {
        return type;
    }

    public IDLType ofType () {
        return of_type;
    }

    public Vector ofDimension () {
        return dim;
    }

    public void setDimension (Vector val) {
        dim = val;
    }

    public String toString () {
        return type + ":" + name + "-<" + of_type + ">"; // NOI18N
    }

}

/*
 * $Log
 * $
 */
