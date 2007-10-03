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

package org.netbeans.modules.vcs.profiles.clearcase.list;

import java.util.*;

import java.io.*;

import org.openide.filesystems.*;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsFactory;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.netbeans.modules.vcscore.util.VcsUtilities;

public class ClearCaseRevisionListGetter implements VcsAdditionalCommand
{

    private ArrayList revisionItems = new ArrayList();
    private ArrayList lastRevisionItems = null;
    private VcsFileSystem fileSystem = null;
    private VcsCommand logCmd = null;
    private CommandOutputListener stdoutNRListener = null;
    private CommandOutputListener stderrNRListener = null;

    public ClearCaseRevisionListGetter() 
    {
    }

    public void setFileSystem(VcsFileSystem fileSystem) 
    {
        this.fileSystem = fileSystem;
    }
    
    /**
     * From the VcsAdditionalCommand interface...
     *
     * Executes the history command to get the logging informations.
     * @param vars variables needed to run the clearcase commands
     * @param args the arguments,
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull,
     *         false if some error has occured.
     */
    public boolean exec(final Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        // Wire the listeners
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;

        System.out.println("Calling revision list getter for " + args[0]);
        
        InputStream istream = null;
        
        try {
            Process process = Runtime.getRuntime().exec(
                new String[] {
                    "cleartool",
                    "lshistory",
                    "-fmt",
                    "\"%Nd\" \"%u\" %o \"%f\" \"%Vn\" \"%En\";\n",
                    args[0]
                }
                );
            
            istream = process.getInputStream();
            InputStream errstream = process.getErrorStream();

            BufferedReader es = new BufferedReader(new InputStreamReader(errstream));
            ClearCaseRevisionList revisionList = new ClearCaseRevisionList();
            
            revisionList.processRevisionStream(istream);
            

            // This is incomprehensible. We turn it into a byte array
            // for hex encoding??? WHY?
            stdoutListener.outputData(new String[] {VcsUtilities.encodeValue(revisionList)});
            // Dump the error stream
            String line;
            while ((line = es.readLine()) != null)
            {
                stderrNRListener.outputLine(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            
            // Dump rest of output stream for debugging
            if (istream != null)
            {
                try {
                    BufferedReader is = new BufferedReader(new InputStreamReader(istream));
                    String line;
            
                    while ((line = is.readLine()) != null)
                    {
                        System.out.println(line);
                    }
                } catch (IOException ioe2) {
                    // oh well
                }
            }
            
            return false;
        }
        
        return true;
    }
    
    static class ClearCaseRevisionItem extends RevisionItem
    {
        String date;
        String uname;
        String operation;
        String checkout;
        String version;
        String entityName;
        String comment;
        String branch;
        
        ClearCaseRevisionList list;

        public ClearCaseRevisionItem(String date,
                                     String uname,
                                     String operation,
                                     String checkout,
                                     String version,
                                     String entityName,
                                     String comment,
                                     ClearCaseRevisionList list)
        {
            super(version);
            
            this.date =  date;
            this.uname =  uname;
            this.operation =  operation;
            this.checkout =  checkout;
            this.version =  version;
            this.entityName =  entityName;
            this.comment =  comment;
            this.list =  list;

            int pos;
            
            if (version != null && (pos = version.lastIndexOf('/')) != -1)
            {
                this.branch = version.substring(0, pos);
            }
        }
        public RevisionItem getNextItem()
        {
            SortedSet tailSet = list.tailSet(this);
            if (tailSet.first() != this)
                throw new RuntimeException("I'm confused");
            Iterator iterator = tailSet.iterator();
            iterator.next();    // Skip past self
            if (!iterator.hasNext())
            {
                return null;
            }
            
            ClearCaseRevisionItem result = (ClearCaseRevisionItem) iterator.next();
            if (result.operation.equals("checkin") 
                && result.branch.equals(this.branch))
                return result;
            else
                return null;
        }

        public boolean isBranch()
        {
            boolean result = operation.equals("mkbranch") || operation.equals("mkelem");
            return result;
        }
        
        protected int compareTo(RevisionItem item)
        {
            return this.getRevision().compareTo(item.getRevision());
        }
        
        public boolean isDirectSubItemOf(RevisionItem item) {
            if (item == null) return true;
            else return false;
        }
        
        public RevisionItem addRevision(String revision)
        {
            throw new RuntimeException("Unsupported method");
        }

        public RevisionItem addBranch(String branch)
        {
            throw new RuntimeException("Unsupported method");
        }
    
        private void readObject(java.io.ObjectInputStream in) 
            throws ClassNotFoundException, 
                   java.io.IOException
        {
            in.defaultReadObject();
            System.out.println("Deserializing " + getRevision());
        }
    }

    static class ClearCaseRevisionList extends RevisionList
    {
        HashMap revisionTable = new HashMap();
        
        public void processRevisionStream(InputStream istream)
            throws IOException
        {
            StreamTokenizer tok = new StreamTokenizer(istream);

            tok.eolIsSignificant(false);

            // Date processing
            tok.wordChars('.', '.');
            tok.wordChars('-', '-');
            tok.wordChars(':', ':');
            tok.wordChars('0', '9');

            // Version path processing
            tok.wordChars('/', '/');

            String lastDate = null;
            
            while (true)
            {
                int ttype = tok.nextToken();
                if (ttype == StreamTokenizer.TT_EOF)
                    break;

                String date = tok.sval;
                ttype = tok.nextToken();
                String username = tok.sval;
                ttype = tok.nextToken();
                String operation = tok.sval;
                ttype = tok.nextToken();
                String checkout = tok.sval;
                ttype = tok.nextToken();
                String version = tok.sval;
                ttype = tok.nextToken();
                String entityName = tok.sval;
                StringBuffer comment = new StringBuffer();

                ttype = tok.nextToken();

                /*
                // Now we read the comment
                while ((ttype = tok.nextToken()) == StreamTokenizer.TT_WORD)
                {
                comment.append(tok.sval);
                }
                */
                if (lastDate == null || !lastDate.equals(date))
                {
                    ClearCaseRevisionItem item = new ClearCaseRevisionItem(
                        date,
                        username,
                        operation,
                        checkout,
                        version,
                        entityName,
                        comment.toString(),
                        this);
                    this.add(item);
                    if (revisionTable.get(version) != null)
                        System.out.println("WARNING: replacing revision entry");
                    
                    this.revisionTable.put(version, item);
                }
                lastDate = date;
                
                if (ttype != ';')
                    throw new IOException("Malformed comment, expected ';', got " + (char) ttype + "(" + ttype + ") last token " + entityName);
            }

            System.out.println("Processed " + revisionTable.size() + " items");

            System.out.println("Here is everything in order");
            Iterator iterator = this.iterator();
            while (iterator.hasNext())
            {
                ClearCaseRevisionItem item = (ClearCaseRevisionItem) iterator.next();
                System.out.println(item.operation + ":" + item.getRevision());
            }

            System.out.println("Here are the branches");
            iterator = this.iterator();
            while (iterator.hasNext())
            {
                ClearCaseRevisionItem item = (ClearCaseRevisionItem) iterator.next();
                if (item.isBranch())
                {
                    System.out.println(item.getRevision() + " contains subrevisions: " + containsSubRevisions(item.getRevision()));

                    ClearCaseRevisionItem next = (ClearCaseRevisionItem) item.getNextItem();
                    
                    while (next != null)
                    {
                        System.out.println("On " + item.getRevision() + 
                                           " is " +
                                           next.getRevision());
                        
                        next = (ClearCaseRevisionItem) next.getNextItem();
                    }
                }
            }
        }

        /**
         * for any revision string that you get you should return true
         * only if there exists some revision that is in this branch.
         */
        public boolean containsSubRevisions(String revision)
        {
            ClearCaseRevisionItem item = (ClearCaseRevisionItem) revisionTable.get(revision);
            boolean result = item.isBranch();
            return result;
        }
        private void readObject(java.io.ObjectInputStream in) throws ClassNotFoundException, java.io.IOException
        {
            in.defaultReadObject();

            System.out.println("Deserializing, now have " + size() + " items");
        }
    }
}
