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

package org.netbeans.modules.vcscore.cmdline;

import java.util.*;

import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

/**
 * An abstract recursive list command, that every class has to implement
 * to provide a recursive listing information.
 *
 * @author  Martin Entlicher
 */
public abstract class VcsListRecursiveCommand extends Object {


    /**
     * This method is called when the content of the directory is to be listed recursively.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param filesByName return the files read from the directory content. Each element in
     *                    <code>VcsDirContainer</code> is supposed to be <code>Hashtable</code> object.
     *                    The keys in this Hashtable
     *                    are supposed to be file names, values are supposed to be an array
     *                    of String containing statuses.
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                           satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                           satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *         false if some error occured.
     */
    public abstract boolean listRecursively(Hashtable vars, String[] args, VcsDirContainer filesByName,
                                            CommandOutputListener stdoutListener,
                                            CommandOutputListener stderrListener,
                                            CommandDataOutputListener stdoutDataListener, String dataRegex,
                                            CommandDataOutputListener stderrDataListener, String errorRegex);

}
