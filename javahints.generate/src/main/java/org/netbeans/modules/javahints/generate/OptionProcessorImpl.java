/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javahints.generate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.modules.java.hints.providers.spi.HintMetadata;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.modules.java.hints.spiimpl.RulesManager;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.LifecycleManager;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author lahvac
 */
@ServiceProvider(service=OptionProcessor.class)
public class OptionProcessorImpl extends OptionProcessor {

    private static final Option GENERATE_WIKI = Option.requiredArgument('g', "generate-wiki-text-and-exit");
    private static final Option GENERATE_ERRORS_WIKI = Option.requiredArgument('g', "generate-errors-wiki-text-and-exit");
    private static final Option GENERATE_HINTS_JSON = Option.requiredArgument('g', "generate-hints-json-and-exit");
    private static final Option DUMP_HINTS = Option.requiredArgument('d', "dump-hint-ids-and-exit");
    private static final Option DUMP_ERRORS = Option.requiredArgument('d', "dump-error-ids-and-exit");
    private static final Set<Option> OPTIONS = new HashSet<Option>(Arrays.asList(GENERATE_WIKI, GENERATE_ERRORS_WIKI, GENERATE_HINTS_JSON, DUMP_HINTS, DUMP_ERRORS));
    
    @Override
    protected Set<Option> getOptions() {
        return OPTIONS;
    }

    @Override
    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        boolean exit = false;
        if (optionValues.containsKey(GENERATE_WIKI)) {
            exit = true;
            
            OutputStream out = null;
            try {
                String targetFilePath = optionValues.get(GENERATE_WIKI)[0];
                File target = new File(env.getCurrentDirectory(), targetFilePath);
                out = new BufferedOutputStream(new FileOutputStream(target));
                out.write(GenerateHintWiki.generateWiki().getBytes("UTF-8"));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        if (optionValues.containsKey(GENERATE_ERRORS_WIKI)) {
            exit = true;
            
            OutputStream out = null;
            try {
                String targetFilePath = optionValues.get(GENERATE_ERRORS_WIKI)[0];
                File target = new File(env.getCurrentDirectory(), targetFilePath);
                out = new BufferedOutputStream(new FileOutputStream(target));
                out.write(GenerateHintWiki.generateErrorsWiki().getBytes("UTF-8"));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        if (optionValues.containsKey(GENERATE_HINTS_JSON)) {
            exit = true;
            
            OutputStream out = null;
            try {
                String targetFilePath = optionValues.get(GENERATE_HINTS_JSON)[0];
                File target = new File(env.getCurrentDirectory(), targetFilePath);
                out = new BufferedOutputStream(new FileOutputStream(target));
                out.write(GenerateHintWiki.generateHintsJSON().getBytes("UTF-8"));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        if (optionValues.containsKey(DUMP_HINTS)) {
            exit = true;
            
            OutputStream out = null;
            try {
                String targetFilePath = optionValues.get(DUMP_HINTS)[0];
                File target = new File(env.getCurrentDirectory(), targetFilePath);
                out = new BufferedOutputStream(new FileOutputStream(target));
                for (HintMetadata hm : RulesManager.getInstance().readHints(null, null, null).keySet()) {
                    out.write(hm.id.getBytes("UTF-8"));
                    out.write("\n".getBytes("UTF-8"));
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        if (optionValues.containsKey(DUMP_ERRORS)) {
            exit = true;
            
            OutputStream out = null;
            try {
                String targetFilePath = optionValues.get(DUMP_ERRORS)[0];
                File target = new File(env.getCurrentDirectory(), targetFilePath);
                out = new BufferedOutputStream(new FileOutputStream(target));
                for (ErrorRule rule : GenerateHintWiki.listErrorFixes()) {
                    out.write(rule.getId().getBytes("UTF-8"));
                    out.write("\n".getBytes("UTF-8"));
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        
        if (exit)
            LifecycleManager.getDefault().exit();
    }
    
}
