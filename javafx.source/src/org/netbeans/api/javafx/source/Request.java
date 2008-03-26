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
package org.netbeans.api.javafx.source;

import java.util.Comparator;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.javafx.source.JavaFXSource.Priority;

/**
 * 
 * @author David Strupl (initially copied from Java Source module JavaSource.java)
 */

class Request {

    final CancellableTask<? extends CompilationInfo> task;
    final JavaFXSource source;        //XXX: Maybe week, depends on the semantics

    final Phase phase;
    final Priority priority;
    final boolean reschedule;

    public Request(final CancellableTask<? extends CompilationInfo> task, final JavaFXSource source,
            final Phase phase, final Priority priority, final boolean reschedule) {
        assert task != null;
        this.task = task;
        this.source = source;
        this.phase = phase;
        this.priority = priority;
        this.reschedule = reschedule;
    }

    public 
    @Override
    String toString() {
        if (reschedule) {
            return String.format("Periodic request for phase: %s with priority: %s to perform: %s", phase.name(), priority, task.toString());   //NOI18N

        } else {
            return String.format("One time request for phase: %s with priority: %s to perform: %s", phase != null ? phase.name() : "<null>", priority, task.toString());   //NOI18N

        }
    }

    public 
    @Override
    int hashCode() {
        return this.priority.ordinal();
    }

    public 
    @Override
    boolean equals( Object other) {
        if (other instanceof Request) {
            Request otherRequest = (Request) other;
            return priority == otherRequest.priority && reschedule == otherRequest.reschedule && (phase == null ? otherRequest.phase == null : phase.equals(otherRequest.phase)) && task.equals(otherRequest.task);
        } else {
            return false;
        }
    }

    static class RequestComparator implements Comparator<Request> {

        public int compare(Request r1, Request r2) {
            assert r1 != null && r2 != null;
            return r1.priority.compareTo(r2.priority);
        }
    }
}
    
    