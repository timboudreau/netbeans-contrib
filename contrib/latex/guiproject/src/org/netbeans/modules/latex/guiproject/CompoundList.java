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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;
import java.util.AbstractCollection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;


import java.util.Iterator;


import java.util.List;

/**
 *
 * @author Jan Lahoda
 */
public class CompoundList extends AbstractCollection {

    private List/*<Collection>*/ collections;

    public CompoundList(List/*<Collection>*/ collections) {
        this.collections = collections;
    }

    public Iterator iterator() {
        return new IteratorImpl(collections);
    }

    public int size() {
        int size = 0;
        
        for (Iterator i = collections.iterator(); i.hasNext(); ) {
            size += ((Collection) i.next()).size();
        }
        
        return size;
    }
    
    private static class IteratorImpl implements Iterator {
        
        private Iterator current;
        private Iterator content;
        
        public IteratorImpl(List collections) {
            List/*<Iterator>*/ iterators = new ArrayList();
            
            for (Iterator i = collections.iterator(); i.hasNext(); ) {
                iterators.add(((Collection) i.next()).iterator());
            }
            
            content = iterators.iterator();
            
            if (content.hasNext())
                current = (Iterator) content.next();
            else
                current = null;
        }
        
        public boolean hasNext() {
            if (current == null)
                return false;
            
            if (current.hasNext())
                return true;
            
            if (!content.hasNext())
                return false;
            
            current = (Iterator) content.next();
            
            return hasNext();
        }

        public Object next() {
            if (!hasNext())
                throw new ArrayIndexOutOfBoundsException("");//!!!
            
            return current.next();
        }

        public void remove() {
            current.remove();
        }
        
    }
    
}
