/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
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
    
    private Map type2Required;
    private Map type2Optional;
    
    /** Creates a new instance of FieldDatabase */
    private FieldDatabase() {
        type2Required = new HashMap();
        type2Optional = new HashMap();
        
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
    
    public Collection/*<String>*/ getRequiredFields(String type) {
        Collection result = (Collection) type2Required.get(type.toUpperCase());
        
        if (result != null)
            return result;
        else
            return Collections.EMPTY_LIST;
    }

    public Collection/*<String>*/ getOptionalFields(String type) {
        Collection result = (Collection) type2Optional.get(type.toUpperCase());
        
        if (result != null)
            return result;
        else
            return Collections.EMPTY_LIST;
    }
    
    public Collection/*<String>*/ getKnownTypes() {
        return type2Required.keySet();
    }
}
