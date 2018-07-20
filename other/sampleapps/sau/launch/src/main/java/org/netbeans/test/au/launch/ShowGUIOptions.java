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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.test.au.launch;

import java.awt.EventQueue;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;

/**
 *
 * @author Jaroslav Tulach
 */
public class ShowGUIOptions extends OptionProcessor {
    private static final Option GUI = Option.optionalArgument(Option.NO_SHORT_NAME, "gui"); // NOI18N
    private static final Option ALWAYS = Option.always();
    

    protected Set<Option> getOptions() {
        Set<Option> options = new HashSet<Option>();
        options.add(GUI);
        options.add(ALWAYS);
        return options;
    }

    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        String[] gui = optionValues.get(GUI);
        if (gui == null || !gui[0].equals("false")) {
            EventQueue.invokeLater(
                Installer.findObject(Installer.class, true)
            );
        }
    }

}
