/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dew4nb.services.debugger;

import net.java.html.json.Model;
import net.java.html.json.Property;
import org.netbeans.modules.dew4nb.endpoint.Status;

/**
 *
 * @author Tomas Zezula
 */
public final class DebugerModels {

    static final String END_POINT = "debug"; //NOI18N

    private DebugerModels() {
        throw new IllegalStateException("No instance allowed.");    //NOI18N
    }

    @Model(className = "DebugAction", properties = {
        @Property(name = "type", type = DebugMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "context", type = Context.class),
        @Property(name = "session", type = int.class),
        @Property(name = "data", type = String.class, array = true)
    })
    static final class DebugActionModel {
    }

    @Model(className = "AttachResult", properties = {
        @Property(name = "status", type = Status.class),
        @Property(name = "type", type = DebugMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "id", type = int.class)
    })
    static final class AttachResultModel {
    }


    @Model(className = "SetBreakpointsResult", properties = {
        @Property(name = "status", type = Status.class),
        @Property(name = "type", type = DebugMessageType.class),
        @Property(name = "state", type = String.class),
    })
    static final class SetBreakpointsResultModel {
    }

    @Model(className = "EvalResult", properties = {
        @Property(name = "status", type = Status.class),
        @Property(name = "type", type = DebugMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "values", type = String.class, array = true)
    })
    static final class EvalResultModel {
    }

    @Model(className = "ContinueResult", properties = {
        @Property(name = "status", type = Status.class),
        @Property(name = "type", type = DebugMessageType.class),
        @Property(name = "state", type = String.class),
        @Property(name = "id", type = int.class)
    })
    static final class ContinueResultModel {
    }

     @Model(className = "SuspendResult", properties = {
        @Property(name = "status", type = Status.class),
        @Property(name = "type", type = DebugMessageType.class),
        @Property(name = "id", type = int.class),
        @Property(name="stack", type = String.class, array = true)
    })
    static final class SuspendResultModel {
    }


    @Model(className = "Context", properties = {
        @Property(name="user", type=String.class),
        @Property(name = "workspace", type = String.class),
        @Property(name = "path", type = String.class)
    })
    static final class ContextModel {
    }

}
