/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 1997-2015 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.lib.callgraph.javac;

import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.netbeans.lib.callgraph.util.ComparableCharSequence;

/**
 * Represents a method or field and the things that it references and that
 * reference it.
 *
 * @author Tim Boudreau
 */
public final class SourceElement implements Comparable<SourceElement> {

    private final ComparableCharSequence name;
    private volatile Set<SourceElement> inbound;
    private volatile Set<SourceElement> outbound = new HashSet<>();
    private final SourceElementKind kind;
    private final ComparableCharSequence type;
    private final ComparableCharSequence typeName;
    private final ComparableCharSequence parameters;
    private final ComparableCharSequence packageName;
    private final ComparableCharSequence qname;
    private final ComparableCharSequence shortName;
    private final boolean abstrakt;

    public SourceElement(SourceElementKind kind, TreePath handle, String name, String type, SourcesInfo info, boolean abstrakt) {
        this.name = info.strings.create(name);
        this.kind = kind;
        this.type = info.strings.create(type);
        // Derive the name and do not hang onto any objects from the Javac task,
        // or we will hold the whole compile in memory
        this.typeName = info.strings.create(info.nameOf(handle));
        parameters = info.strings.create(info.parametersOf(handle));
        packageName = info.strings.create(info.packageNameOf(handle));
        qname = info.strings.concat(packageName, info.strings.DOT, typeName(), info.strings.DOT, name, parameters());;
        shortName = info.strings.concat(typeName(), info.strings.DOT, name, parameters());
        this.abstrakt = abstrakt;
    }

    public ComparableCharSequence getType() {
        return type;
    }

    public ComparableCharSequence getName() {
        return name;
    }

    public synchronized Set<SourceElement> getOutboundReferences() {
        return outbound == null ? EMPTY : Collections.unmodifiableSet(outbound);
    }

    private static final Set<SourceElement> EMPTY = Collections.emptySet();

    public synchronized Set<SourceElement> getInboundReferences() {
        return inbound == null ? EMPTY : Collections.unmodifiableSet(inbound);
    }

    void addOutboundReference(SourceElement item) {
        if (item != this) {
            Set<SourceElement> outbound = this.outbound;
            if (outbound == null) {
                synchronized (this) {
                    outbound = this.outbound;
                    if (outbound == null) {
                        this.outbound = outbound = new ConcurrentSkipListSet<>();
                    }
                }
            }
            outbound.add(item);
        }
    }

    synchronized void addInboundReference(SourceElement item) {
        if (item != this) {
            Set<SourceElement> inbound = this.inbound;
            if (inbound == null) {
                synchronized (this) {
                    inbound = this.inbound;
                    if (inbound == null) {
                        this.inbound = inbound = new ConcurrentSkipListSet<>();
                    }
                }
            }
            inbound.add(item);
        }
    }

    public SourceElementKind kind() {
        return kind;
    }

    public ComparableCharSequence typeName() {
        return typeName;
    }

    public ComparableCharSequence packageName() {
        return packageName;
    }

    public ComparableCharSequence parameters() {
        return parameters;
    }

    public boolean isAbstract() {
        return abstrakt;
    }

    @Override
    public String toString() {
        return qname().toString();
    }

    public ComparableCharSequence qname() {
        return qname;
    }

    public ComparableCharSequence shortName() {
        return shortName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        SourceElement other = (SourceElement) obj;
        return qname().equals(other.qname());
    }

    @Override
    public int hashCode() {
        return 37 * qname().hashCode();
    }

    @Override
    public int compareTo(SourceElement o) {
        return qname.compareTo(o.qname);
    }
}
