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

/*
 * Abbreviations.java
 *
 * Created on August 18, 2002, 2:07 PM
 */

package org.netbeans.modules.javanavigators;
import java.util.*;
/**
 * A class containing mostly procedural code to intelligently (readably) abbreviate java class member names.
 * @author  tboudreau
 */
final class Abbreviations {
    static boolean html=true;
    private static final boolean isSeparator (char c) {
        return c=='_' || c=='-' || c=='.' || c=='(' || c==')' || c=='[' || c==']' || c=='{' || c=='}' || Character.isWhitespace(c);
    }

    private static final int getConsonantWeight(char c) {
        int result = isPlosive(c) ? 3 : 0;
        if (result==0) result = isFricative(c) ? 2:0;
        if (result==0) result = isVowel(c) ? 0 : 1;
        result += isUnvoiced(c) ? 2:1;
        return result;
    }
    
    private static final boolean isImportant (char c) {
        return c == ')' || c == '(' || c=='.';
    }
    
    private static final boolean isPlosive(char c) {
        return 
            c=='t' || c=='b' || c=='p' || c=='d' || c=='k' || c=='c' || c=='g' || c=='q' || c=='x' ||
            c=='T' || c=='B' || c=='P' || c=='D' || c=='K' || c=='C' || c=='G' || c=='Q' || c=='X';
            //well, x isn't plosive, but it might as well be
    }
    
    private static final boolean isConsonant(char c) {
        return Character.isLetter(c) && !isVowel(c);
    }
    
    private static final boolean isFricative(char c) {
        return c=='s' || c=='f' || c=='z' || c=='j' || c=='v' ||
            c=='S' || c=='F' || c=='Z' || c=='J' || c=='V';
    }
    
    private static final boolean isUnvoiced(char c) {
        return c=='f' || c=='h' || c=='k' || c=='c' || c=='p' || c=='s' || c=='t' || c=='x' ||
            c=='F' || c=='H' || c=='K' || c=='C' || c=='P' || c=='S' || c=='T' || c=='X';
    }
    
    private static final boolean isVoiced(char c) {
        return Character.isLetter(c) && (isVowel(c) || !isUnvoiced(c));
    }
    
    private static final boolean isPairStarter(char c) {
        return c=='p' || c=='g' || c=='s' || c=='t' || c=='d' || c=='c' ||
        c=='P' || c=='G' || c=='S' || c=='T' || c=='D' || c=='C';
    }
    
    private static final boolean isVowel(char c) {
        return c=='a' || c=='e' || c=='i' || c=='o' || c=='u' || c=='y' ||
        c=='A' || c=='E' || c=='I' || c=='O' || c=='U' || c=='Y'; //and we'll call y a vowel
    }
    
    private static final boolean isPairEnder(char c) {
        return c=='r' || c=='h' || c=='R' || c=='H';
    }
    
    private static final boolean isDipthong(char a, char b) {
        return (a == 'p' || a == 'P' || a=='t' || a=='s' || a=='c' || a=='T' || a=='S' || a=='C') && ((b=='h') || (b=='H'));
    }
    
    private static final boolean isSoftVoiced (char c) {
        return (c=='r' || c=='n' || c=='l' || c=='m' || c=='w' ||
            c=='R' || c=='N' || c=='L' || c=='M' || c=='W');
    }
    
    private static final boolean isHard (char c) {
        return (c=='g' || c=='b' || c=='p' || c=='c' || c=='d' || c=='j' || c=='k' || c=='q' || c=='t' || c=='v' ||
            c=='G' || c=='B' || c=='P' || c=='C' || c=='D' || c=='J' || c=='K' || c=='Q' || c=='T' || c=='V'        
        );
    }
    
    private static final boolean diffCase(char a, char b) {
        return Character.isUpperCase(a) == Character.isUpperCase(b);
    }
    
