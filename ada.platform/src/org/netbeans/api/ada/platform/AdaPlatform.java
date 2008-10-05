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

package org.netbeans.api.ada.platform;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaPlatform implements Serializable {
    private String name;
    private String info;

    private ArrayList<String> adaCompilerPath;
    private String compilerCommand;
    private String compilerArgs;

    public AdaPlatform() {
        adaCompilerPath = new ArrayList<String>();
    }


    public String getCompilerArgs() {
        return compilerArgs;
    }

    public void setCompilerArgs(String compilerArgs) {
        this.compilerArgs = compilerArgs;
    }

    public String getInterpreterCommand() {
        return compilerCommand;
    }

    public void setCompilerCommand(String compilerCommand) {
        this.compilerCommand = compilerCommand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ArrayList<String> getAdaCompilerPath() {
        return adaCompilerPath;
    }

    public void setAdaCompilerPath(ArrayList<String> adaCompilerPath) {
        this.adaCompilerPath = adaCompilerPath;
    }
    public void addAdaCompilerPath(String pathElement){
        getAdaCompilerPath().add(pathElement);
    }
    public void removeAdaCompilerPath(String pathElement){
        getAdaCompilerPath().remove(pathElement);
    }

    /**
     *Build a path string from arraylist
     * @param path
     * @return
     */
    public static String buildPath(ArrayList<String> path){
        StringBuilder pathString = new StringBuilder();
        int count = 0;
        for(String pathEle: path){
            pathString.append(pathEle);
            if (count++ < path.size()){
                pathString.append(File.pathSeparator);
            }
        }
        return pathString.toString();
    }

    void addAdaCompilerPath(String[] pathElements) {
        for (int i =0; i < pathElements.length; i++){
            addAdaCompilerPath(pathElements[i]);
        }
    }
}
