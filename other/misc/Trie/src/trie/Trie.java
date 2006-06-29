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
package trie;

/**
 * A trie of sorts.
 *
 * @author Tim Boudreau
 */
public interface Trie {
    /**
     * Get all the strings in the trie that begin with the
     * passed prefix.
     */
    public CharSequence[] match(CharSequence prefix);
    /**
     * Determine if the trie contains a character sequence.
     * The sequence may not have been explicitly added for it to
     * return true.
     */
    public boolean contains (CharSequence seq);
    /**
     * Add a character sequence.
     */
    public void add (CharSequence seq);
    /**
     * Remove a character sequence.  Note the meaning of this is a bit
     * ambiguous, since a string may be implicitly or explicitly 
     * present.  
     */
    public boolean remove (CharSequence seq);
}
