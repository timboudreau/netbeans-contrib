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
/*
 * ListDiff.java
 *
 * Created on September 18, 2004, 7:27 PM
 */

package org.netbeans.misc.diff;

import java.util.*;

/**
 * A diff of two <code>java.util.List</code>s, which provides a list of transformations which, applied in order to the
 * old list, will result in the new list.
 *
 * @author Tim Boudreau
 */
final class ListDiff <T> extends Diff <T> {
    private final List <T> old;
    private final List <T> nue;
    List <Change> changes = null;

    ListDiff (List <T> old, List <T> nue) {
        this.old = old;
        this.nue = nue;
    }

    public List <T> getOld () {
        return old;
    }

    public List <T> getNew () {
        return nue;
    }

    public List <Change> getChanges () {
        if ( changes == null ) {
            changes = new ParallelIterator <T> ( old, nue ).getChanges ();
        }
        return changes;
    }

    public String toString () {
        StringBuffer sb = new StringBuffer ();
        sb.append ( "Old:\n" );
        sb.append ( old );
        sb.append ( "\nNew:\n" );
        sb.append ( nue );
        sb.append ( "\nChanges\n" );
        sb.append ( getChanges () );
        return sb.toString ();
    }
}
