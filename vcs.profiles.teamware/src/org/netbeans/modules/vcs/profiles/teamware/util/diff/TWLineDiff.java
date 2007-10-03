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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.vcs.profiles.teamware.util.diff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.diff.Difference;

/**
 * Diff implementation, that compares just lines. Based on source provided
 * by Kit Bishop, cbishop@voyager.co.nz.
 *
 * @author  Kit Bishop, Martin Entlicher
 */
public class TWLineDiff extends Object {

    public static final boolean DEFAULT_HEURISTIC = true;

    public static final long DEFAULT_MINMATCH = 1;

    /**
     * Creates a new instance of TWLineDiff
     */
    private TWLineDiff() {
    }
    
    public static Difference[] diff(TWLineIndexedAccess f1, TWLineIndexedAccess f2) throws IOException {
        return diff(f1, f2, DEFAULT_HEURISTIC, DEFAULT_MINMATCH);
    }
    
    public static Difference[] diff(TWLineIndexedAccess f1, TWLineIndexedAccess f2, boolean heuristic, long minmatch) throws IOException {
        List diffs = new ArrayList();
        long s1=f1.length();
        long s2=f2.length();
        
        if ((s1 == 0) && (s2 == 0)) {
            // Both files empty
            return new Difference[0];
        }
        if (s1 == 0) {
            // First file empty
            //addFileIns(diffs, f2, 0, s2);
            diffs.add(createAdd(f2, 0, 0, s2));
            return (Difference[]) diffs.toArray(new Difference[diffs.size()]);
        }
        if (s2 == 0) {
            // Second file empty
            //addFileDel(diffs, 0, s1);
            diffs.add(createDel(f1, 0, s1, 0));
            return (Difference[]) diffs.toArray(new Difference[diffs.size()]);
        }

        // do diff
        long o1=0;
        long o2=0;
        long iRun;
        long max_i;
        long line1 = 0;
        long line2 = 0;
        boolean sync;
        while ((s1 !=o1) && (s2 != o2)) {
            max_i = Math.max(s1-o1, s2-o2) - minmatch;
            sync = false;
     
    i_loop:
            for(long i=0; i <= max_i; i++) {
        j_loop:        
                for(long j=0; j <= i; j++) {
                    if (testMinmatch(f1, o1+i, s1, f2, o2+j, s2, minmatch)) {
                        if (heuristic) {
                            while ((i>0) && (j>0) && (f1.readAt(o1+i-1).equals(f2.readAt(o2+j-1)))) {
                                i--;
                                j--;
                            }
                        }
                        
                        if (i > 0) {
                            //addFileDel(diffs, i);
                            //diffs.addDiffItem(new DiffSkip(i));
                            diffs.add(createDel(f1, o1, o1 + i, o2));
                        }
                        //addFileIns(diffs, f2, o2, j);
                        diffs.add(createAdd(f2, o1, o2, o2 + j));
                        o1 += i;
                        o2 += j;
                        sync = true;
                        break i_loop;
                    }
                    
                    if (testMinmatch(f1, o1+j, s1, f2, o2+i, s2, minmatch)) {
                        if (heuristic) {
                            while ((i>0) && (j>0) && (f1.readAt(o1+j-1).equals(f2.readAt(o2+i-1)))) {
                                i--;
                                j--;
                            }
                        }
                        if (j > 0) {
                            //diffs.addDiffItem(new DiffSkip(j));
                            diffs.add(createDel(f1, o1, o1 + j, o2));
                        }
                        //addFileIns(diffs, f2, o2, i);
                        diffs.add(createAdd(f2, o1, o2, o2 + i));
                        o1 += j;
                        o2 += i;
                        sync = true;
                        break i_loop;
                    }
                }
      
                if (heuristic) {
                    i += i/10;
                }
            }
            
            if (!sync) {
                // no sync found
                
                if ((s1-o1) > 0) {
                    //diffs.addDiffItem(new DiffSkip(s1-o1));
                    diffs.add(createDel(f1, o1, s1, o2));
                }
                //addFileIns(diffs, f2, o2, s2-o2);
                diffs.add(createAdd(f2, o1, o2, s2));
                o1 = s1;
                o2 = s2;
            }

            if ((o1 != s1) && (o2 != s2)) {
                iRun = getRun(f1, o1, s1, f2, o2, s2);
                
                if (iRun > 0) {
                    //diffs.addDiffItem(new DiffRun(iRun));
                    
                    o1 += iRun;
                    o2 += iRun;
                }
            }
        }

        /* CANNOT HAPPEN
        if ((o1 != s1) && (o2 != s2))
            throw new DiffException("Internal error creating difference list.");
         */
        
        if (o1 != s1) {
            //diffs.addDiffItem(new DiffSkip(s1-o1));
            diffs.add(createDel(f1, o1, s1, o2));
        }
        
        if (o2 != s2) {
            //addFileIns(diffs, f2, o2, s2-o2);
            diffs.add(createAdd(f2, o1, o2, s2));
        }
        
        //diffs.addDiffItem(new DiffEnd());
        cleanup(diffs);
        
        return (Difference[]) diffs.toArray(new Difference[diffs.size()]);
    }

