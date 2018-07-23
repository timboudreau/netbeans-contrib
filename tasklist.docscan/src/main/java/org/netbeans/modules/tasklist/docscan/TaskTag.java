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

package org.netbeans.modules.tasklist.docscan;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.netbeans.modules.tasklist.client.SuggestionPriority;


/** Represents a tag in the user's source code that should be interpreted
 * as a task.
 *
 * @author Tor Norbye */
public final class TaskTag implements Externalizable {

    static final long serialVersionUID = 1L;
    private String token = null;
    private SuggestionPriority priority = SuggestionPriority.MEDIUM;

    public TaskTag() {
    }
    
    /** 
     * Create a task tag with the given attributes
     * @param token The token string that needs to occur in the source
     * @param priority The priority to assign to tasks for these tokens
     */
    public TaskTag(String token, SuggestionPriority priority) {
        this.token = token;
        this.priority = priority;
    }
    
    /** Set the token associated with the tag - this is a case sensitive
     * string which when present in the user's code marks a task.
     */
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setPriority(SuggestionPriority priority) {
        this.priority = priority;
    }

    public SuggestionPriority getPriority() {
        return priority;
    }

    /** Generate a string summary of the tag; only used
     * for debugging. DO NOT depend on this format for anything!
     * @return summary string */    
    public String toString() {
        return "TaskTag[\"" + token + "\", " + priority + "]"; // NOI18N
    }


    /** Read in a serialized version of the task tag
     * @param objectInput object stream to read from
     * @todo Use a more robust serialization format (not int uid based)
     * @throws IOException
     * @throws ClassNotFoundException  */    
    public void readExternal(ObjectInput objectInput) throws IOException, java.lang.ClassNotFoundException {
	int ver = objectInput.read();
        //assert ver == 1 : "serialization version incorrect; should be 1";

        // Read in the token
	token = (String)objectInput.readObject();

        // Read in the priority
	int prioNum = ((Integer)objectInput.readObject()).intValue();
        // TODO - this should really be a static factory in SuggestionPriority!
        switch (prioNum) {
        case 1: priority = SuggestionPriority.HIGH; break;
        case 2: priority = SuggestionPriority.MEDIUM_HIGH; break;
        case 3: priority = SuggestionPriority.MEDIUM; break;
        case 4: priority = SuggestionPriority.MEDIUM_LOW; break;
        case 5: priority = SuggestionPriority.LOW; break;
        default: priority = SuggestionPriority.MEDIUM; break;
        }
    }

    /** Write out relevant task tag settings data
     * @param objectOutput Object stream to write to
     * @throws IOException  */    
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.write(1); // SERIAL VERSION
	objectOutput.writeObject(token);
	objectOutput.writeObject(new Integer(priority.intValue()));
    }

}



