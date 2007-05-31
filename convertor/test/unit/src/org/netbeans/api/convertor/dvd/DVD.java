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

package org.netbeans.api.convertor.dvd;


/**
 *
 * @author  David Konecny
 */
public class DVD {

    public int ID;
    public String title;
    public String publisher;
    public int price;

    public DVD() {
    }

    public DVD(int ID, String title, String publisher, int price) {
        this.ID = ID;
        this.title = title;
        this.publisher = publisher;
        this.price = price;
    }

    public boolean equals(Object o) {
        if (!(o instanceof DVD)) {
            return false;
        }
        DVD d = (DVD)o;
        return ID == d.ID &&
            title.equals(d.title) &&
            publisher.equals(d.publisher) &&
            price == d.price;
    }
    
    public int hashCode() {
        // not relevant
        return 125;
    }
    
    public String toString() {
        return "DVD[id="+ID+", title="+title+", publisher="+publisher+", price="+price+"]"+super.toString();
    }

}