    private static long getRun(TWLineIndexedAccess f1, long o1, long s1,
                               TWLineIndexedAccess f2, long o2, long s2)
                               throws IOException {
        long i;
        long i1 = o1;
        long i2 = o2;
       
        for(i=0; (i1<s1) && (i2<s2) && (f1.readAt(i1).equals(f2.readAt(i2))); i++, i1++, i2++);
            
        return i;
    }

    /**
     * Thest whether there is a match of at least <code>minmatch</code> characters.
     * @param f1 The first file
     * @param o1 The starting position of the first file
     * @param s1 The length of the first file
     * @param f2 The second file
     * @param o2 The starting position of the second file
     * @param s2 The length of the second file
     */
    private static boolean testMinmatch(TWLineIndexedAccess f1, long o1, long s1,
                                        TWLineIndexedAccess f2, long o2, long s2,
                                        long minmatch) throws IOException {
        long i1=o1;
        long i2=o2;
   
        if ((s1-i1) < minmatch) 
            return false;
       
        if ((s2-i2) < minmatch) 
            return false;
       
        for (int i=0; i<minmatch; i++, i1++, i2++) {
            if (!(f1.readAt(i1).equals(f2.readAt(i2)))) { 
                return false;
            }
        }
     
        return true;
    }

    private static Difference createAdd(TWLineIndexedAccess file, long f1l1, long f2l1, long f2l2) {
        if (f2l1 >= f2l2) {
            //System.out.println("f2l1 = "+f2l1+", f2l2 = "+f2l2);
            //Thread.currentThread().dumpStack();
            return null;
        }
        StringBuffer text = new StringBuffer();
        try {
            String[] lines = (String[]) file.readFullyAt(f2l1, f2l2 - f2l1);
            for (int i = 0; i < lines.length; i++) {
                text.append(lines[i]);
                text.append("\n");
            }
        } catch (IOException ioex) {}
        return new Difference(Difference.ADD, (int) f1l1, 0, (int) f2l1 + 1, (int) f2l2,
                              "", text.toString());
    }

    private static Difference createDel(TWLineIndexedAccess file, long f1l1, long f1l2, long f2l1) {
        StringBuffer text = new StringBuffer();
        try {
            String[] lines = (String[]) file.readFullyAt(f1l1, f1l2 - f1l1);
            for (int i = 0; i < lines.length; i++) {
                text.append(lines[i]);
                text.append("\n");
            }
        } catch (IOException ioex) {}
        return new Difference(Difference.DELETE, (int) f1l1 + 1, (int) f1l2, (int) f2l1, 0,
                              text.toString(), "");
    }
    
    private static void cleanup(List diffs) {
        /*
        System.out.println("\nCLEANUP, I have:");
        for (int i = 0; i < diffs.size(); i++) {
            System.out.println(diffs.get(i));
        }
        System.out.println("Starting cleanup...");
        */
        Difference last = null;
        for (int i = 0; i < diffs.size(); i++) {
            Difference diff = (Difference) diffs.get(i);
            if (diff == null) {
                diffs.remove(i);
                i--;
                continue;
            }
            if (last != null) {
                //System.out.println("\ndiff = "+diff);
                //System.out.println("last = "+last+"\n");
                if (diff.getType() == Difference.ADD && last.getType() == Difference.DELETE ||
                    diff.getType() == Difference.DELETE && last.getType() == Difference.ADD) {

                    Difference add;
                    Difference del;
                    if (Difference.ADD == diff.getType()) {
                        add = diff;
                        del = last;
                    } else {
                        add = last;
                        del = diff;
                    }
                    int d1f1l1 = add.getFirstStart() + 1;
                    int d2f1l1 = del.getFirstStart();
                    if (d1f1l1 == d2f1l1) {
                        int d1f2l1 = add.getSecondStart();
                        int d2f2l1 = del.getSecondStart() + 1;
                        if (d1f2l1 == d2f2l1) {
                            Difference newDiff = new Difference(Difference.CHANGE,
                                d1f1l1, del.getFirstEnd(), d1f2l1, add.getSecondEnd(),
                                del.getFirstText(), add.getSecondText());
                            diffs.set(i - 1, newDiff);
                            diffs.remove(i);
                            i--;
                            diff = newDiff;
                        }
                    }
                }
            }
            last = diff;
        }
        /*
        System.out.println("\nAFTER cleanup, I have:");
        for (int i = 0; i < diffs.size(); i++) {
            System.out.println(diffs.get(i));
        }
         */
    }

}
