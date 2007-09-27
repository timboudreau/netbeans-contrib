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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.text.ParsePosition;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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
        
        private Set<String> values;
        
        /*package private*/ void setType(int type) {
            this.type = type;
        }
        
        /*package private*/ synchronized void addValue(String value) {
            if (values == null)
                values = new HashSet<String>();
            
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
        
        public Collection<String> getValues() {
            if (values == null)
                return Collections.<String>emptySet();
            else
                return values;
        }
    }
    
}
