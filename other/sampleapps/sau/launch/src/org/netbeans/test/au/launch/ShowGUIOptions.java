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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.test.au.launch;

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

    protected Set<Option> getOptions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
