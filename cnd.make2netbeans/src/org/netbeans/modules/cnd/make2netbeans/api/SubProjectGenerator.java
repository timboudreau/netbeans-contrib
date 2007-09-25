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

import java.io.IOException;

/**
 * Interface for creating subprojects
 * @author Andrey Gubichev
 */
public interface SubProjectGenerator {

    /**
     * initialize
     * @param newProjectFolder project folder
     * @param newWorkingDir working directory (for build and clean commands)
     * @param newMakefilePath path to existing makefile
     */
    public void init(String projectFolder, String workingDir, String makefilePath);

    /**
     *
     * @param cmd new build command
     */
    public void setBuildCommand(String cmd);

    /**
     *
     * @param cmd new clean command
     */
    public void setCleanCommand(String cmd);

    public void setInvokeDwarfProvider(boolean dwarf);

    /**
     *
     * @param out new output
     */
    public void setOutput(String out);

    /**
     *
     * @param prefix for projects display name
     */
    public void setPrefixName(String prefix);

    /**
     *
     * @param maximal depth of projects nesting
     */
    public void setDepthLevel(int depth);

    /**
     * Generate project and all its subprojects
     * @throws java.io.IOException in case something went wrong
     */
    public void generate() throws IOException;
}