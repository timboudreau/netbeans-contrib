/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionList;
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
        
        protected int cmpRev(String revision)
        {
            return revision.compareTo(this.getRevision());
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
