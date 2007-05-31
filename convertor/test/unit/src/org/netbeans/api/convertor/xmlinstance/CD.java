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

package org.netbeans.api.convertor.xmlinstance;

import java.util.Properties;

/**
 *
 * @author  David Konecny
 */
public class CD {

    String artist;
    String album;

    public CD() {
        artist = "TBD";
        album = "UNKNOWN";
    }

    public CD(String ar, String al) {
        artist = ar;
        album = al;
    }

    static public CD createDefault() {
        return new CD("V.A.", "Radio 1 Essential Mixes");
    }

    static public CD create(Properties p) {
        return new CD(p.getProperty("artist"), p.getProperty("album"));
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof CD)) {
            return false;
        }
        CD d = (CD)o;
        return artist.equals(d.artist) &&
            album.equals(d.album);
    }
    
    public int hashCode() {
        // not relevant
        return 125;
    }
    
    public String toString() {
        return "CD[artist="+artist+", album="+album+"]"+super.toString();
    }
    
}
