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
package org.sample.registry.model;

import org.sample.registry.model.v09.Registry09;
import org.sample.registry.model.v09.Service09;

public interface RegistryVisitor {

    void visit(Registry component);
    void visit(Entries component);
    void visit(KnownTypes component);
    void visit(Service component);
    void visit(ServiceProvider component);
    void visit(ServiceType component);
    void visit(Registry09 component);
    void visit(Service09 component);

    /**
     * Default shallow visitor.
     */
    public static class Default implements RegistryVisitor {
        public void visit(Registry component) {
            visitChild();
        }
        public void visit(Registry09 component) {
            visitChild();
        }
        public void visit(Entries component) {
            visitChild();
        }
        public void visit(KnownTypes component) {
            visitChild();
        }
        public void visit(Service component) {
            visitChild();
        }
        public void visit(Service09 component) {
            visitChild();
        }
        public void visit(ServiceProvider component) {
            visitChild();
        }
        public void visit(ServiceType component) {
            visitChild();
        }
        protected void visitChild() {
        }
    }
    
    /**
     * Deep visitor.
     */
    public static class Deep extends Default {
        protected void visitChild(RegistryComponent component) {
            for (RegistryComponent child : component.getChildren()) {
                child.accept(this);
            }
        }
    }
}
