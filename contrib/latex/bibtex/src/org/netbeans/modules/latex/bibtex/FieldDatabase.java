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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  lahvac
 */
public class FieldDatabase {

    private static FieldDatabase instance;

    public static synchronized FieldDatabase getDefault() {
        if (instance == null)
            instance = new FieldDatabase();

        return instance;
    }

    private Map<String, Collection<String>> type2Required;
    private Map<String, Collection<String>> type2Optional;
    
    /** Creates a new instance of FieldDatabase */
    private FieldDatabase() {
        type2Required = new HashMap<String, Collection<String>>();
        type2Optional = new HashMap<String, Collection<String>>();
        
        type2Required.put("INPROCEEDINGS", //==CONFERENCE!
            Arrays.asList(new String[] {
                "author",
                "title",
                "booktitle",
                "year"
            })
        );
        type2Required.put("ARTICLE", 
            Arrays.asList(new String[] {
                "author",
                "title",
                "journal",
                "year"
            })
        );
        type2Required.put("BOOK", 
            Arrays.asList(new String[] {
                "author",
                "title",
                "editor",
                "publisher",
                "year"
            })
        );

        type2Optional.put("INPROCEEDINGS", 
            Arrays.asList(new String[] {
                "editor",
                "volume",
                "number",
                "series",
                "pages",
                "address",
                "month",
                "organization",
                "publisher",
                "note"
            })
        );
        type2Optional.put("ARTICLE",
            Arrays.asList(new String[] {
                "volume",
                "number",
                "pages",
                "month",
                "note"
            })
        );
        type2Optional.put("BOOK",
            Arrays.asList(new String[] {
                "volume",
                "number",
                "series",
                "address",
                "edition",
                "month",
                "note"
            })
        );
    }
    
    public Collection<String> getRequiredFields(String type) {
        Collection<String> result = type2Required.get(type.toUpperCase());
        
        if (result != null)
            return result;
        else
            return Collections.<String>emptyList();
    }

    public Collection<String> getOptionalFields(String type) {
        Collection<String> result = type2Optional.get(type.toUpperCase());
        
        if (result != null)
            return result;
        else
            return Collections.<String>emptyList();
    }
    
    public Collection<String> getKnownTypes() {
        return type2Required.keySet();
    }
}