    public static StringBuilder abbreviate(StringBuilder s, int targetLength) {
        if (s.length() <= targetLength) {
            return s;
        }
        int len = s.length();
        if (len <= 3) return s;
        StringBuilder result = new StringBuilder();
        
        char[] ch = new char[len];
        int[] w = new int[len];
        s.getChars(0, len, ch, 0);
        int[] counts = weightLetters(ch, w);
        
        int threshold;
        
//        I am counting backwards
        
        for (threshold=0; threshold < counts.length; threshold++) {
//            System.out.println("Counts[" + threshold + "] = " + counts[threshold]);
            if (counts[threshold] <= targetLength) {
//                threshold = Math.max (0, threshold);
                break;
            }
        }
        char lastChar=' ';
//        System.out.println("Using threshold " + threshold + " targetLength = " + targetLength + " count will be " + counts[threshold] + " original length=" + s.length() );
        for (int i=0; i < ch.length; i++) {
            if ((ch.length - i) + result.length() <= targetLength) {
                result.append (ch[i]);
            } else {
                if (w[i] >= threshold) {
                    if (isSeparator(lastChar) && isSeparator(ch[i])) {
                    } else {
                        result.append (ch[i]);
                    }
                    lastChar = ch[i];
                }
            }
        }
        
//        System.out.println("Abbreviated to: " + result.toString());
        return result;
    }
    
    
    static int[] weightLetters (final char[] c, final int[] result) {
        Arrays.fill (result, 0);
//        Arrays.fill (ruleCallCounts, 0); //XXX debug
        int[] counts = new int[] {0,0,0,0,0,0,0,0,0,0,0 };
        int finalSepPos = 0;
        for (int i=c.length-1; i >= 0; i--) {
            if (c[i] == '.' || c[i] == '_' || c[i] == '-') {
                finalSepPos = i;
                break;
            }
        }
        
        /*
        result[0]=5;
        if (c [len-1] != 'e') {
            result[len-1]=5;
        } else {
            result[len-1]=3;
            result[len-2]=5;
        }
         */
        char prev='`';
        char curr;
        boolean inTag = html & c[0] =='<';
        int lastSepPosition = 0;
        for (int i=0; i < c.length; i++) {
            int currWeight=0;
            int tally=0;
            if (!inTag) {
                for (int r=0; r < rules.length; r++) {
                    currWeight = Math.max(0, rules[r].getWeight(c, i));
                    ruleWeights[r] = currWeight;
                    ruleCallCounts[r] += currWeight != 0 ? 1 : 0;
                    tally += currWeight;
                    if (rules[r].influencesOthers() && (currWeight != 0)) {
                        tally = currWeight;
                        for (int xr = r-1; xr > 0; xr--) {
                            int effect = rules[r].affects(rules[xr]);
                            switch (effect) {
                                case Rule.IGNORES : tally += ruleWeights[xr];
                                                    break;
                                case Rule.NULLIFIES : if (ruleWeights[xr] != 0) {
                                                        ruleWeights[xr] = 0;
//                                                        System.out.println("Rule " + rules[r].getClass().getName() + " NULLIFYING " + rules[xr].getClass().getName());
                                                    }
                                                    break;
                                case Rule.AUGMENTS : ruleWeights[xr] *=2;
//                                                    System.out.println("Rule " + rules[r].getClass().getName() + " AUGMENTING " + rules[xr].getClass().getName());
                                                    tally += ruleWeights[xr];
                                                    break;
                                case Rule.NEGATES : ruleWeights[r] *=-1;
//                                                    System.out.println("Rule " + rules[r].getClass().getName() + " NEGATING " + rules[xr].getClass().getName());
                                                    tally += ruleWeights[xr];
                                                    break;
                            }
                        }
                    }
                }
                if (((i - lastSepPosition) > 3) && i < finalSepPos) {
                    tally = Math.max (tally-2, 0);
                } else {
                    if (finalSepPos != 0) tally++;
                }
                
                result[i] += tally;
                int countIdx = Math.max (0, Math.min (result[i], 9));
//                System.out.println("CountIDX is " + countIdx + " for " + resuPrev lt[i] + " counts[" + countIdx + " will be " + (counts[countIdx]+1));

                counts[countIdx] += 1;
                if (!isSeparator(c[i])) {
                    lastSepPosition ++;
                } else {
                    lastSepPosition = i;
                }
            } else {
//                System.out.println(c[i] + " in tag - " + i);
                result[i] = 25;
            }
            if (html) {
                if (inTag) {
                    inTag = c[i] !='>';
                }
                if (i < c.length-1) {
                    inTag |= c[i+1]=='<';
                }
            }
            /*
            if (html && (i < c.length-1)) {
                inTag = (inTag || (c[i+1] == '<')) && !(inTag && (c[i+1] != '>'));
            }
             */
        }
        int total=0;
        for (int i=counts.length-2; i >=0; i--) {
            total+=counts[i];
            counts[i] += counts[i+1];
  //          System.out.println("Counts[" + i + "] = " + counts[i]);
        }
//        System.out.println("Original length: " + c.length + " counts total: " + total);
        
        /*
        StringBuffer sb1=new StringBuffer();
        StringBuffer sb2=new StringBuffer();
        for (int i=0; i < c.length; i++) {
           sb1.append (c[i]);
           sb2.append (Integer.toString(result[i],16));
        }
        System.out.println(sb1.toString());
        System.out.println(sb2.toString());
        System.out.println("");
         */
//        for (int i=0; i < counts.length; i++) {
//            System.out.println("Threshold: " + i + " count: " + counts[i]);
//        }
        
        return counts;
    }
    
    
    
