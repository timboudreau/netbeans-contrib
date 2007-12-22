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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.pojoeditors.api;

import java.io.Serializable;

/**
 * Interface for locating an editor of a particular type, without 
 * necessarily creating and opening one unless desired.
 *
 * @author Tim Boudreau
 */
public interface PojoEditorFinder<T extends Serializable> {
    /**
     * Finds an editor of the particular kind passed.  An instance of this
     * type can be found in the lookup of PojoDataObject/PojoDataNode.
     * 
     * @param kind The type of editor being sought, e.g. OPEN, VIEW or EDIT
     * @param openIfNecessary if true, create and open an editor to satisfy
     *      the request.  If false, only return an editor if one is already
     *      open
     * @return null if openIfNecessary is false and no editor of the passed 
     *      kind is open;  null if openIfNecessary is true but the EditorFactory
     *      for the PojoDataObject does not actually support this Kind;  otherwise
     *      returns an appropriate PojoEditor
     */
    PojoEditor<T> find (EditorFactory.Kind kind, boolean openIfNecessary);
}
