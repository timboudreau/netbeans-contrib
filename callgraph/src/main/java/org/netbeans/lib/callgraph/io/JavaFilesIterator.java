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

package org.netbeans.lib.callgraph.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Iteratively reads directories and finds files, without pre-walking the
 * file tree and building a giant array of Files - finds the next one
 * on demand.
 *
 * @author Tim Boudreau
 */
public class JavaFilesIterator implements Iterator<File>, FileFilter {

    private final LinkedList<Iterator<File>> stack = new LinkedList<>();
    private File next;

    public JavaFilesIterator(File root) throws IOException {
        Iterator<File> base = list(root);
        stack.push(base);
    }

    public static Iterable<File> iterable(final File root) {
        return () -> {
            try {
                return new JavaFilesIterator(root);
            } catch (IOException ex) {
                throw new IllegalStateException(root + "");
            }
        };
    }

    private Iterator<File> list(File root) {
        return new NamedIterator<>(root.getName(), Arrays.asList(root.listFiles(this)).iterator());
    }

    private Iterator<File> current() {
        if (stack.isEmpty()) {
            return null;
        }
        Iterator<File> result = stack.peek();
        if (!result.hasNext()) {
            stack.pop();
            return current();
        }
        return result;
    }

    private void findNext() {
        if (next != null) {
            return;
        }
        Iterator<File> iter = current();
        if (iter == null) {
            next = null;
            return;
        }
        File f = iter.next();
        if (f.isDirectory()) {
            stack.push(list(f));
            findNext();
        } else {
            next = f;
        }
    }

    @Override
    public synchronized boolean hasNext() {
        findNext();
        return next != null;
    }

    @Override
    public synchronized File next() {
        findNext();
        if (next == null) {
            throw new IndexOutOfBoundsException();
        }
        File nx = next;
        next = null;
        return nx;
    }

    @Override
    public boolean accept(File file) {
        return (file.isDirectory() && !file.getName().startsWith(".")) 
                || ((file.isFile() && file.canRead() && file.getName().endsWith(".java") 
                && !file.getName().equals("package-info.java")));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }

    private static class NamedIterator<File> implements Iterator<File> {

        private final String name;
        private final Iterator<File> del;

        public NamedIterator(String name, Iterator<File> del) {
            this.name = name;
            this.del = del;
        }

        @Override
        public boolean hasNext() {
            return del.hasNext();
        }

        @Override
        public File next() {
            return del.next();
        }

        public String toString() {
            return name;
        }

        @Override
        public void remove() {
            del.remove();
        }
    }
}
