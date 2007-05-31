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

package org.netbeans.api.convertor.book;

import org.netbeans.spi.convertor.SimplyConvertible;
import java.util.Properties;


/**
 *
 * @author  David Konecny
 */
public class Book implements SimplyConvertible {

    public int ID;
    public String author;
    public String title;
    public String publisher;
    public int price;

    private boolean initialized = false;

    private static final String KEY_ID = "id";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PUBLISHER = "publisher";
    private static final String KEY_PRICE = "price";
    
    public Book() {
    }
    
    public Book(int ID, String title, String author, String publisher, int price) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
    }
    
    public void read(Properties prop) {
        if (initialized) {
            throw new RuntimeException("Cannot initialize the object more than once!");
        }
        initialized = true;
        
        ID = Integer.parseInt(prop.getProperty(KEY_ID));
        author = prop.getProperty(KEY_AUTHOR);
        title = prop.getProperty(KEY_TITLE);
        publisher = prop.getProperty(KEY_PUBLISHER);
        price = Integer.parseInt(prop.getProperty(KEY_PRICE));
    }
    
    public void write(java.util.Properties p) {
        p.setProperty(KEY_ID, Integer.toString(ID));
        p.setProperty(KEY_AUTHOR, author);
        p.setProperty(KEY_TITLE, title);
        p.setProperty(KEY_PUBLISHER, publisher);
        p.setProperty(KEY_PRICE, Integer.toString(price));
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof Book)) {
            return false;
        }
        Book b = (Book)o;
        return ID == b.ID &&
            title.equals(b.title) &&
            author.equals(b.author) &&
            publisher.equals(b.publisher) &&
            price == b.price;
    }
   
    public int hashCode() {
        // not relevant
        return 125;
    }
    
    public String toString() {
        return "Book[id="+ID+", title="+title+", author="+author+", publisher="+publisher+", price="+price+"]"+super.toString();
    }

}
