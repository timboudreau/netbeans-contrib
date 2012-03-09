/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2007-2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints.tasklist;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.providers.spi.HintDescription;
import org.netbeans.modules.java.hints.providers.spi.HintMetadata;
import org.netbeans.modules.java.hints.spiimpl.RulesManager;
import org.netbeans.modules.java.hints.spiimpl.hints.HintsInvoker;
import org.netbeans.modules.java.hints.spiimpl.options.HintsSettings;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.tasklist.Task;

/**
 *
 * @author lahvac
 */
public class ComputeTasks {

    public static List<? extends Task> computeTasks(CompilationInfo info, AtomicBoolean cancel) throws IOException {
        //TODO: move the hint computation to TaskResolver?
        Collection<HintDescription> hints = new LinkedList<HintDescription>();
        for ( Entry<HintMetadata, ? extends Collection<? extends HintDescription>> e : RulesManager.getInstance().readHints(info, Collections.<ClassPath>emptyList(), new AtomicBoolean()).entrySet()) {
            if (!HintsSettings.isEnabled(e.getKey()) || !HintsSettings.isShowInTaskList(e.getKey())) continue;

            hints.addAll(e.getValue());
        }

        List<ErrorDescription> errors = new HintsInvoker(info, cancel).computeHints(info, hints);
        List<Task> result = new LinkedList<Task>();

        for (ErrorDescription e : errors) {
            result.add(Task.create(info.getFileObject(), "nb-tasklist-warning", e.getDescription(), e.getRange().getBegin().getLine() + 1));
        }

        return result;
    }

}
