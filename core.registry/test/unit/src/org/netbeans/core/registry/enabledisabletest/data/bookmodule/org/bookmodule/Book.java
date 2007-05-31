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

package org.bookmodule;

import java.io.Serializable;

/**
 *
 * @author  David Konecny
 */
public class Book implements Serializable {

    String author;
    String title;

    static final long serialVersionUID = -868500654892626305L;

    public Book() {
        author = "N/A";
        title = "N/A";
    }

    public Book(String ar, String al) {
        author = ar;
        title = al;
    }

    public static Book createDefault() {
        return new Book("Jesse, Tim & others", "NetBeans Definitive Guide");
    }

    public boolean equals(Object o) {
        if (!(o instanceof Book)) {
            return false;
        }
        Book d = (Book)o;
        return author.equals(d.author) &&
            title.equals(d.title);
    }
    
    public int hashCode() {
        // not relevant
        return 125;
    }
    
    public String toString() {
        return "Book[author="+author+", title="+title+"]"+super.toString();
    }
    
}
