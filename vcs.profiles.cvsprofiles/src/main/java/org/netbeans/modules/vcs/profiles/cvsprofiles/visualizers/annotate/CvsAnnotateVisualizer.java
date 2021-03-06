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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.annotate;

import java.awt.Dialog;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import java.util.Iterator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;

import org.openide.windows.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.table.RevisionComparator;
import org.netbeans.modules.vcscore.util.table.DateComparator;
import org.netbeans.modules.vcscore.util.table.IntegerComparator;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 * The cvs annotate visaulizer.
 *
 * @author  Richard Gregor
 */
public class CvsAnnotateVisualizer extends OutputVisualizer {
    
    private static final String UNKNOWN = "server: nothing known about";  //NOI18N
    private static final String ANNOTATING = "Annotations for ";  //NOI18N
    private static final String STARS = "***************";  //NOI18N
    
    private ArrayList annotationLines; 
    private int lineNum;
    private String filePath;
    private ArrayList resultList;
    private HashMap file_infoMap;
    private HashMap output;
    
    /** Creates new CvsAnnotateVisualizer */
    public CvsAnnotateVisualizer() {
        super();
        annotationLines = new ArrayList();
        resultList = new ArrayList();
    }

    public Map getOutputPanels() {
        debug("getOutputPanel");       
        output = new HashMap();  
        Iterator it = files.iterator();
        while(it.hasNext()) {            
            String fileName = (String)it.next();
            filePath = rootDir+File.separator+fileName;
            debug("filePath:"+filePath);
            File file = new File(filePath);
            output.put(file.getPath(),showAnnotations(file));
        }
 
        return output;
    }
    
    private javax.swing.JComponent showAnnotations(File file) {
        debug("showAnn:"+file.getName());
        AnnotatePanel panel = new AnnotatePanel();
        createTableDefinition(panel);
        panel.clearAllLines();
        panel.setFileName(filePath);
        debug("annotationLines size:"+annotationLines.size());
        for (Iterator it = annotationLines.iterator(); it.hasNext(); ) {
            AnnotateLine line = (AnnotateLine) it.next();
            if(line != null)
                panel.addLine(line);
        }
        panel.doRepaintAndSort();
        return panel;
        
    }
     
    private void createTableDefinition(AnnotatePanel panel) {
        Class classa = AnnotateLine.class;
        try {
            Method method0 = classa.getMethod("getLineNumInteger", null);    //NOI18N
            Method method1 = classa.getMethod("getRevision", null);     //NOI18N
            Method method2 = classa.getMethod("getAuthor", null);     //NOI18N
            Method method3 = classa.getMethod("getDateString", null);     //NOI18N
            Method method4 = classa.getMethod("getContent", null);   //NOI18N
            panel.addLineNumColumnDefinition(method0, new IntegerComparator());
            panel.addRevisionColumnDefinition(method1, new RevisionComparator());
            panel.addAuthorColumnDefinition(method2, null);
            panel.addDateColumnDefinition(method3, new DateComparator());
            panel.addContentColumnDefinition(method4, null);
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
    }
 
    
    public boolean doesDisplayError() {
        return false;
    }
    
    /**
     * This method is called, with the output line.
     * @param line The output line.
     */
    public void stdOutputLine(String line) {
        debug("Line:"+line); 
        AnnotateLine annLine = processLine(line);
        if (annLine != null) {
            annLine.setLineNum(lineNum);            
            debug("line number:"+lineNum);
        }
        debug("annotationLines.add");
        annotationLines.add(annLine);
    }
    
    public AnnotateLine processLine(String line) {
        debug("processLine");
        int indexOpeningBracket = line.indexOf('(');
        int indexClosingBracket = line.indexOf(')');
        AnnotateLine annLine = null;
        if (indexOpeningBracket > 0 && indexClosingBracket > indexOpeningBracket) {
            String revision = line.substring(0, indexOpeningBracket).trim();
            String userDate = line.substring(indexOpeningBracket + 1, indexClosingBracket);
            String contents = line.substring(indexClosingBracket + 3);
            int lastSpace = userDate.lastIndexOf(' ');
            String user = userDate;
            String date = userDate;
            if (lastSpace > 0) {
                user = userDate.substring(0, lastSpace).trim();
                date = userDate.substring(lastSpace).trim();
            }
            annLine = new AnnotateLine();
            annLine.setContent(contents);
            annLine.setAuthor(user);
            annLine.setDateString(date);
            annLine.setRevision(revision);
            annLine.setLineNum(++lineNum);
        }
        return annLine;
    }
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(final String line) {
        debug("errOutputLine:"+line);
    }
    
    /**
     * Receive the data output.
     */
    public void stdOutputData(final String[] data) {
        debug("stdOutputData:"+data);
    }
    
    /**
     * Receive the error data output.
     */
    public void errOutputData(final String[] data) {
        debug("errOutputData:"+data);
    }
    private File createFile(String fileName) {
        File file = new File(filePath, fileName);
        debug("file:"+file.getAbsolutePath());
        return file;
    }
    
    private static boolean DEBUG = false;
    private static void debug(String msg){
        if(DEBUG)
            System.err.println("CvsAnnotateVisualizer: "+msg);
    }
    
    
    
}
