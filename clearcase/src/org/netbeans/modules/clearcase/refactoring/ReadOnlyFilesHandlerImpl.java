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

package org.netbeans.modules.clearcase.refactoring;
import java.util.Collection;
import java.util.HashSet;
import java.util.WeakHashMap;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RefactoringSession;
import org.netbeans.modules.refactoring.spi.*;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Stupka
 */
public class ReadOnlyFilesHandlerImpl implements ReadOnlyFilesHandler {
    
    private WeakHashMap sessions = new WeakHashMap(2);
    /** Creates a new instance of ReadOnlyFilesHandlerImpl */
    public ReadOnlyFilesHandlerImpl() {
    }
    
    public Problem createProblem(RefactoringSession session, Collection files) {
        CheckoutFiles cof = (CheckoutFiles) sessions.get(session);
        Collection fileSet = null;
        if (cof != null) {
            //instance of CheckoutFiles created for this session, try to add files
            fileSet = new HashSet(cof.getFiles());
            if (!fileSet.addAll(files)) {
                // no files were added
                return null;
            }
        } else {
            // CheckoutFiles not found - create a new one
            fileSet = new HashSet(files);
        }
                 
        if(fileSet.size() == 0) {
            return null;
        }
        
        if (cof == null) {
            cof = new CheckoutFiles(fileSet); // XXX check the files status - it might be there is some another reason why it's r/o!
            sessions.put(session, cof);
            return new Problem(false, NbBundle.getMessage(ReadOnlyFilesHandlerImpl.class, "MSG_CheckoutWarning"), ProblemDetailsFactory.createProblemDetails(cof));
        } else {            
            cof.setFiles(files);
            return null;
        }
    }
}
