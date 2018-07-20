/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.api.docbook;

import org.openide.filesystems.FileObject;


/**
 * Parent class for parse visitors.
 */
public abstract class Callback<T> {
    private volatile boolean cancelled;
    private final T t;
    Callback(T t) {
        this.t = t;
    }

    /**
     *  Get the regexp Pattern or SAX ContentHandler this callback provides
     * to collect parse info.
     */ 
    public final T getProcessor() {
        return t;
    }

    /**
     * Cancel this callback so it will not be run.
     */ 
    public final void cancel() {
        cancelled = true;
        cancelled();
    }

    /**
     * Determine if this callback has been cancelled.
     */ 
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * Called when a parse is started.  Default impl does nothing.
     */ 
    protected void start(FileObject f, ParseJob job) {
        //do nothing
    }

    /**
     * Called when a parse is cancelled.  Default impl does nothing.
     */ 
    protected void cancelled() {
        //do nothing
    }

    /**
     * Called when a parse is completed, either with failure or success.  
     * Default impl does nothing.
     */ 
    protected void done(FileObject f, ParseJob job) {
        //do nothing
    }

    /**
     * Called when a parse is has failed.  Default impl does nothing.
     * Failure can be throwing a SAXException, or any runtime exception
     * from any method on this class.
     */ 
    protected void failed(Exception e, FileObject ob, ParseJob job) {
        //do nothing
    }
}