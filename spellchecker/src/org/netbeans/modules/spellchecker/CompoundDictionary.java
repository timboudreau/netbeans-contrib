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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.spellchecker;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;

/**
 *
 * @author Jan Lahoda
 */
public class CompoundDictionary implements Dictionary {

    private Dictionary[] delegates;
    
    private CompoundDictionary(Dictionary... delegates) {
        this.delegates = delegates.clone();
    }
    
    public static Dictionary create(Dictionary... delegates) {
        return new CompoundDictionary(delegates);
    }
    
    public ValidityType validateWord(CharSequence word) {
        ValidityType result = ValidityType.INVALID;
        
        for (Dictionary d : delegates) {
            ValidityType thisResult = d.validateWord(word);
            
            Logger.getLogger(CompoundDictionary.class.getName()).log(Level.FINE, "validating word \"{0}\" using dictionary {1}, result: {2}", new Object[] {word, d.toString(), thisResult});

            if (thisResult == ValidityType.VALID || thisResult == ValidityType.BLACKLISTED) {
                return thisResult;
            }
            
            if (thisResult == ValidityType.PREFIX_OF_VALID && result == ValidityType.INVALID) {
                result = ValidityType.PREFIX_OF_VALID;
            }
        }
        
        return result;
    }

    public List<String> findValidWordsForPrefix(CharSequence word) {
        List<String> result = new LinkedList<String>();
        
        for (Dictionary d : delegates) {
            result.addAll(d.findValidWordsForPrefix(word));
        }
        
        return result;
    }

    public List<String> findProposals(CharSequence word) {
        List<String> result = new LinkedList<String>();
        
        for (Dictionary d : delegates) {
            result.addAll(d.findProposals(word));
        }
        
        return result;
    }

}
