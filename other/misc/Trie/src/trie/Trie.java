/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
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
