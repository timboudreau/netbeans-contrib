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

package org.netbeans.api.convertor.store;

import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.api.convertor.book.Book;
import org.netbeans.api.convertor.dvd.DVD;


/**
 *
 * @author  David Konecny
 */
public class Store {

    public Collection books;
    public Collection dvds;

    public Store() {
        books = new ArrayList();
        dvds = new ArrayList();
    }

    public Store(Collection books, Collection dvds) {
        this.books = books;
        this.dvds = dvds;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Store)) {
            return false;
        }
        Store s = (Store)o;
        return books.equals(s.books) &&
            dvds.equals(s.dvds);
    }
    
    public void addBook(Book b) {
        books.add(b);
    }
    
    public void addDVD(DVD d) {
        dvds.add(d);
    }
    
    public int hashCode() {
        // not relevant
        return 125;
    }
    
    public String toString() {
        return "Store[dvds="+dvds+", books="+books+"]"+super.toString();
    }

}
