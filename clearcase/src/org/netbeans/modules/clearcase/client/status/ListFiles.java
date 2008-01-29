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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.clearcase.client.status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.clearcase.ClearcaseException;
import org.netbeans.modules.clearcase.client.Arguments;

/**
 *
 * @author Tomas Stupka
 */
public class ListFiles extends StatusExecutionUnit {

    /** Structured output from the commands **/ 
    protected List<ListOutput> output = new ArrayList<ListOutput>();
    
    private static String EXTENDED_NAMING_SYMBOL = "@@"; // XXX this is the defualt value. could be also somthing else.
    private static String RULE_PREFIX = "Rule:"; 

    private static Pattern typePattern = Pattern.compile("(" + 
        FileStatus.TYPE_VERSION +"|" + FileStatus.TYPE_DIRECTORY_VERSION + "|" + FileStatus.TYPE_FILE_ELEMENT + "|" + 
        FileStatus.TYPE_DIRECTORY_ELEMENT + "|" + FileStatus.TYPE_VIEW_PRIVATE_OBJECT+ "|" + FileStatus.TYPE_DERIVED_OBJECT + "|" + 
        FileStatus.TYPE_DERIVED_OBJECT_VERSION + "|" + FileStatus.TYPE_SYMBOLIC_LINK + ")" + 
        "( +)(.*)");

    private static Pattern annotationPattern = Pattern.compile("(.*?)(\\[.*\\])");
    private static Pattern checkedoutPattern = Pattern.compile("(.*?\\" + File.separator + "CHECKEDOUT)( +from +(.+?))?");

    public ListFiles(File file, boolean handleChildren) {
        super(handleChildren ? 
                new ListFiles.ListCommand[] { new ListFiles.ListCommand(file, true), new ListFiles.ListCommand(file, false) } : 
                new ListFiles.ListCommand[] { new ListFiles.ListCommand(file, false) });        
    }

    public List<ListOutput> getOutputList() {
        return output;
    }    
    
    public void outputText(String line) {
        ListOutput o = parseOutput(line); 
        if(o != null) {
            output.add(o);
        }
    }
    
    protected ListOutput parseOutput(String outputLine) {        
        Matcher typeMatcher = typePattern.matcher(outputLine);
        if(typeMatcher.matches()) {
            String type = typeMatcher.group(1);
            String fileDesc = typeMatcher.group(3);

            String filePath = null;       
            String annotation = null;
            FileVersionSelector version = null;
            FileVersionSelector originVersion = null;

            int idxAt = fileDesc.lastIndexOf(EXTENDED_NAMING_SYMBOL);

            if(idxAt > -1) {                

                // rip of the Rule part - "Rule: ... " 
                int idxRule = fileDesc.lastIndexOf(RULE_PREFIX);
                if(idxRule > -1) {
                    fileDesc = fileDesc.substring(0, idxRule).trim();
                }                
                filePath = fileDesc.substring(0, idxAt).trim();
                String extendedPathPart = fileDesc.substring(idxAt + EXTENDED_NAMING_SYMBOL.length()).trim();

                // rip of the Annotation - e.g [hijacked]
                Matcher annotationMatcher = annotationPattern.matcher(extendedPathPart);
                if(annotationMatcher.matches()) {
                   annotation = annotationMatcher.group(2).trim();
                   extendedPathPart = annotationMatcher.group(1).trim();
                } 

                String branch;
                String originBranch = null;
                Matcher checkedoutMatcher = checkedoutPattern.matcher(extendedPathPart);
                if(checkedoutMatcher.matches()) {
                    branch = checkedoutMatcher.group(1);
                    originBranch = checkedoutMatcher.group(3);                                        
                } else {
                    branch = extendedPathPart;
                }

                // XXX originVersion should be populated even if the file isn't checked out
                originVersion = originBranch != null ? FileVersionSelector.fromString(originBranch) : null;
                version = FileVersionSelector.fromString(branch);

            } else {
                filePath = fileDesc.trim();
            }           
            return new ListOutput(type, new File(filePath), originVersion, version, annotation);
        } else {
            Clearcase.LOG.warning("Unknownn file classification: \"" + outputLine + "\"");                    
            return null; // XXX do we need to do this?
        }
    }
    
    public static class ListOutput {
        final private String type;    
        final private File file;    
        final private FileVersionSelector originVersion;
        final private FileVersionSelector version;
        final private String annotation;

        private ListOutput(String type, File file, FileVersionSelector originVersion, FileVersionSelector version, String annotation) {
            this.type = type;
            this.file = file;
            this.originVersion = originVersion;
            this.version = version;
            this.annotation = annotation;
        }

        public String getAnnotation() {
            return annotation;
        }

        public File getFile() {
            return file;
        }

        public FileVersionSelector getOriginVersion() {
            return originVersion;
        }

        public String getType() {
            return type;
        }

        public FileVersionSelector getVersion() {
            return version;
        }
    }
    
    public static class ListCommand extends Command {
        private boolean handleChildren;
        public ListCommand(File file, boolean handleChildren) {
            super(file);
            this.handleChildren = handleChildren;
        }        
        @Override
        public void prepareCommand(Arguments arguments) throws ClearcaseException {
            arguments.add("ls");
            arguments.add("-long");
            if(file.isDirectory() && !handleChildren) {
                arguments.add("-directory");
            }
            arguments.add(file.getAbsoluteFile());
        }

        public String toString() {
            return "ls -long " + file.getAbsolutePath();
        }
    }
           
}
