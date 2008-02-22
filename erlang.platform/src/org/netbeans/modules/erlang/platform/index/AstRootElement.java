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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.erlang.platform.index;

import java.util.Collections;
import java.util.Set;
import org.netbeans.modules.gsf.api.Element;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.api.languages.ASTNode;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Caoyuan Deng
 */
public class AstRootElement implements Element{
    private FileObject fileObject;
    private ASTNode node;
    
    AstRootElement(FileObject fo, ASTNode node) {
        this.fileObject = fo;
        this.node = node;
    }
    
    public String getName() {
        return fileObject.getNameExt();
    }
    public String getIn() {
        return "To do getIn()";
    }
    
    public ElementKind getKind() {
        return ElementKind.OTHER;
    }
    
    public Set<Modifier> getModifiers() {
        return Collections.emptySet();
    }
    
    public ASTNode getNode() {
        return node;
    }
}


