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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.netbeans.lib.callgraph.util.ComparableCharSequence;

/**
 * Represents a method or field and the things that it references and that
 * reference it.
 *
 * @author Tim Boudreau
 */
public final class SourceElement implements Comparable<SourceElement> {

    private final ComparableCharSequence name;
    private volatile Map<SourceElement, Integer> outboundCounts;
    private volatile Map<SourceElement, Integer> inboundCounts;
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
    
    public boolean isOrphan() {
        return (outboundCounts == null || outboundCounts.isEmpty()) 
                && (inboundCounts == null || inboundCounts.isEmpty());
    }

    public synchronized Set<SourceElement> getOutboundReferences() {
        return outboundCounts == null ? EMPTY : Collections.unmodifiableSet(outboundCounts.keySet());
    }

    private static final Set<SourceElement> EMPTY = Collections.emptySet();

    public synchronized Set<SourceElement> getInboundReferences() {
        return inboundCounts == null ? EMPTY : Collections.unmodifiableSet(inboundCounts.keySet());
    }

    public int inboundCount(SourceElement el) {
        return inboundCounts == null || !inboundCounts.containsKey(el) ? 0 : inboundCounts.get(el);
    }

    public int outboundCount(SourceElement el) {
        return outboundCounts == null || !outboundCounts.containsKey(el) ? 0 : outboundCounts.get(el);
    }

    public int inboundTypeCount(SourceElement typeProvider) {
        if (inboundCounts == null) {
            return 0;
        }
        return countTypes(typeProvider, inboundCounts);
    }

    public int outboundTypeCount(SourceElement typeProvider) {
        if (outboundCounts == null) {
            return 0;
        }
        return countTypes(typeProvider, outboundCounts);
    }

    private int countTypes(SourceElement typeProvider, Map<SourceElement, Integer> in) {
        int result = 0;
        for (Map.Entry<SourceElement, Integer> e : in.entrySet()) {
            if (typeProvider.typeName.equals(e.getKey().typeName)) {
                if (typeProvider.packageName.equals(e.getKey().packageName)) {
                    result += e.getValue();
                }
            }
        }
        return result;
    }

    public int inboundPackageCount(SourceElement typeProvider) {
        if (inboundCounts == null) {
            return 0;
        }
        return countPackages(typeProvider, inboundCounts);
    }

    public int outboundPackageCount(SourceElement typeProvider) {
        if (outboundCounts == null) {
            return 0;
        }
        return countPackages(typeProvider, outboundCounts);
    }

    private int countPackages(SourceElement typeProvider, Map<SourceElement, Integer> in) {
        int result = 0;
        for (Map.Entry<SourceElement, Integer> e : in.entrySet()) {
            if (typeProvider.packageName.equals(e.getKey().packageName)) {
                result += e.getValue();
            }
        }
        return result;
    }

    void addOutboundReference(SourceElement item) {
        if (item != this) {
            Map<SourceElement, Integer> outbound = this.outboundCounts;
            if (outbound == null) {
                synchronized (this) {
                    outbound = this.outboundCounts;
                    if (outbound == null) {
                        this.outboundCounts = outbound = new ConcurrentHashMap<>();
                    }
                }
            }
            Integer val = outbound.get(item);
            if (val == null) {
                val = 1;
            } else {
                val = val + 1;
            }
            outbound.put(item, val);
        }
    }

    synchronized void addInboundReference(SourceElement item) {
        if (item != this) {
            Map<SourceElement, Integer> inbound = this.inboundCounts;
            if (inbound == null) {
                synchronized (this) {
                    inbound = this.inboundCounts;
                    if (inbound == null) {
                        this.inboundCounts = inbound = new ConcurrentHashMap<>();
                    }
                }
            }
            Integer val = inbound.get(item);
            if (val == null) {
                val = 1;
            } else {
                val = val + 1;
            }
            inbound.put(item, val);
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

    static class Ref {

        public SourceElement element;
        public int count;

        public Ref(SourceElement element, int count) {
            this.element = element;
            this.count = count;
        }

        public String toString() {
            return element + "(" + count + ")";
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + Objects.hashCode(this.element);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Ref other = (Ref) obj;
            if (!Objects.equals(this.element, other.element)) {
                return false;
            }
            return true;
        }

    }
}
