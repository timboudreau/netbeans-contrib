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

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openide.ErrorManager;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * This command gives the regular expression of ignored files and relevant files
 * on the data output.
 *
 * @author  Martin Entlicher
 */
public class IgnoreAndRelevantListExpressions extends Object implements VcsAdditionalCommand {
    
    /** Creates a new instance of IgnoreAndRelevantListExpressions */
    public IgnoreAndRelevantListExpressions() {
    }
    
    public static void createMaskRegularExpressions(String relevantMasks,
                                                    Pattern[] regExpPositivePtr,
                                                    Pattern[] regExpNegativePtr) {
        if (relevantMasks != null) {
            List ignoreListPositive = new ArrayList();
            List ignoreListNegative = new ArrayList();
            String[] masks = VcsUtilities.getQuotedArguments(relevantMasks);
            for (int i = 0; i < masks.length; i++) {
                if (masks[i].startsWith("!")) {
                    ignoreListNegative.add(masks[i].substring(1));
                } else {
                    ignoreListPositive.add(masks[i]);
                }
            }
            if (ignoreListPositive.size() > 0) {
                String unionExp = VcsUtilities.computeRegularExpressionFromIgnoreList(ignoreListPositive);
                try {
                    regExpPositivePtr[0] = Pattern.compile(unionExp);
                    //System.out.println(" **** GOT positive reg EXP: '"+regExpPositivePtr[0]+"' *********");
                } catch (PatternSyntaxException malformedRE) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, malformedRE);
                }
            }
            if (ignoreListNegative.size() > 0) {
                String unionExp = VcsUtilities.computeRegularExpressionFromIgnoreList(ignoreListNegative);
                try {
                    regExpNegativePtr[0] = Pattern.compile(unionExp);
                    //System.out.println(" **** GOT negative reg EXP: '"+regExpNegativePtr[0]+"' *********");
                } catch (PatternSyntaxException malformedRE) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, malformedRE);
                }
            }
        }
    }
    
    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *         false if some error occured.
     */
    public boolean exec(final Hashtable vars, final String[] args,
                        final CommandOutputListener stdoutListener, final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, final String dataRegex,
                        final CommandDataOutputListener stderrDataListener, final String errorRegex) {
        
        String ssDir = (String) vars.get("ENVIRONMENT_VAR_SSDIR"); // NOI18N
        String userName = (String) vars.get("USER_NAME"); // NOI18N
        if (userName == null || userName.length() == 0) {
            userName = System.getProperty("user.name");
        }
        String relevantMasks;
        try {
            relevantMasks = GetInitializationVariable.getVariable(ssDir, userName, GetAdjustedRelevantMasks.RELEVANT_MASKS);
        } catch (IOException ioex) {
            stderrListener.outputLine(ioex.getLocalizedMessage());
            relevantMasks = null;
            return false;
        }
        String ignoreList = null;
        String relevantList = null;
        if (relevantMasks != null) {
            List ignoreListPositive = new ArrayList();
            List ignoreListNegative = new ArrayList();
            String[] masks = VcsUtilities.getQuotedStrings(relevantMasks);
            for (int i = 0; i < masks.length; i++) {
                if (masks[i].startsWith("!")) {
                    ignoreListNegative.add(masks[i].substring(1));
                } else {
                    ignoreListPositive.add(masks[i]);
                }
            }
            if (ignoreListPositive.size() > 0) {
                relevantList = VcsUtilities.computeRegularExpressionFromIgnoreList(ignoreListPositive);
            }
            if (ignoreListNegative.size() > 0) {
                ignoreList = VcsUtilities.computeRegularExpressionFromIgnoreList(ignoreListNegative);
            }
        }
        if (ignoreList != null || relevantList != null) {
            stdoutDataListener.outputData(new String[] { ignoreList, relevantList });
        }
        return true;
    }
}
