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

//package org.netbeans.modules.corba;
package org.netbeans.modules.corba;
import org.openide.loaders.DataObject;
import org.openide.compiler.CompilerType;
import org.openide.compiler.CompilerJob;

/**
*
* @author Karel Gardas
*/
public class IDLCompilerType extends CompilerType {

    static final long serialVersionUID =-8389299857638878014L;

    //public static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    /** Prepare a data object for compilation.
    * Implementations should create an instance of a
    * suitable subclass of {@link Compiler}, passing
    * the compiler job to the constructor so that the job may
    * register the compiler.
    *
    * @param job compiler job to add compilers to
    * @param type the type of compilation task to manage
    * ({@link org.openide.cookies.CompilationCookie.Compile}, etc.)
    * @param obj data object to prepare for compilation
    */

    public void prepareJob (CompilerJob job, Class type, DataObject obj) {
        if (DEBUG)
            System.out.println ("IDLCompilerType::prepareJob (...)"); // NOI18N
        if (obj instanceof IDLDataObject)
            ((IDLDataObject)obj).createCompiler(job, type);
    }
}

/*
* <<Log>>
*  7    Gandalf   1.6         2/8/00   Karel Gardas    
*  6    Gandalf   1.5         11/27/99 Patrik Knakal   
*  5    Gandalf   1.4         11/4/99  Karel Gardas    - update from CVS
*  4    Gandalf   1.3         11/4/99  Karel Gardas    update from CVS
*  3    Gandalf   1.2         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems copyright in file comment
*  2    Gandalf   1.1         10/5/99  Karel Gardas    
*  1    Gandalf   1.0         10/5/99  Karel Gardas    initial revision
* $
*/
