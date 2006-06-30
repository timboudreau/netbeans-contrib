/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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



