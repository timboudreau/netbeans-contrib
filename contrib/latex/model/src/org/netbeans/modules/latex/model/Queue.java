/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Jan Lahoda
 */
public class Queue {
    
    private LinkedList queue;
    
    /** Creates a new instance of Queue */
    public Queue() {
        queue = new LinkedList();
    }
    
    public void put(Object obj) {
        queue.addLast(obj);
    }
    
    public Object pop() {
        return queue.removeFirst();
    }
    
    public boolean empty() {
        return queue.size() == 0;
    }
    
    public void putAll(Collection c) {
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            put(i.next());
        }
    }
}
