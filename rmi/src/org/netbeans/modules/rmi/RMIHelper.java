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

package org.netbeans.modules.rmi;

import java.util.*;
import org.openide.src.*;

/**
 *
 * @author  mryzl
 */

public class RMIHelper extends Object {

    /** Name of the remote interface. */
    public static final String REMOTE = "java.rmi.Remote"; // NOI18N

    /** Creates new RMIHelper. */
    public RMIHelper() {
    }

    /** Test if ce1 implements given class or if interface ce1 extends given class.
    * @param ce1 - class element
    * @param classname - name of the class that ce1 could implement
    */
    public static boolean implementsClass(ClassElement ce1, String classname) {
        Identifier cn = Identifier.create(classname);
        if (ce1.getName().equals(cn)) return true;

        ArrayList list = new ArrayList();
        Set done = new HashSet();

        Identifier[] ids = ce1.getInterfaces();
        putInterfaces(list, done, ids);
        while (!list.isEmpty()) {
            int len = list.size();
            Identifier id = (Identifier) list.get(len - 1);
            list.remove(len - 1);
            if (id.equals(cn)) return true;
            ClassElement ce2 = ClassElement.forName(id.getFullName());
            if (ce2 != null) {
                Identifier[] ids2 = ce2.getInterfaces();
                putInterfaces(list, done, ids2);
            }
        }
        return false;
    }

    /** Add interfaces to the FIFO.
    */
    private static void putInterfaces(List list, Set done, Identifier[] ids) {
        for(int i = 0; i < ids.length; i++) {
            if (done.add(ids[i])) {
                list.add(ids[i]);
            }
        }
    }
}

/*
* <<Log>>
*  3    Gandalf-post-FCS1.1.1.0     3/20/00  Martin Ryzl     localization
*  2    Gandalf   1.1         1/28/00  Martin Ryzl     
*  1    Gandalf   1.0         1/24/00  Martin Ryzl     
* $ 
*/ 