    static abstract class Rule {
        protected static final int AUGMENTS=1;
        protected static final int NULLIFIES=0;
        protected static final int IGNORES=Integer.MIN_VALUE;
        protected static final int NEGATES=-1;
        
        public abstract int getWeight (char[] c, int i);
        
        public int affects (Rule other) {
            return IGNORES;
        }
        
        protected boolean influencesOthers() {
            return false;
        }
        
        protected static final char prev (char[] c, int i) {
            char result = (i > 0) && (i < c.length) ? c[i-1] : '`'; //NOI18n
            if (html && (result=='>')) {
               while (i > 0) {
                   i--;
                   if (c[i] == '<') {
                       i--;
                       if (i >=0) {
                           return c[i];
                       }
                   }
               }
               return ' ';
            }
//            System.out.println("Prev for " + i + "(" + c[i] + ") returning " + result);
            return result;
        }
        
        protected static final char next (char[] c, int i) {
            char result = (i >= 0) && (i < c.length-1) ? c[i+1] : '`'; //NOI18n
            if (html && (result=='<')) {
               while (i > 0) {
                   i--;
                   if (c[i] == '>') {
                       i++;
                       if (i < c.length) {
                           return c[i];
                       }
                   }
               }
               return ' ';
            }
//            System.out.println("Next for " + i + "(" + c[i] + ") in " + new String (c) + " returning " + result);
            return result;
        }
        
        protected static final boolean prevIsSame (char[] c, int i) {
            return prev(c, i) == c[i];
        }
        
        protected static final boolean isLast (char[] c, int i) {
            return i == c.length-1;
        }
        
        protected static final boolean isFirst (char[] c, int i) {
            return i==0;
        }
        
        protected static final boolean isCapital (char c) {
            return Character.isUpperCase(c);
        }
    }
    /*
    
    static final class CheapVowelsRule extends Rule {
        public int getWeight(char[] c, int i) {
            return isVowel(c[i]) ?
                isVowel(prev(c,i)) ? -3 : -2
                : 0;
        }
    }
    
    static final class SeparatorsRule extends Rule {
        public boolean influencesOthers() {
            return true;
        }
        public int affects (Rule other) {
            return other instanceof CheapVowelsRule ? NULLIFIES : IGNORES;
        }
        public int getWeight(char[] c, int i) {
            return isSeparator(c[i]) ? 6 : c[i]==',' ? 7 : 0;
        }
    }
    
    static final class ImportantCharactersRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (isCapital(c[i])) {
                if ((!isCapital(prev(c,i)))) {
                    return 2;
                }   
            } else {
                if (isSeparator(prev(c,i))) {
                    return 2;
                }
            }
            return 0;
        }
    }
    
    static final class EURule extends Rule {
        public int getWeight(char[] ch, int i) {
            if (ch[i]=='e' || ch[i]=='E' || ch[i]=='u' || ch[i]=='U') {
                return -1;
            }
            return 0;
        }
    }
    
    static final class PlosivesRule extends Rule {
        public int getWeight(char[] c, int i) {
            return isPlosive(c[i]) ? 
                (prevIsSame (c,i) ? 0 : 1) : 1;
        }
    }
    
    static final class LetterPairRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (c[i] == prev (c,i)) {
                return -3;
            }
            return 0;
        }
    }
    
    static final class FricativeAfterPlosiveRule extends Rule {
        public int getWeight(char[] c, int i) {
            return isFricative(c[i]) ?
                isPlosive (prev(c,i)) ? -1 : 0 : 0;
        }
    }
    
    static final class FricativeBeforePlosiveRule1 extends Rule {
        public int getWeight(char[] c, int i) {
            return isPlosive(c[i]) ?
                isFricative(prev(c,i)) ? 1 : 0 : 0;
        }
    }
    
    static final class FricativeBeforePlosiveRule2 extends Rule {
        public int getWeight(char[] c, int i) {
            return isFricative(c[i]) ?
                isPlosive (next(c,i)) ? -1 : 1 : 0;
        }
    }
    
    static final class DipthongRule1 extends Rule {
        public boolean influencesOthers() {
            return true;
        }
        public int affects (Rule other) {
            return other instanceof UnvoicedRule ? NULLIFIES : IGNORES;
        }
        public int getWeight(char[] c, int i) {
            return isDipthong (c[i], next(c,i)) ?
                2 : 0;
        }
    }
    
    static final class DipthongRule2 extends Rule {
        public boolean influencesOthers() {
            return true;
        }
        public int affects (Rule other) {
            return other instanceof UnvoicedRule ? NULLIFIES : IGNORES;
        }
        public int getWeight(char[] c, int i) {
            return isDipthong (prev(c,i), c[i]) ?
                -1 : 0;
        }        
    }
    
    static final class VowelPlusYRule1 extends Rule {
        public int getWeight(char[] c, int i) {
            final char nx = next (c,i);
            return isVowel (c[i]) && (nx == 'y' || nx == 'Y') ? -2 : 0;
        }
    }
    
    static final class VowelPlusYRule2 extends Rule {
        public int getWeight(char[] c, int i) {
            return isVowel (prev(c,i)) && (c[i] =='y' || c[i]=='Y') ? 2 : 0;
        }
    }
    
    static final class TrailingERule extends Rule {
        public int getWeight(char[] c, int i) {
            if ((i == c.length-1) && (c[i] == 'e' || c[i] == 'E')) {
                return -2;
            } else {
                if (c[i] == 'e' || c[i] == 'E') {
                    if (isConsonant(prev(c,i)) && (isConsonant(next(c,i)))) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
                return 0;
            }
        }
    }
    
    static final class MultiCapRule extends Rule {
        public boolean influencesOthers() {
            return true;
        }
        public int affects (Rule other) {
            return other instanceof CapRule ? NULLIFIES : IGNORES;
        }
        public int getWeight(char[] c, int i) {
            boolean prevCap = Character.isUpperCase(prev (c,i)) || isImportant(prev(c,i)) || isSeparator(prev(c,i));
            boolean nextCap = Character.isUpperCase(next(c,i)) || isImportant(next(c,i)) || isSeparator(next(c,i));
            boolean isCap = Character.isUpperCase(c[i]);// || isImportant(c[i]) || isSeparator(c[i]);
            return prevCap && nextCap && isCap ? -2 : 0;
        }
    }    

    static final class UnvoicedRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (isUnvoiced(c[i])) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    
    static final class ConsonantWeightRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (isConsonant(c[i])) {
                return getConsonantWeight(c[i]);
            }
            return 0;
        }
    }
    
    static final class CapRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (Character.isUpperCase(c[i])) {
                return 1;
            }
        return 0;
        }
    }
     */
    
    
    
    static final class PlosivesRule extends Rule {
        public int getWeight(char[] c, int i) {
            return isPlosive(c[i]) ? 
                (prevIsSame (c,i) ? 0 : 1) : 0;
        }
    }    
    
    static final class HardRule extends Rule {
        public int getWeight(char[] c, int i) {
            return isHard(c[i]) ? 
                (prevIsSame (c,i) ? 0 : 4) : 0;
        }
    }        
    
    static final class SoftVoicedRule extends Rule {
        public int getWeight(char[] c, int i) {
            return isSoftVoiced(c[i]) ? 
                (prevIsSame (c,i) ? 0 : 1) : 0;
        }
    }

