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

package org.netbeans.modules.fortress.editing.visitors;

import com.sun.fortress.nodes.Node;
import java.util.Collections;
import java.util.Set;
import org.netbeans.modules.fortress.editing.FortressMimeResolver;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Modifier;
import org.openide.filesystems.FileObject;

/**
 *
 * @author dcaoyuan
 */
public class Element implements ElementHandle {
    
    private Node node;
    private Node nameNode;
    private Scope enclosedScope;
    private ElementKind kind;
    
    public Element(Node node, Node nameNode, Scope enclosedScope, ElementKind kind) {
        this.node = node;
        this.nameNode = nameNode;
        this.enclosedScope = enclosedScope;
        this.kind = kind;
    }
    
    public Node getNode() {
        return node;
    }
    
    public Node getNameNode() {
        return nameNode;
    }
    
    public Scope getEnclosedScope() {
        return enclosedScope;
    }
    
    public  String getName() {
        return nameNode.stringName();
    }
    
    public  ElementKind getKind() {
        return kind;
    }
    
    public String getMimeType() {
        return FortressMimeResolver.MIME_TYPE;
    }

    public boolean signatureEquals(ElementHandle handle) {
        // XXX TODO
        return false;
    }

    public FileObject getFileObject() {
        return null;
    }
    
    public Set<Modifier> getModifiers() {
        return Collections.emptySet();
    }

    public String getIn() {
        return null;
    }
}
