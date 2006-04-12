/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;
import org.openide.ErrorManager;

import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author  Jan Lahoda
 */
public class DictionaryImpl implements Dictionary {
    
    private Locale locale;
    private String suffix;
    
    /**
     * Creates a new instance of DictionaryImpl
     */
    public DictionaryImpl(Locale locale, String suffix, List<InputStream> streams) {
        this.locale = locale;
        this.suffix = suffix;
        loadDictionary(streams);
    }
    
    private void loadDictionary(List<InputStream> streams) {
        for (InputStream in : streams) {
            BufferedReader reader = null;
            
            try {
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                
                in = null;
                
                String line = null;
                
                while ((line = reader.readLine()) != null) {
                    addEntry(line);
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        
        Collections.sort(getDictionary(), new Comparator() {
            public int compare(Object obj1, Object obj2) {
                return ((String) obj1).compareToIgnoreCase((String) obj2);
            }
        });
        
    }
    
    private List<String> dictionary = null;
    private StringBuffer dictionaryText = null;
    
    public int findLesser(String word) {
        List dict = getDictionary();
        
        int lower = 0;
        int upper = dict.size() - 1;
        
        boolean last = false;
        
        while (true) {
            if (lower == upper)
                break;
            
            if (last)
                break;
            
            if ((upper - lower) == 1)
                last = true;
            
            int current = (lower + upper) / 2;
            String currentObj = (String) dict.get(current);
            
            int result = currentObj.compareToIgnoreCase(word);
            
            if (result == 0)
                return current;
            
            if (result < 0) {
                lower = current + 1;
            }
            
            if (result > 0) {
                upper = current - 1;
            }
        }
        
        if (((String )dict.get(lower)).compareToIgnoreCase(word) == 0)
            return lower;
        else
            return (lower + 1) < dict.size() ? lower + 1 : lower;
    }
    
    public ValidityType findWord(String word) {
        String str = (String) getDictionary().get(findLesser(word.toLowerCase()));
//            System.err.println("str=" + str);
        if (str.startsWith(word.toLowerCase())) {
            if (str.length() == word.length())
                return ValidityType.VALID;
            else
                return ValidityType.PREFIX_OF_VALID;
        } else
            return ValidityType.INVALID;
    }
    
    protected synchronized List<String> getDictionary() {
        if (dictionary == null)
            dictionary = new ArrayList<String>();
        
//            System.err.println("returning dictionary=" + System.identityHashCode(dictionary));
        return dictionary;
    }
    
    protected synchronized StringBuffer getDictionaryText() {
        if (dictionaryText == null) {
            dictionaryText = new StringBuffer();
            dictionaryText.append('\n');
        }
        
        return dictionaryText;
    }
    
    protected void addEntry(String entry) {
        getDictionary().add(entry);
        getDictionaryText().append(entry);
        getDictionaryText().append('\n');
    }
    
    public List<String> completions(String word) {
        if ("".equals(word))
            return Collections.emptyList();
        
        int start = findLesser(word);
        
//            if (!((String )getDictionary().get(start)).equalsIgnoreCase(word)) {
//                start++;
//            }
        
        int end   = findLesser(word.substring(0, word.length() - 1) + (char) (word.charAt(word.length() - 1) + 1));
        
        return getDictionary().subList(start, end/* + 1*/);
    }
    
    private static class Pair {
        private int distance;
        private String proposedWord;
        
        public Pair(String proposedWord, int distance) {
            this.distance = distance;
            this.proposedWord = proposedWord;
        }
    }
    
    private static class SimilarComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
            Pair p1 = (Pair) o1;
            Pair p2 = (Pair) o2;
            
            if (p1.distance < p2.distance)
                return (-1);
            
            if (p1.distance > p2.distance)
                return 1;
            
            return 0;
        }
        
    }
    
    private static int MINIMAL_SIMILAR_COUNT = 3;
    
    public List<String> getSimilarWords(String word) {
        List proposal = dynamicProgramming(word, dictionaryText, 5);
        List<String> result   = new ArrayList<String>();
        
        //future:
//            if (Character.isLowerCase(word.charAt(0)))
//                return result;
        
        Collections.sort(proposal, new SimilarComparator());
        
        Iterator words = proposal.iterator();
        int      proposedCount = 0;
        int      lastDistance = 0;
        
        while (words.hasNext()) {
            Pair pair = (Pair) words.next();
            
            if (proposedCount >= MINIMAL_SIMILAR_COUNT && lastDistance != pair.distance)
                continue;
            
            result.add(pair.proposedWord);
            proposedCount++;
            lastDistance = pair.distance;
        }
        
        return result;
    }
    
    private static List/*<String>*/ dynamicProgramming(String pattern, CharSequence text, int distance) {
        List/*<String>*/ result = new ArrayList/*<String>*/();
        pattern = pattern.toLowerCase();
        
        int[] old = new int[pattern.length() + 1];
        int[] current = new int[pattern.length() + 1];
        int[] oldLength = new int[pattern.length() + 1];
        int[] length = new int[pattern.length() + 1];
        
        for (int cntr = 0; cntr < old.length; cntr++) {
            old[cntr] = distance + 1;//cntr;
            oldLength[cntr] = (-1);
        }
        
        current[0] = old[0] = oldLength[0] = length[0] = 0;
        
        int currentIndex = 0;
        
        while (currentIndex < text.length()) {
            for (int cntr = 0; cntr < pattern.length(); cntr++) {
                int insert = old[cntr + 1] + 1;
                int delete = current[cntr] + 1;
                int replace = old[cntr] + ((pattern.charAt(cntr) == text.charAt(currentIndex)) ? 0 : 1);
                
                if (insert < delete) {
                    if (insert < replace) {
                        current[cntr + 1] = insert;
                        length[cntr + 1] = oldLength[cntr + 1] + 1;
                    } else {
                        current[cntr + 1] = replace;
                        length[cntr + 1] = oldLength[cntr] + 1;
                    }
                } else {
                    if (delete < replace) {
                        current[cntr + 1] = delete;
                        length[cntr + 1] = length[cntr];
                    } else {
                        current[cntr + 1] = replace;
                        length[cntr + 1] = oldLength[cntr] + 1;
                    }
                }
            }
            
            if (current[pattern.length()] <= distance) {
                int start = currentIndex - length[pattern.length()] + 1;
                int end   = currentIndex + 1;
                
                if ((start == 0 || text.charAt(start - 1) == '\n') && text.charAt(end) == '\n') {
                    String occurence = text.subSequence(start, end).toString();
                    
                    if (occurence.indexOf('\n') == (-1) && !pattern.equals(occurence)) {
                        result.add(new Pair(occurence, current[pattern.length()]));
                    }
                }
            }
            
            currentIndex++;
            
            int[] temp = old;
            
            old = current;
            current = temp;
            
            temp = oldLength;
            
            oldLength = length;
            length = temp;
        }
        
        return result;
    }

    public ValidityType validateWord(CharSequence word) {
        return findWord(word.toString());
    }

    public List<String> findValidWordsForPrefix(CharSequence word) {
        return Collections.emptyList();
    }

    public List<String> findProposals(CharSequence word) {
        return getSimilarWords(word.toString());
    }

    public String getSuffix() {
        return suffix;
    }
}