    static final class ConsonantRule extends Rule {
        public int getWeight(char[] c, int i) {
            return isSoftVoiced(c[i]) ? 
                (prevIsSame (c,i) ? 0 : 3) : 0;
        }
    }            
    
    static final class FricativeRule extends Rule {
        public int getWeight(char[] c, int i) {
            return isFricative(c[i]) ? 
                3 : 0;
        }
    }            

    static final class VowelRule extends Rule {
        public int getWeight(char[] c, int i) {
            if ((c[i] == 'e' || c[i] == 'E') && i==c.length-1 || isSeparator(next(c,i)) || diffCase(c[i], next(c,i))) {
                return -1;
            }
            return isVowel(c[i]) ? 
                c[i] == 'e' || c[i] == 'u' || c[i]=='E' || c[i]=='U'
                ? 1 : 2 : 0;
        }
    }
    

    static final class DipthongRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (diffCase (c[i], prev(c,i))) return 0;
            return isDipthong (prev(c,i), c[i]) ?
                3 : 0;
        }
    }
    
    static final class LetterPairRule extends Rule {
        public boolean influencesOthers() {
            return true; //XXX
        }
        @Override
        public int affects (Rule other) {
            return other instanceof FirstCharacterRule ? AUGMENTS : IGNORES;
        }
        public int getWeight(char[] c, int i) {
            if (diffCase (c[i], prev(c,i))) return 0;
            if (c[i] == prev (c,i)) {
                return -3;
            }
            return 0;
        }
    }    
    
    static final class DipthongRule1 extends Rule {
        public boolean influencesOthers() {
            return true; //XXX
        }
        @Override
        public int affects (Rule other) {
            return other instanceof PlosivesRule ? NULLIFIES : IGNORES;
        }
        public int getWeight(char[] c, int i) {
            if (diffCase (c[i], next(c,i))) return 0;
            return isDipthong (c[i], next(c,i)) ?
                4 : 0;
        }
    }
    
    static final class FirstCharacterRule extends Rule {
        public boolean influencesOthers() {
            return true; //XXX
        }
        @Override
        public int affects (Rule other) {
            return AUGMENTS;
        }
        public int getWeight(char[] c, int i) {
            return i==0 ? 10 : isSeparator(prev(c,i)) ? 7 : 
                Character.isUpperCase(c[i]) && !Character.isUpperCase(prev(c,i))
                ? 4: isSeparator(c[i]) ? 6 : 0;
        }
    }
    
    static final class CommaRule extends Rule {
        public boolean influencesOthers() {
            return true;
        }
        @Override
        public int affects (Rule other) {
            return NULLIFIES;
        }
        public int getWeight(char[] c, int i) {
            if (isSeparator(c[i]) && next(c,i)==',') {
                return 1;
            }
            return 0;
        }
    }    
    
    

    static final class NGKRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (diffCase (c[i], next(c,i))) return 0;
            if (isSoftVoiced (c[i]) && isHard(next(c,i))) {
                return 1;
            }
            return 0;
        }
    }    

    static final class CKRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (c[i] =='c' || c[i] == 'C') {
            if (diffCase (c[i], next(c,i))) return 0;
                char nxt = next(c,i);
                if (nxt=='k' || nxt=='K') {
                    return -2;
                }
            }
        return 0;
        }
    }     
    
    static final class VowelAfterSoftVoicedRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (diffCase (c[i], prev(c,i))) return 0;
            if (isSoftVoiced(prev(c,i)) && isVowel(c[i])) {
                return -2;
            }
            return 0;
        }
    }     
    
    static final class SoftVoicedBeforeVowelRule extends Rule {
        public int getWeight(char[] c, int i) {
            if (diffCase (c[i], prev(c,i))) return 0;
            if (isVowel(c[i]) && isSoftVoiced(prev(c,i))) {
                return 1;
            }
            return 0;
        }
    } 
    
    static final class EBeforeARule extends Rule {
        public boolean influencesOthers() {
            return true;
        }
        @Override
        public int affects (Rule other) {
            return other instanceof VowelRule ? NULLIFIES : IGNORES;
        }
        public int getWeight(char[] c, int i) {
            if (diffCase (c[i], next(c,i))) return 0;
            return (c[i] == 'e' || c[i] == 'E') && (next(c,i) == 'a' || next(c,i) == 'A') ?
                2 : 0;
        }
    }
    
    private static final Rule[] rules = new Rule[] {
//        new CheapVowelsRule(),
        new ConsonantRule(),
        new SoftVoicedRule(),
        new PlosivesRule(),
        new VowelRule(),
        new FricativeRule(),
//        new FricativeAfterPlosiveRule(),
//        new FricativeBeforePlosiveRule1(),
//        new FricativeBeforePlosiveRule2(),
//        new DipthongRule1(),
//        new DipthongRule2(),
//        new VowelPlusYRule1(),
//        new VowelPlusYRule2(),
//        new LetterPairRule(),
//        new UnvoicedRule(),
//        new CKRule(),
//        new NGKRule(),
//        new EURule(),
//        new CapRule(),
//        new TrailingERule(),
//        new SeparatorsRule(),
//        new MultiCapRule(),
//        new ImportantCharactersRule(),
        new CKRule(),
        new NGKRule(),
        new EBeforeARule(),
        new VowelAfterSoftVoicedRule(),
        new SoftVoicedBeforeVowelRule(),
        new DipthongRule(),
        new DipthongRule1(),
        new HardRule(),
        new LetterPairRule(),
        new FirstCharacterRule(),
        new CommaRule(),
//        new CommaRule()
    };
    private static int[] ruleWeights = new int[rules.length];
    private static int[] ruleCallCounts = new int[rules.length]; //XXX debug
    
    private static int countNonHTMLChars (String s) {
        char[] ch = s.toCharArray();
        boolean inTag = false;
        int result = 0;
        for (int i=0; i < ch.length; i++) {
            if (!inTag) {
                inTag = ch[i]=='<';
            }
            if (!inTag) result++;
            if (inTag) {
                inTag = ch[i] != '>';
            }
        }
        return result;
    }
    
    public static void main (String[] args) {
        /*
        String[] tests = new String[] {
            "ringRingRingRingSwungHung", "PROPERTY_PROPERTY", "SICK_SO_SICK"
        };
         */
        
        String[] tests = new String[] {
            "abbreviate", "truncateName", "addPropertyChangeListener",
            "removePropertyChangeListener", "MyComponent (String foo)",
            "clear", "goAway", "run", "add", "close", "someReallyLongMethodNameForNoReason",
            "DataObject.find", "Abbreviations.abbreviate", "InnerClass.InnerClass.method",
            "longMethodNameWithLotsOfArguments (String foo, int i, PropertyChangeListener pce)",
            "SHORT_CONSTANT", "THIS_IS_A_LONG_CONSTANT", "PROP_SAMPLE_PROPERTY",
            "FoogleBarf.PROP_SAMPLE_PROPERTY", "InnerClass.AnotherInnerClass.addPropertyChangeListener",
            "a.b.c.doSomething", "A.B.C.DoSomething", "createRoot", "createActions", "SomeInnerClass.createRoot", "SomeInnerClass.createActions"
        };
 /*
        String[] tests = new String[] {"alpha.beta.Charlie.DoSomething", "a.b.c.doSomething",
        "addPropertyChangeListener"};

 */
 
/*        String[] tests = new String[] {
            "<b>abbreviate</b>", "<b><font color='!green'>someReallyLongMethodName</font></b>","someReallyLongMethodName",
            "<I>PROPERTY_blue</I>"
 
        };*/
        String curr;
        boolean success;
        for (int i=0; i < tests.length; i++) {
            int origCt = countNonHTMLChars(tests[i]);
            for (int j=0; j <= origCt; j++) {
//            int j=10;
                curr = abbreviate(new StringBuilder(tests[i]), j).toString();
                int ct = countNonHTMLChars(curr) ;
                
                success = ct <= origCt;
//                System.out.println(curr + "\n");
                System.out.println(j+ ":" +tests[i] + " -" + origCt + "->" + ct + ": " + curr + (success ? " SUCCESS" : " FAIL"));
            }
        }
        
        for (int i=0; i < ruleCallCounts.length; i++) {
            String s = rules[i].getClass().getName();
            s = s.substring (s.lastIndexOf("$") + 1) + ":";
            System.out.println(s + ruleCallCounts[i]);
        }
    }
}
