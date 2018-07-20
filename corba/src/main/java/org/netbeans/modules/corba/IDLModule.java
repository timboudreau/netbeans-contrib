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

package org.netbeans.modules.corba;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.*;

import org.openide.compiler.Compiler;
import org.openide.compiler.CompilerJob;
import org.openide.modules.ModuleInstall;
import org.openide.loaders.DataObject;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileSystem;
import org.openide.TopManager;
import org.openide.loaders.DataFolder;
import org.openide.loaders.XMLDataObject;


/**
* Module installation class for IDLDataObject.
*
* @author Karel Gardas
*/
public class IDLModule extends ModuleInstall {

    static final long serialVersionUID =8847247042163099527L;

    private static final boolean DEBUG = false;
    //private static final boolean DEBUG = true;

    private static final String PUBLIC_ID = "-//Forte for Java//DTD ORBSettings 1.0//EN"; // NOI18N
    private static final String PATH = "org/netbeans/modules/corba/resources/impls/ORBSettings.dtd"; // NOI18N

    static {
	XMLDataObject.registerCatalogEntry (PUBLIC_ID, PATH, IDLModule.class.getClassLoader ());
    }
    /** Module installed for the first time. */
    public void installed() {
        this.restored ();
    }


    /** Module installed again. */
    public void restored() {
        if (DEBUG)
            System.out.println ("CORBA Support Module restoring..."); // NOI18N
        if (DEBUG)
            System.out.println ("CORBA Support Module restored..."); // NOI18N
    }
    
    /** Called when module is uninstalled
     *  Removes CorbaWizardAction
     */
    public void uninstalled () {
    }

    private String getClasspath(String[] classpathItems) {
        return null;
    }

    private static final String getSystemEntries() {
        return null;
    }

}

/*
 * <<Log>>
 *  27   Jaga      1.25.1.0    3/16/00  Miloslav Metelka removed unused import
 *  26   Gandalf   1.25        2/9/00   Karel Gardas    
 *  25   Gandalf   1.24        2/8/00   Karel Gardas    
 *  24   Gandalf   1.23        11/27/99 Patrik Knakal   
 *  23   Gandalf   1.22        11/9/99  Karel Gardas    - updated for new IDL 
 *       Editor Stuff
 *  22   Gandalf   1.21        11/4/99  Karel Gardas    - update from CVS
 *  21   Gandalf   1.20        11/4/99  Karel Gardas    update from CVS
 *  20   Gandalf   1.19        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  19   Gandalf   1.18        10/5/99  Karel Gardas    
 *  18   Gandalf   1.17        10/1/99  Petr Hrebejk    org.openide.modules.ModuleInstall
 *        changed to class + some methods added
 *  17   Gandalf   1.16        10/1/99  Karel Gardas    updates from CVS
 *  16   Gandalf   1.15        9/13/99  Jaroslav Tulach 
 *  15   Gandalf   1.14        8/7/99   Karel Gardas    changes in code which 
 *       hide generated files
 *  14   Gandalf   1.13        8/3/99   Karel Gardas    
 *  13   Gandalf   1.12        6/10/99  Ian Formanek    Modified copying 
 *       templates and impls on install
 *  12   Gandalf   1.11        6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  11   Gandalf   1.10        6/4/99   Karel Gardas    
 *  10   Gandalf   1.9         6/4/99   Karel Gardas    
 *  9    Gandalf   1.8         6/4/99   Karel Gardas    
 *  8    Gandalf   1.7         5/28/99  Karel Gardas    
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/28/99  Karel Gardas    
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */



