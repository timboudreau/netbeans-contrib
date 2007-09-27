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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.zeroadmin.test;

import javax.naming.*;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.ErrorManager;

/**
 * Action creating some number of the TestSetting instances.
 *
 * @author David Strupl
 */
public class TestAction extends CallableSystemAction {

    private static int number;
    /** Cache the reference to the initial context*/
    private Context incon;

    public void performAction() {
        try {
            int n = getNumber();
            Context c = getInitialContext();
            for (int i = 0; i < n; i++) {
                TestSetting ts = new TestSetting();
                ts.setSize(n);
                String name="test" + i;
                boolean foundAvailable = false;
                while (! foundAvailable) {
                    try {
                        c.lookup(name);
                        name += i;
                        if (name.length() > 1000000) {
                            throw new IllegalStateException();
                        }
                    } catch (NameNotFoundException nnfe) {
                        foundAvailable = true;
                    }
                }
                c.bind(name, ts);
            }
        } catch (Exception x) {
            ErrorManager.getDefault().notify(x);
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(TestAction.class, "LBL_Action");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/zeroadmin/test/TestActionIcon.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx(TestAction.class);
    }

    private static int getNumber() {
        if (number != 0) {
            return number;
        }
        try {
            String s = System.getProperty("test.setting.size");
            if (s != null) {
                number = Integer.parseInt(s);
            } else {
                number = 10;
            }
        } catch (Exception x) {
            ErrorManager.getDefault().notify(x);
        }
        return number;
    }
    
    /**
     * Lazy initialization of the initial context
     */
    private Context getInitialContext() throws NamingException {
        if (incon == null) {
            incon = (Context)new InitialContext().lookup("nbres:/");
        }
        return incon;
    }

}
