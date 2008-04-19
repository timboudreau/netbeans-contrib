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

package org.netbeans.modules.javafx.preview;

import java.io.IOException;
import java.net.URI;
import javax.swing.text.Document;
import javax.tools.SimpleJavaFileObject;
import org.netbeans.modules.editor.NbEditorUtilities;

class MemoryFileObject extends SimpleJavaFileObject {

    CharSequence code;
    String className;
    Document referredDoc;

    public MemoryFileObject(String className, CharSequence code, Kind kind, Document doc) {
        super(toURI(className), kind);
        this.code = code;
        this.className = className;
        this.referredDoc = doc;
    }
    
    Document getDocument() {
        return referredDoc;
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        return true;
    }

    @Override
    public URI toUri() {
        return toURI(className);
    }

    @Override
    public String getName() {
        return getFileName(className);
    }
    
    public String getClassName() {
        return className;
    }
    
    public String getFilePath(){
        return NbEditorUtilities.getFileObject(referredDoc).getPath();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return code;
    }

    private static URI toURI(String className) {
        return URI.create("./" + getFilePath(className));
    }
    
    private static String getFileName(String className){
        return className.substring(className.lastIndexOf('.') + 1) + ".fx";
    }
    
    private static String getFilePath(String className){
        return className.replace('.','/') + ".fx";
    }

    private void print(String text) {
        System.out.println("[file object] " + text);
    }    
}