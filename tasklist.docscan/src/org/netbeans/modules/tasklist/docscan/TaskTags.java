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

package org.netbeans.modules.tasklist.docscan;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.regex.*;

import org.openide.ErrorManager;


/** Represents a set of tags in the user's source code that marks
 * lines describing tasks
 * @todo Move regexp code out of source scanner such that the
 *   matching is done in this file (by abstracting getParenStart & friends)
 *   
 *
 * @author Tor Norbye */
public final class TaskTags implements Externalizable {

    static final long serialVersionUID = 1L;
    private TaskTag[] tags = null;

    public TaskTags() {
    }
    
    /** Set the token associated with the tag - this is a case sensitive
     * string which when present in the user's code marks a task.
     */
    public void setTags(TaskTag[] tags) {
        this.tags = tags;
    }

    public TaskTag[] getTags() {
        return tags;
    }

    public TaskTag getTag(CharSequence token, int start, int len) {
        if (tags == null) {
            return null;
        }
        for (int i = 0; i < tags.length; i++) {
            if (same(tags[i].getToken(), token, start, len)) {
                return tags[i];
            }
        }
        return null;
    }

    private static boolean same(String s1, CharSequence s2, int start, int len) {
        if (s1.length() != len) return false;
        for (int i = 0; i<len; i++) {
            if (s1.charAt(i) != s2.charAt(start+i)) return false;
        }
        return true;
    }

    private Pattern regexp = null;

    /**
     * Gets the scan regular expression - used during scanning for
     * todo items. We use a regular expression since (I believe, but 
     * haven't checked) that this might be faster than a simple string
     * matching algorithm I could easily write. The regular expression
     * package should be able to build a check routine which is really
     * fast since it precomputes the bytecode(?) which as quickly as
     * possible checks all the matches.
     *
     * [ccc] I tied and that's true.
     */
    public Pattern getScanRegexp() {
        // Create regexp from tags
        if (regexp == null) {
            StringBuffer sb = new StringBuffer(200);
            TaskTag[] tgs = getTags();
            for (int i = 0; i < tgs.length; i++) {
                if (i > 0) {
                    sb.append('|');
                }
                String s = tgs[i].getToken();
                int n = s.length();
                // Insert token/boundary separator when we're dealing
                // with text tokens, since you probably don't want
                // a todo-match in a comment like
                // "and now process GLYPTODON content".
                // However, for non-token tags, such as "<<<<" don't
                // insert word boundary markers since it won't work - there's
                // no word on the right...
                if (Character.isJavaIdentifierPart(s.charAt(0))) {
                    // isJavaIdentifierPart - roughly matches what regex
                    // considers a word ([a-zA-Z_0-9])

                    // \W instead of \b: Workarond for issue 30250
                    sb.append("\\W"); // NOI18N
                }
                // "escape" the string here such that regexp meta
                // characters are handled literally
                for (int j = 0; j < n; j++) {
                    char c = s.charAt(j);
                    // regexp metachar?
                    if ((c == '(') || (c == ')') ||
                        (c == '{') || (c == '}') ||
                        (c == '[') || (c == ']') ||
                        (c == '?') || (c == '*') || (c == '+') ||
                        (c == '!') || (c == '|') || (c == '\\') ||
                        (c == '^') || (c == '$')) {
                        sb.append('\\');
                    }
                    sb.append(c);
                }
                if (Character.isJavaIdentifierPart(s.charAt(n-1))) {
                    sb.append("\\b"); // NOI18N
                }
            }
            try {
                regexp = Pattern.compile(sb.toString());
            } catch (PatternSyntaxException e) {
                // Internal error: the regexp should have been validated when
                // the user edited it
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
                return null;
            }
        }
        return regexp;
    }

    /** Read in a serialized version of the task tag
     * @param objectInput object stream to read from
     * @todo Use a more robust serialization format (not int uid based)
     * @throws IOException
     * @throws ClassNotFoundException  */    
    public void readExternal(ObjectInput objectInput) throws IOException, java.lang.ClassNotFoundException {
	    int ver = objectInput.read();
        assert ver == 1 : "serialization version incorrect; should be 1";

        // Read in the priority
	    int num = ((Integer)objectInput.readObject()).intValue();
        tags = new TaskTag[num];
        for (int i = 0; i < num; i++) {
            tags[i] = (TaskTag)objectInput.readObject();
        }
    }

    /** Write out relevant task tag settings data
     * @param objectOutput Object stream to write to
     * @throws IOException  */    
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.write(1); // SERIAL VERSION
	    objectOutput.writeObject(new Integer(tags.length));
        for (int i = 0; i < tags.length; i++) {
            objectOutput.writeObject(tags[i]);
        }
    }
}



