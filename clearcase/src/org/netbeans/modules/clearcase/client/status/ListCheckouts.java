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
import java.util.logging.Level;
import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.clearcase.ClearcaseException;
import org.netbeans.modules.clearcase.client.Arguments;

/**
 *
 * @author Tomas Stupka
 */
public class ListCheckouts extends StatusExecutionUnit {

    /** Structured output from the commands **/ 
    protected List<LSCOOutput> output = new ArrayList<LSCOOutput>();
    
    private static String OUTPUT_DELIMITER = "<~=~>";
    private static String RESERVED = "reserved";
    
    public ListCheckouts(File file, boolean handleChildren) {        
        super(new LSCOCommand(file, handleChildren));        
    }        
    
    public List<LSCOOutput> getOutputList() {
        return output;
    }    
    
    public void outputText(String line) {
        LSCOOutput o = parseOutput(line);
        if(o != null) {
            output.add(o);
        }
    }
    
    protected LSCOOutput parseOutput(String outputLine) {        
        try {
            String st[] = outputLine.split(OUTPUT_DELIMITER);        
            File file = new File(st[0]);
            String user = st[1];
            boolean reserved = st[2].equals(RESERVED);
            return new LSCOOutput(user, file, reserved);
        } catch (Exception e) {
            Clearcase.LOG.log(Level.SEVERE, e.getMessage());
            return null;
        }
        
    }
    
    public static class LSCOOutput {
        final private String user;    
        final private File file;    
        final boolean reserved;
        public LSCOOutput(String user, File file, boolean reserved) {
            this.user = user;
            this.file = file;
            this.reserved = reserved;
        }
        public File getFile() {
            return file;
        }
        public boolean isReserved() {
            return reserved;
        }
        public String getUser() {
            return user;
        }        
    }
    
    private static class LSCOCommand extends Command {
        private boolean handleChildren;
        public LSCOCommand(File file, boolean handleChildren) {
            super(file);            
            this.handleChildren = handleChildren;
        }        

        @Override
        public void prepareCommand(Arguments arguments) throws ClearcaseException {
            arguments.add("lsco");       
            arguments.add("-fmt");
            arguments.add("\"%En" + OUTPUT_DELIMITER + "%u" + OUTPUT_DELIMITER + "%Rf\\n\"");
            if(file.isDirectory() && !handleChildren) {
                arguments.add("-directory");
            }
            arguments.add("-me");
            arguments.add("-cview");            
            arguments.add(file.getAbsoluteFile());
        }

        @Override
        public String toString() {
            return "lsco " + file.getAbsolutePath();
        }
    }
           
}
