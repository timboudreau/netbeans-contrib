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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.make2netbeans.api;

import java.util.List;

/**
 *
 * @author Andrey Gubichev
 */
public interface CodeAssistanceInterface {

    /**
     *
     * @return list of include paths
     */
    List<String> getIncludes(String makefilePath);

    /**
     *
     * @return list of all makefile targets
     */
    List<MakefileTarget> getTargets(String makefilePath);

    /**
     *
     * @return install target
     */
    String getInstallTarget(String makefilePath);

    /**
     *
     * @return info target
     */
    String getDocTarget(String makefilePath);

    /**
     *
     * @return test target
     */
    String getTestTarget(String makefilePath);
}