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
