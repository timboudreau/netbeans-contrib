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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
//import org.openide.ErrorManager;

/**
 *
 * @author Jan Lahoda
 */
public final class Command extends NamedAttributableWithArguments {

    private static final String tagMathSt     = "math";
    private static final String tagPreambleSt = "preamble";
    private static final String tagInputSt    = "input";
    private static final String tagPARSt      = "par";
    private static final String tagBeginSt    = "begin";
    private static final String tagEndSt      = "end";
    private static final String tagLabelSt    = "label";
    
    /*package private*/Command() {
        super();
    }
    
    public Command(String command, int mandatoryArgumentsCount, boolean isFirstNonMandatory) {
        this();
        setName(command);
        
        for (int cntr = 0; cntr < mandatoryArgumentsCount; cntr++) {
            if (cntr == 0 && isFirstNonMandatory)
                getArguments().add(new Param(Param.NONMANDATORY));
            else
                getArguments().add(new Param(Param.MANDATORY));
        }
    }

    public Command(String inputLine) {
        this();
        StringTokenizer tokenizer = new StringTokenizer(inputLine, ":");
        
        String commDef = tokenizer.nextToken();
        String tagsSt  = tokenizer.nextToken();
        
        ParsePosition pp = new ParsePosition(0);
        
        while ((pp.getIndex() < commDef.length()) && ("{[\'".indexOf(commDef.charAt(pp.getIndex())) == (-1)))
            pp.setIndex(pp.getIndex() + 1);
        
        setName(commDef.substring(0, pp.getIndex()));
        
//        System.err.println("command=\"" + command + "\".");
        
        while (pp.getIndex() < commDef.length()) {
            getArguments().add(new Param(commDef, pp));
        }
        
        StringTokenizer commaTokenizer = new StringTokenizer(tagsSt, ",");
        
        while (commaTokenizer.hasMoreTokens()) {
            String tag = commaTokenizer.nextToken();
            
//            System.err.println("tag=" + tag);
            
            getAttributes().put(tag, "true");
        }
    }
    
    public String getCommand() {
        return getName();
    }
    
    public boolean isMath() {
        return hasAttribute(tagMathSt);
    }
    
    public boolean isPreamble() {
        return hasAttribute(tagPreambleSt);
    }
    
    public boolean isInputLike() {
        return hasAttribute(tagInputSt);
    }
    
    public boolean isPARLike() {
        return hasAttribute(tagPARSt);
    }
    
    public boolean isBeginLike() {
        return hasAttribute(tagBeginSt);
    }
    
    public boolean isEndLike() {
        return hasAttribute(tagEndSt);
    }
    
    public boolean isLabelLike() {
        return hasAttribute(tagLabelSt);
    }
    
    public String toString() {
        return getCommand();
    }
    
    public static final class Param extends AttributableImpl {
        
        public static final int FREE = 0;
        public static final int MANDATORY = 1;
        public static final int NONMANDATORY = 2;
        public static final int SPECIAL = 3;
        
        public static final int ENUM = 0;
        public static final int CODE = 1;
        public static final int TEXT = 2;
        public static final int INVALID = -1;
        
        public static final String ATTR_NO_PARSE = "#no-parse";
        
        private static final String textMark = "#text";
        private static final String codeMark = "#code";
        
        private int     type;
//        private boolean canContainText;
//        private boolean canContainCode;
//        private boolean canBeEnum;
        
        private Set values;
        
        /*package private*/ void setType(int type) {
            this.type = type;
        }
        
        /*package private*/ synchronized void addValue(String value) {
            if (values == null)
                values = new HashSet();
            
            values.add(value);
        }
        
//        public Param(String parseFrom) {
//            this(parseFrom, new ParsePosition(0));
//        }
        
        /**Only for use by the SAX parser.*/
        /*package private*/ Param() {
        }

        /*package private*/ Param(int type) {
            this.type = type;
            
            getAttributes().put(textMark, "true");
            getAttributes().put(codeMark, "true");
        }
        
        public Param(String parseFrom, ParsePosition index) {
            char end = '\0';
            
            switch (parseFrom.charAt(index.getIndex())) {
                case '[':
                    type = NONMANDATORY;
                    end  = ']';
                    break;
                case '{':
                    type = MANDATORY;
                    end  = '}';
                    break;
                case '\'':
                    type = FREE;
                    end  = '\'';
                    break;
                default:
                    throw new IllegalArgumentException("Unknown parameter type: " + parseFrom);
            };
            
            int endIndex = index.getIndex() + 1;
            
            while (parseFrom.charAt(endIndex) != end)
                endIndex++;
            
//            System.err.println("To parse:\"" + parseFrom.substring(index.getIndex() + 1, endIndex) + "\"");
            
            StringTokenizer t = new StringTokenizer(parseFrom.substring(index.getIndex() + 1, endIndex),  "|");
            
            while (t.hasMoreTokens()) {
                String token = t.nextToken();
                
                if (token.length() > 0 && token.charAt(0) == '#') {
                    getAttributes().put(token, "true");
                    continue;
                }
                
//                if (textMark.equals(token)) {
//                    canContainText = true;
//                    continue;
//                }
//                
//                if (codeMark.equals(token)) {
//                    canContainCode = true;
//                    continue;
//                }
//                
//                canBeEnum = true;
                
                getValues().add(token);
//                System.err.println("token=" + token);
            }

            index.setIndex(endIndex + 1);
        }
        
        public int isValid(CharSequence text) {
//            System.err.println("isValid for text=\"" + text + "\"");
            if ((values != null) && (values.contains(text.toString()))) {
                return ENUM;
            }
            
            if (isCodeLike())
                return CODE;
            
            if (isTextual())
                return TEXT;
            
            return INVALID;
        }
        
        /**Returns the type of this parameter.
         *
         * @return type of this parameter: MANDATORY, NONMANDATORY, FREE, SPECIAL.
         */
        public int getType() {
            return type;
        }
        
        public boolean isTextual() {
            return hasAttribute(textMark)/*|| canContainCode*/;
        }
        
        public boolean isCodeLike() {
            return hasAttribute(codeMark);
        }
        
        public boolean isEnumerable() {
            return values != null;
        }
        
        public Collection getValues() {
            if (values == null)
                return Collections.EMPTY_SET;
            else
                return values;
        }
    }
    
}
