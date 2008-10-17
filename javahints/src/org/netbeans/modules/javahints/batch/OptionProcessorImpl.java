/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints.batch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.modules.java.hints.infrastructure.RulesManager;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.modules.java.hints.spi.TreeRule;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jan Lahoda
 */
public class OptionProcessorImpl extends OptionProcessor {

    private static final Option LIST = Option.withoutArgument(Option.NO_SHORT_NAME, "list-hints");
    private static final Option APPLY_HINTS = Option.requiredArgument(Option.NO_SHORT_NAME, "apply-hints");
    
    private static final Set<Option> OPTIONS = new HashSet<Option>(Arrays.asList(LIST, APPLY_HINTS));
    
    @Override
    protected Set<Option> getOptions() {
        return OPTIONS;
    }

    @Override
    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        if (optionValues.containsKey(LIST)) {
            env.getOutputStream().println("Supported Hints:");
            for (TreeRule r : BatchApply.listHints()) {
                env.getOutputStream().println(r.getDisplayName() + " - " + r.getId());
            }
        }

        if (optionValues.containsKey(APPLY_HINTS)) {
            String hintsArg = optionValues.get(APPLY_HINTS)[0];
            String[] hints = hintsArg.split(":");

            Lookup context = Lookups.fixed((Object[]) OpenProjects.getDefault().getOpenProjects());
            String error = BatchApply.applyFixes(context, new HashSet<String>(Arrays.asList(hints)));

            if (error != null) {
                env.getErrorStream().println("Cannot apply hints because of: " + error);
            }
        }
    }

}
