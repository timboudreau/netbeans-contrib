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

package org.netbeans.modules.vcscore.versioning.impl;

import java.util.*;

import org.netbeans.modules.vcscore.versioning.RevisionItem;

/**
 * The revision item that represents revisions of the form <code>x.y</code>
 * where even number of dots represents branches and odd represents revisions.
 *
 * @author  Martin Entlicher
 */
public class NumDotRevisionItem extends RevisionItem {

    private static final long serialVersionUID = 7946273312693547993L;

    /** The number of dots that we match as subitems. */
    private int numAcceptDots;
    
    /** Creates new RevisionItem */
    public NumDotRevisionItem(String revision) {
        super(revision);
        numAcceptDots = NumDotRevisionItem.numDots(revision) - 1;
    }

    public boolean isBranch() {
        return (evenDots());
    }
    
    public static int numDots(String str) {
        int num = 0;
        for(int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.') num++;
        }
        return num;
    }

    private boolean evenDots() {
        return (NumDotRevisionItem.numDots(getRevision()) % 2) == 0;
    }
    
    public boolean isDirectSubItemOf(RevisionItem item) {
        if (item == null) {
            return this.numAcceptDots == 0;
        }
        String subRev = this.getRevision();
        String rev = item.getRevision();
        if (subRev.length() <= rev.length()) return false;
        /*
        System.out.println(subRev+".isDirectSubItemOf("+rev+")");
        System.out.println("  = "+(subRev.regionMatches(0, rev, 0, rev.length())
                && (rev.length() == 0 || subRev.charAt(rev.length()) == '.')
                && this.numAcceptDots == NumDotRevisionItem.numDots(rev)));
        System.out.println("  ("+subRev.regionMatches(0, rev, 0, rev.length())+", "+(rev.length() == 0 || subRev.charAt(rev.length()) == '.')+", "+(this.numAcceptDots == NumDotRevisionItem.numDots(rev))+")");
         */
        return (subRev.regionMatches(0, rev, 0, rev.length())
                && (rev.length() == 0 || subRev.charAt(rev.length()) == '.')
                && this.numAcceptDots == NumDotRevisionItem.numDots(rev));
    }

    protected int compareTo(RevisionItem item) {
        StringTokenizer tokens1 = new StringTokenizer(getRevision(), ".");
        StringTokenizer tokens2 = new StringTokenizer(item.getRevision(), ".");
        while(tokens1.hasMoreTokens() && tokens2.hasMoreTokens()) {
            String rev1 = tokens1.nextToken();
            String rev2 = tokens2.nextToken();
            int irev1;
            int irev2;
            try {
                irev1 = Integer.parseInt(rev1);
                irev2 = Integer.parseInt(rev2);
            } catch (NumberFormatException e) {
                return -1000;
            }
            if (irev1 != irev2) return irev2 - irev1;
        }
        if (tokens1.hasMoreTokens()) return -1;
        if (tokens2.hasMoreTokens()) return +1;
        return 0;
    }

}
