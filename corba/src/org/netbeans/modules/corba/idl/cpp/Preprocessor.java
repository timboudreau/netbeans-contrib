/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.idl.cpp;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.TopManager;
import org.netbeans.modules.corba.utils.FileUtils;
import org.openide.execution.NbClassPath;

/*
 * @author David Kaspar
 * @version 0.01, Feb 22 2001
 */

public class Preprocessor {
    
    /* known bugs:
     * 1) #pragma once
     * 2) endless octal and hex character
     * 3) recursive macro definition
     */
    
    public static class PreprocessorException extends Exception {
        int line;
        
        public PreprocessorException (int _line, String message) {
            super (message);
            line = _line;
        }
        
        public int getLine () {
            return line;
        }
    }
    
    static final int START = 0;
    static final int NORMAL = 1;
    static final int BLOCK_COMMENT = 2;
    
    static final int MASK_SKIP = 1;
    static final int MASK_ELSE = 2;
    static final int MASK_AVAIL = 4;
    
    static final int CMD_NONE = 0;
    static final int CMD_UNKNOWN = 1;
    static final int CMD_SKIP = 2;
    static final String LBL_DEFINE = "define";
    static final int CMD_DEFINE = 3;
    static final String LBL_UNDEF = "undef";
    static final int CMD_UNDEF = 4;
    static final String LBL_INCLUDE = "include";
    static final int CMD_INCLUDE = 5;
    static final String LBL_LINE = "line";
    static final int CMD_LINE = 6;
    static final String LBL_IFDEF = "ifdef";
    static final int CMD_IFDEF = 7;
    static final String LBL_IFNDEF = "ifndef";
    static final int CMD_IFNDEF = 8;
    static final String LBL_ELSE = "else";
    static final int CMD_ELSE = 9;
    static final String LBL_ENDIF = "endif";
    static final int CMD_ENDIF = 10;
    static final String LBL_IF = "if";
    static final int CMD_IF = 11;
    static final String LBL_ELIF = "elif";
    static final int CMD_ELIF = 12;
    
    boolean included;
    boolean systemFile;
    String[] sysDirs;
    File file;
    FileObject fobj;
    String pwd;
    String filename;
    BufferedReader input;
    PrintStream output;
    Hashtable defines;
    int[] ifDepth;
    int state;
    String str;
    int line;
    int pos;
    int lineHeight;
    
    public Preprocessor(String _pwd, String _input, Hashtable _defines, String[] _sysDirs, PrintStream _output, boolean _systemFile) {
        this (_pwd, _input, _defines, _sysDirs, _output);
        included = true;
        systemFile = _systemFile;
    }
    
    public Preprocessor(String _pwd, String _input, Hashtable _defines, String[] _sysDirs, PrintStream _output) {
        pwd = _pwd;
        file = null;
        filename = _input;
        input = null;
        output = _output;
        defines = (_defines != null) ? _defines : new Hashtable ();
        sysDirs = (_sysDirs != null) ? _sysDirs : new String[0];
        ifDepth = new int[0];
        line = 0;
        included = false;
        systemFile = false;
    }
    
    void error (String message) throws PreprocessorException {
        throw new PreprocessorException (line - lineHeight, "Error on line " + (line - lineHeight) + ": " + message);
    }
    
    static void staticError (String message) throws PreprocessorException {
        throw new PreprocessorException (0, "Error: " + message);
    }
    
    void addDepth () {
        int[] old = ifDepth;
        ifDepth = new int[old.length + 1];
        System.arraycopy (old, 0, ifDepth, 1, old.length);
    }
    
    void removeDepth () {
        int[] old = ifDepth;
        ifDepth = new int[old.length - 1];
        System.arraycopy (old, 1, ifDepth, 0, ifDepth.length);
    }
    
    boolean readLine () throws IOException {
        str = input.readLine ();
        lineHeight = 0;
        if (str == null)
            return false;
        line ++;
        lineHeight ++;
        while (str.endsWith("\\")) {
            str = str.substring(0, str.length () - 1);
            String s = input.readLine ();
            if (s == null)
                return true;
            str += s;
            line ++;
            lineHeight ++;
        }
        return true;
    }
    
    boolean readLineNext () throws IOException {
        str = str.substring(0, str.length () - 1) + "/*";
        String s = input.readLine ();
        if (s == null)
            return false;
        str += s;
        line ++;
        lineHeight ++;
        while (str.endsWith("\\")) {
            str = str.substring(0, str.length () - 1);
            s = input.readLine ();
            if (s == null)
                return true;
            str += s;
            line ++;
            lineHeight ++;
        }
        return true;
    }
    
    void writeOutput (String out) {
        if (out != null)
            output.println (out);
        else
            output.println ();
        while (lineHeight > 1) {
            lineHeight --;
            output.println ();
        }
    }
    
    int getPosChar () {
        return (pos < str.length ()) ? str.charAt (pos) : -1;
    }
    
    int getPosChar (int x) {
        x += pos;
        return (x < str.length ()) ? str.charAt (x) : -1;
    }
    
    boolean isDigit (int c) {
        return c >= 0  &&  Character.isDigit((char) c);
    }
    
    int getHexNumber (int c) {
        if (c >= '0'  &&  c <= '9')
            return c - '0';
        if (c >= 'A'  &&  c <= 'F')
            return (c - 'A') + 10;
        if (c >= 'a'  &&  c <= 'f')
            return (c - 'a') + 10;
        return -1;
    }
    
    int getOctalNumber (int c) {
        if (c >= '0'  &&  c <= '7')
            return c - '0';
        return -1;
    }
    
    boolean isJavaIdentifierStart (int c) {
        return c >= 0  &&  Character.isJavaIdentifierStart((char) c);
    }
    
    int eatWhiteSpaces () {
        int last = pos;
        while (pos < str.length ()  &&  Character.isWhitespace(str.charAt (pos)))
            pos ++;
        return pos - last;
    }
    
    String eatCharacter () throws PreprocessorException {
        if (getPosChar () != '\'')
            return null;
        pos ++;
        int last = pos;
        while (pos < str.length ()  &&  str.charAt (pos) != '\'') {
            if (getPosChar () == '\\') {
                switch (getPosChar (1)) {
                    case '\'':
                    case '"':
                    case '\\':
                        pos ++;
                }
            }
            pos ++;
        }
        if (pos >= str.length ())
            error ("Unterminated character: " + str);
        String s = str.substring(last, pos);
        pos ++;
        return s;
    }
    
    String eatString () throws PreprocessorException {
        if (getPosChar () != '"')
            return null;
        pos ++;
        int last = pos;
        while (pos < str.length ()  &&  str.charAt (pos) != '"') {
            if (getPosChar () == '\\') {
                switch (getPosChar (1)) {
                    case '\'':
                    case '"':
                    case '\\':
                        pos ++;
                }
            }
            pos ++;
        }
        if (pos >= str.length ())
            error ("Unterminated string: " + str);
        String s = str.substring(last, pos);
        pos ++;
        return s;
    }
    
    String eatIncludeString () {
        if (getPosChar () != '<')
            return null;
        pos ++;
        int last = pos;
        while (pos < str.length ()  &&  str.charAt (pos) != '>')
            pos ++;
        if (pos >= str.length ())
            return null;
        String s = str.substring(last, pos);
        pos ++;
        return s;
    }
    
    String eatIncludeString2 () {
        if (getPosChar () != '"')
            return null;
        pos ++;
        int last = pos;
        while (pos < str.length ()  &&  str.charAt (pos) != '"')
            pos ++;
        if (pos >= str.length ())
            return null;
        String s = str.substring(last, pos);
        pos ++;
        return s;
    }
    
    String eatIdentifier () {
        int last = pos;
        if (pos >= str.length ()  ||  !Character.isJavaIdentifierStart(str.charAt (pos)))
            return null;
        pos ++;
        while (pos < str.length ()  &&  Character.isJavaIdentifierPart(str.charAt (pos)))
            pos ++;
        return str.substring(last, pos);
    }
    
    String eatInteger () {
        int last = pos;
        while (pos < str.length ()  &&  Character.isDigit(str.charAt (pos)))
            pos ++;
        if (last >= pos)
            return null;
        return str.substring(last, pos);
    }
    
    int findCommand () throws IOException {
        pos = 0;
        eatWhiteSpaces ();
        if (getPosChar () != '#')
            return CMD_NONE;
        pos ++;
        eatWhiteSpaces ();
        if (str.startsWith(LBL_DEFINE, pos)) {
            pos += LBL_DEFINE.length ();
            if (eatWhiteSpaces () > 0)
                return (ifDepth.length <= 0  ||  (ifDepth[0] & MASK_SKIP) == 0) ? CMD_DEFINE : CMD_SKIP;
        } else if (str.startsWith (LBL_UNDEF, pos)) {
            pos += LBL_UNDEF.length ();
            if (eatWhiteSpaces () > 0)
                return (ifDepth.length <= 0  ||  (ifDepth[0] & MASK_SKIP) == 0) ? CMD_UNDEF : CMD_SKIP;
        } else if (str.startsWith (LBL_INCLUDE, pos)) {
            pos += LBL_INCLUDE.length ();
            if (eatWhiteSpaces () > 0)
                return (ifDepth.length <= 0  ||  (ifDepth[0] & MASK_SKIP) == 0) ? CMD_INCLUDE : CMD_SKIP;
        } else if (str.startsWith (LBL_LINE, pos)) {
            pos += LBL_LINE.length ();
            if (eatWhiteSpaces () > 0)
                return (ifDepth.length <= 0  ||  (ifDepth[0] & MASK_SKIP) == 0) ? CMD_LINE : CMD_SKIP;
        } else if (str.startsWith (LBL_IFDEF, pos)) {
            pos += LBL_IFDEF.length ();
            if (eatWhiteSpaces () > 0)
                return CMD_IFDEF;
        } else if (str.startsWith (LBL_IFNDEF, pos)) {
            pos += LBL_IFNDEF.length ();
            if (eatWhiteSpaces () > 0)
                return CMD_IFNDEF;
        } else if (str.startsWith (LBL_ELSE, pos)) {
            pos += LBL_ELSE.length ();
            if (pos >= str.length ()  ||  Character.isWhitespace (str.charAt (pos)))
                return CMD_ELSE;
        } else if (str.startsWith (LBL_ENDIF, pos)) {
            pos += LBL_ENDIF.length ();
            if (pos >= str.length ()  ||  Character.isWhitespace (str.charAt (pos)))
                return CMD_ENDIF;
        } else if (str.startsWith (LBL_IF, pos)) {
            pos += LBL_IF.length ();
            if (eatWhiteSpaces () > 0)
                return CMD_IF;
        } else if (str.startsWith (LBL_ELIF, pos)) {
            pos += LBL_ELIF.length ();
            if (eatWhiteSpaces () > 0)
                return CMD_ELIF;
        }
        return CMD_UNKNOWN;
    }
    
    void performInclude () throws PreprocessorException {
        boolean _systemFile;
        String flags;
        String fn;
        switch (getPosChar ()) {
            case '<':
                fn = eatIncludeString ();
                _systemFile = true;
                break;
            case '"':
                fn = eatIncludeString2 ();
                _systemFile = false;
                break;
            default:
                error ("Invalid include argument: " + str);
                return;
        }
        eatWhiteSpaces ();
        if (pos < str.length ())
            error ("Include command expects one argument only: " + str);
        else {
            if (file != null)
                new Preprocessor (file.getParent(), fn, defines, sysDirs, output, _systemFile).perform ();
            else if (fobj != null)
                new Preprocessor (FileUtils.getRealFileName(fobj.getParent()), fn, defines, sysDirs, output, _systemFile).perform ();
            output.println ("# " + line + " \"" + filename + "\" 2");
            lineHeight = 1;
        }
        writeOutput (null);
    }
    
    void performDefine () throws PreprocessorException {
        String def = eatIdentifier();
        if (def == null  ||  def.length () < 1)
            error ("Bad define name: " + str);
        else {
            if (getPosChar () == '(')
                error ("Defines with arguments are not supported" + str);
            if (defines.get (def) != null)
                error ("Redefining of define: " + def + ": " + str);
            eatWhiteSpaces();
            String s = str.substring (pos) + " ";
            defines.put (def, s);
        }
        writeOutput (null);
    }
    
    void performUndef () throws PreprocessorException {
        String def = eatIdentifier();
        eatWhiteSpaces();
        if (pos < str.length ())
            error ("Undef command expects one argument only: " + str);
        writeOutput (null);
    }
    
    void performLineDef () throws PreprocessorException {
        String s = eatInteger ();
        if (s == null)
            error ("Expected line number: " + str);
        else {
            try {
                eatWhiteSpaces();
                if (pos < str.length ())
                    error ("Line command expects one argument only: " + str);
                else {
                    line = Integer.parseInt(s) - 1;
                    output.println ("# " + line + " \"" + filename + "\"");
                    lineHeight = 1;
                }
            } catch (NumberFormatException e) {
                error ("Invalid line number format: " + str);
            }
        }
        writeOutput (null);
    }
    
    void performIfDef () throws PreprocessorException {
        String def = eatIdentifier();
        if (def == null  ||  def.length () < 1)
            error ("Bad define name: " + str);
        else {
            eatWhiteSpaces();
            if (pos < str.length ())
                error ("Ifdef command expects one argument only: " + str);
            if (defines.get (def) != null  &&  (ifDepth.length <= 0  ||  (ifDepth[0] & MASK_SKIP) == 0)) {
                addDepth ();
                ifDepth[0] = 0;
            } else {
                addDepth ();
                ifDepth[0] = MASK_AVAIL | MASK_SKIP;
            }
        }
        writeOutput (null);
    }
    
    void performIfNDef () throws PreprocessorException {
        String def = eatIdentifier();
        if (def == null  ||  def.length () < 1)
            error ("Bad define name: " + str);
        else {
            eatWhiteSpaces();
            if (pos < str.length ())
                error ("Ifndef command expects one argument only: " + str);
            if (defines.get (def) == null  &&  (ifDepth.length <= 0  ||  (ifDepth[0] & MASK_SKIP) == 0)) {
                addDepth ();
                ifDepth[0] = 0;
            } else {
                addDepth ();
                ifDepth[0] = MASK_AVAIL | MASK_SKIP;
            }
        }
        writeOutput (null);
    }
    
    void performElse () throws PreprocessorException {
        eatWhiteSpaces();
        if (pos < str.length ())
            error ("Else command expects no arguments: " + str);
        if (ifDepth.length <= 0)
            error ("Unexpected else command: " + str);
        else if ((ifDepth[0] & MASK_ELSE) != 0) {
            error ("Unexpected else command: " + str);
            ifDepth[0] = MASK_ELSE | MASK_SKIP;
        } else {
            if ((ifDepth[0] & MASK_AVAIL) != 0  &&  (ifDepth.length <= 1  ||  (ifDepth[1] & MASK_SKIP) == 0))
                ifDepth[0] = MASK_ELSE;
            else
                ifDepth[0] = MASK_ELSE | MASK_SKIP;
        }
        writeOutput (null);
    }
    
    void performEndIf () throws PreprocessorException {
        eatWhiteSpaces();
        if (pos < str.length ())
            error ("Endif command expects no arguments: " + str);
        if (ifDepth.length <= 0)
            error ("Unexpected endif command: " + str);
        else
            removeDepth();
        writeOutput (null);
    }
    
    void performIf () throws PreprocessorException {
        String def = str.substring (pos);
        if (def == null  ||  def.length () < 1)
            error ("If command expects agument: " + str);
        else {
            str = def;
            if (calcExpression ()  &&  (ifDepth.length <= 0  ||  (ifDepth[0] & MASK_SKIP) == 0)) {
                addDepth ();
                ifDepth[0] = 0;
            } else {
                addDepth ();
                ifDepth[0] = MASK_AVAIL | MASK_SKIP;
            }
        }
        writeOutput (null);
    }
    
    void performElif () throws PreprocessorException {
        String def = str.substring (pos);
        if (def == null  ||  def.length () < 1)
            error ("Elif command expects agument: " + str);
        else if (ifDepth.length <= 0)
            error ("Unexpected elif command: " + str);
        else if ((ifDepth[0] & MASK_ELSE) != 0) {
            error ("Unexpected elif command: " + str);
            ifDepth[0] = MASK_ELSE | MASK_SKIP;
        } else {
            str = def;
            if (calcExpression ()  &&  (ifDepth[0] & MASK_AVAIL) != 0  &&  (ifDepth.length <= 1  ||  (ifDepth[1] & MASK_SKIP) == 0)) {
                ifDepth[0] = 0;
            } else {
                if ((ifDepth[0] & MASK_AVAIL) != 0)
                    ifDepth[0] = MASK_SKIP | MASK_AVAIL;
                else
                    ifDepth[0] = MASK_SKIP;
            }
        }
        writeOutput (null);
    }
    
    void performUnknown () {
        pos = 0;
        eatWhiteSpaces ();
        if (getPosChar () != '#') {
            writeOutput(null);
            return;
        }
        pos ++;
        int last = pos;
        eatWhiteSpaces ();
        str = str.substring(0, last) + str.substring(pos);
        writeOutput (str);
    }
    
    boolean replaceDefines () throws PreprocessorException {
        boolean replaced = false;
        String out = "";
        pos = 0;
        while (pos < str.length ()) {
            if (getPosChar () == '"') {
                int last = pos;
                eatString ();
                out += str.substring (last, pos);
                continue;
            }
            if (getPosChar () == '\'') {
                int last = pos;
                eatCharacter ();
                out += str.substring (last, pos);
                continue;
            }
            String id = eatIdentifier();
            if (id != null  &&  id.length () >= 1) {
                String s = (String) defines.get (id);
                if (s != null) {
                    out += s;
                    replaced = true;
                } else {
                    if (id.startsWith("__")  &&  id.endsWith("__")) {
                        if ("__FILE__".equals (id)) {
                            out += "\"";
                            for (int a = 0; a < filename.length (); a ++)
                                if (filename.charAt (a) == '\\')
                                    out += "\\\\";
                                else
                                    out += filename.charAt (a);
                            out += "\"";
                            replaced = true;
                        } else if ("__LINE__".equals (id)) {
                            out += line;
                            replaced = true;
                        } else
                            out += id;
                    } else
                        out += id;
                }
                continue;
            }
            out += (char) getPosChar ();
            pos ++;
        }
        str = out;
        return replaced;
    }
    
    void performComment () throws PreprocessorException {
        String out = "";
        pos = 0;
        if (state != BLOCK_COMMENT)
            state = NORMAL;
        while (pos < str.length ()) {
            switch (state) {
                case START:
                case NORMAL:
                    if (getPosChar () == '/') {
                        if (getPosChar (1) == '/') {
                            pos = str.length ();
                            out += ' ';
                            break;
                        } else if (getPosChar (1) == '*') {
                            pos += 2;
                            state = BLOCK_COMMENT;
                            out += ' ';
                            break;
                        }
                    }
                    if (getPosChar () == '"') {
                        int last = pos;
                        eatString ();
                        out += str.substring (last, pos);
                        break;
                    }
                    if (getPosChar () == '\'') {
                        int last = pos;
                        eatCharacter ();
                        out += str.substring (last, pos);
                        break;
                    }
                    out += (char) getPosChar();
                    pos ++;
                    break;
                case BLOCK_COMMENT:
                    if (getPosChar () == '*'  && getPosChar (1) == '/') {
                        pos += 2;
                        state = NORMAL;
                        break;
                    }
                    pos ++;
                    break;
                default:
                    error ("Internal error: bad state: " + str);
            }
        }
        str = out;
    }
    
    void performLine () throws PreprocessorException {
        if (ifDepth.length > 0  &&  (ifDepth[0] & MASK_SKIP) != 0) {
            writeOutput (null);
            return;
        }
        while (replaceDefines ()) ;
        writeOutput (str);
    }
    
    static final int TOKEN_UNKNOWN = -1;
    static final int TOKEN_ERROR = 0;
    static final int TOKEN_NONE = 1;
    static final int TOKEN_VALUE = 2;
    static final int TOKEN_OR = 3;
    static final int TOKEN_AND = 4;
    static final int TOKEN_NOT = 5;
    static final int TOKEN_LBRACKET = 6;
    static final int TOKEN_RBRACKET = 7;
    static final int TOKEN_EQUAL = 8;
    static final int TOKEN_NOT_EQUAL = 9;
    static final int TOKEN_GREATER = 10;
    static final int TOKEN_GREATER_EQUAL = 11;
    static final int TOKEN_LESS = 12;
    static final int TOKEN_LESS_EQUAL = 13;
    static final int TOKEN_PLUS = 14;
    static final int TOKEN_MINUS = 15;
    static final int TOKEN_MULTIPLE = 16;
    static final int TOKEN_DIVIDE = 17;
    static final int TOKEN_REMAINDER = 18;
    static final int TOKEN_LSHIFT = 19;
    static final int TOKEN_RSHIFT = 20;
    static final int TOKEN_BOR = 21;
    static final int TOKEN_BAND = 22;
    static final int TOKEN_BXOR = 23;
    
    int token;
    long tokenValue;
    int tokenStart;
    
    int getToken () {
        if (str == null)
            return TOKEN_ERROR;
        if (token >= 0)
            return token;
        eatWhiteSpaces ();
        tokenStart = pos;
        switch (getPosChar ()) {
            case '(':
                pos ++;
                return token = TOKEN_LBRACKET;
            case ')':
                pos ++;
                return token = TOKEN_RBRACKET;
            case '=':
                if (getPosChar (1) == '=') {
                    pos += 2;
                    return token = TOKEN_EQUAL;
                }
                break;
            case '>':
                pos ++;
                if (getPosChar () == '=') {
                    pos ++;
                    return token = TOKEN_GREATER_EQUAL;
                }
                if (getPosChar () == '>') {
                    pos ++;
                    return token = TOKEN_RSHIFT;
                }
                return token = TOKEN_GREATER;
            case '<':
                pos ++;
                if (getPosChar () == '=') {
                    pos ++;
                    return token = TOKEN_LESS_EQUAL;
                }
                if (getPosChar () == '<') {
                    pos ++;
                    return token = TOKEN_LSHIFT;
                }
                return token = TOKEN_LESS;
            case '+':
                pos ++;
                return token = TOKEN_PLUS;
            case '-':
                pos ++;
                return token = TOKEN_MINUS;
            case '*':
                pos ++;
                return token = TOKEN_MULTIPLE;
            case '/':
                pos ++;
                return token = TOKEN_DIVIDE;
            case '%':
                pos ++;
                return token = TOKEN_REMAINDER;
            case '!':
                pos ++;
                if (getPosChar () == '=') {
                    pos ++;
                    return token = TOKEN_NOT_EQUAL;
                }
                return token = TOKEN_NOT;
            case '|':
                pos ++;
                if (getPosChar () == '|') {
                    pos ++;
                    return token = TOKEN_OR;
                }
                return token = TOKEN_BOR;
            case '&':
                pos ++;
                if (getPosChar () == '&') {
                    pos ++;
                    return token = TOKEN_AND;
                }
                return token = TOKEN_BAND;
            case '^':
                pos ++;
                return token = TOKEN_BXOR;
            case '\'':
                pos ++;
                if (getPosChar () == '\\') {
                    pos ++;
                    switch (getPosChar ()) {
                        case 'a':
                            tokenValue = 7;
                            break;
                        case 'b':
                            tokenValue = '\b';
                            break;
                        case 'f':
                            tokenValue = '\f';
                            break;
                        case 'n':
                            tokenValue = '\n';
                            break;
                        case 'r':
                            tokenValue = '\r';
                            break;
                        case 't':
                            tokenValue = '\t';
                            break;
                        case 'v':
                            tokenValue = 11;
                            break;
                        case '\'':
                            tokenValue = '\'';
                            break;
                        case '"':
                            tokenValue = '"';
                            break;
                        case '?':
                            tokenValue = '?';
                            break;
                        case '\\':
                            tokenValue = '\\';
                            break;
                        case 'x':
                            tokenValue = 0;
                            if (getHexNumber (getPosChar (1)) < 0) {
                                str = null;
                                return token = TOKEN_ERROR;
                            }
                            for (;;) {
                                int v = getHexNumber (getPosChar (1));
                                if (v < 0)
                                    break;
                                tokenValue = tokenValue * 16 + v;
                                pos ++;
                            }
                            break;
                        default:
                            tokenValue = 0;
                            if (getOctalNumber (getPosChar (1)) < 0) {
                                str = null;
                                return token = TOKEN_ERROR;
                            }
                            for (;;) {
                                int v = getOctalNumber (getPosChar (1));
                                if (v < 0)
                                    break;
                                tokenValue = tokenValue * 8 + v;
                                pos ++;
                            }
                    }
                } else if (getPosChar () == '\'')
                    break;
                else
                    tokenValue = getPosChar ();
                pos ++;
                if (getPosChar () == '\'') {
                    pos ++;
                    return token = TOKEN_VALUE;
                }
                break;
            case -1:
                return TOKEN_NONE;
            default:
                if (isDigit (getPosChar ())) {
                    tokenValue = 0;
                    do {
                        tokenValue = tokenValue * 10 + (getPosChar () - '0');
                        pos ++;
                    } while (isDigit(getPosChar ()));
                    return token = TOKEN_VALUE;
                }
                if (isJavaIdentifierStart(getPosChar ())) {
                    eatIdentifier ();
                    String def = (String) defines.get (str.substring (tokenStart, pos));
                    if (def == null  ||  def.length () < 1)
                        def = "0 ";
                    str = str.substring (0, tokenStart) + def + str.substring (pos);
                    pos = tokenStart;
                    return getToken ();
                }
        }
        str = null;
        return token = TOKEN_ERROR;
    }
    
    boolean cmpToken () {
        if (getToken () > TOKEN_ERROR) {
            token = TOKEN_UNKNOWN;
            return true;
        }
        str = null;
        return false;
    }
    
    boolean cmpToken (int t) {
        if (getToken () == t) {
            token = TOKEN_UNKNOWN;
            return true;
        }
        str = null;
        return false;
    }
    
    public long calcOR () {
        long val = (calcAND () != 0) ? 1 : 0;
        while (getToken () == TOKEN_OR) {
            if (!cmpToken ())
                return 0;
            val |= (calcAND () != 0) ? 1 : 0;
        }
        return val;
    }
    
    public long calcAND () {
        long val = (calcBOR () != 0) ? 1 : 0;
        while (getToken () == TOKEN_AND) {
            if (!cmpToken ())
                return 0;
            val |= (calcBOR () != 0) ? 1 : 0;
        }
        return val;
    }
    
    public long calcBOR () {
        long val = calcBXOR ();
        while (getToken () == TOKEN_BOR) {
            if (!cmpToken ())
                return 0;
            val |= calcBXOR ();
        }
        return val;
    }
    
    public long calcBXOR () {
        long val = calcBAND ();
        while (getToken () == TOKEN_BXOR) {
            if (!cmpToken ())
                return 0;
            val ^= calcBAND ();
        }
        return val;
    }
    
    public long calcBAND () {
        long val = calcEquality ();
        while (getToken () == TOKEN_BAND) {
            if (!cmpToken ())
                return 0;
            val &= calcEquality ();
        }
        return val;
    }
    
    public long calcEquality () {
        long val = calcRelational ();
        long n;
        boolean t = true;
        for (;;) switch (getToken ()) {
            case TOKEN_EQUAL:
                if (!cmpToken ())
                    return 0;
                n = calcRelational ();
                t = t && (val == n);
                val = n;
                break;
            case TOKEN_NOT_EQUAL:
                if (!cmpToken ())
                    return 0;
                n = calcRelational ();
                t = t && (val != n);
                val = n;
                break;
            default:
                return (t) ? val : 0;
        }
    }
    
    public long calcRelational () {
        long val = calcShift ();
        long n;
        boolean t = true;
        for (;;) switch (getToken ()) {
            case TOKEN_GREATER:
                if (!cmpToken ())
                    return 0;
                n = calcShift ();
                t = t && (val > n);
                val = n;
                break;
            case TOKEN_GREATER_EQUAL:
                if (!cmpToken ())
                    return 0;
                n = calcShift ();
                t = t && (val >= n);
                val = n;
                break;
            case TOKEN_LESS:
                if (!cmpToken ())
                    return 0;
                n = calcShift ();
                t = t && (val < n);
                val = n;
                break;
            case TOKEN_LESS_EQUAL:
                if (!cmpToken ())
                    return 0;
                n = calcShift ();
                t = t && (val <= n);
                val = n;
                break;
            default:
                return (t) ? val : 0;
        }
    }
    
    public long calcShift () {
        long val = calcAdditive ();
        for (;;) switch (getToken ()) {
            case TOKEN_LSHIFT:
                if (!cmpToken ())
                    return 0;
                val <<= calcAdditive ();
                break;
            case TOKEN_RSHIFT:
                if (!cmpToken ())
                    return 0;
                val >>= calcRelational ();
                break;
            default:
                return val;
        }
    }
    
    public long calcAdditive () {
        long val = calcMultiplicative ();
        for (;;) switch (getToken ()) {
            case TOKEN_PLUS:
                if (!cmpToken ())
                    return 0;
                val += calcMultiplicative ();
                break;
            case TOKEN_MINUS:
                if (!cmpToken ())
                    return 0;
                val -= calcMultiplicative ();
                break;
            default:
                return val;
        }
    }
    
    public long calcMultiplicative () {
        long val = calcValue ();
        long d;
        for (;;) switch (getToken ()) {
            case TOKEN_MULTIPLE:
                if (!cmpToken ())
                    return 0;
                val *= calcValue ();
                break;
            case TOKEN_DIVIDE:
                if (!cmpToken ())
                    return 0;
                d = calcValue ();
                if (d == 0) {
                    str = null;
                    return 0;
                }
                val /= d;
                break;
            case TOKEN_REMAINDER:
                if (!cmpToken ())
                    return 0;
                d = calcValue ();
                if (d == 0) {
                    str = null;
                    return 0;
                }
                val %= d;
                break;
            default:
                return val;
        }
    }
    
    public long calcValue () {
        switch (getToken ()) {
            case TOKEN_PLUS:
                if (!cmpToken ())
                    return 0;
                return calcValue ();
            case TOKEN_MINUS:
                if (!cmpToken ())
                    return 0;
                return -calcValue ();
            case TOKEN_NOT:
                if (!cmpToken ())
                    return 0;
                return (calcValue () != 0) ? 0 : 1;
            case TOKEN_LBRACKET:
                if (!cmpToken ())
                    return 0;
                long val = calcOR ();
                if (!cmpToken (TOKEN_RBRACKET))
                    return 0;
                return val;
            default:
                if (!cmpToken (TOKEN_VALUE))
                    return 0;
                return tokenValue;
        }
    }
    
    public boolean calcExpression () throws PreprocessorException {
        pos = 0;
        token = TOKEN_UNKNOWN;
        long val = calcOR ();
        if (getToken () != TOKEN_NONE  ||  str == null) {
            error ("Parse error");
            return false;
        }
        return val != 0;
    }
    
    public void perform () throws PreprocessorException {
        input = getInput();
        state = START;
        String s = "# 1 \"" + filename + '"';
        if (included) {
            if (systemFile)
                s += " 1 3";
            else
                s += " 1";
        }
        output.println (s);
        try {
            while (readLine ()) {
                int state2 = state;
                performComment ();
                pos = 0;
                eatWhiteSpaces ();
                if (pos < str.length ()  &&  str.charAt (pos) == '#')
                    while (state == BLOCK_COMMENT  &&  readLineNext ()) {
                        state = state2;
                        performComment ();
                    }
                switch (findCommand ()) {
                    case CMD_NONE:
                        performLine ();
                        break;
                    case CMD_DEFINE:
                        performDefine ();
                        break;
                    case CMD_UNDEF:
                        performUndef ();
                        break;
                    case CMD_INCLUDE:
                        performInclude();
                        break;
                    case CMD_LINE:
                        performLineDef();
                        break;
                    case CMD_IFDEF:
                        performIfDef();
                        break;
                    case CMD_IFNDEF:
                        performIfNDef();
                        break;
                    case CMD_ELSE:
                        performElse();
                        break;
                    case CMD_ENDIF:
                        performEndIf();
                        break;
                    case CMD_SKIP:
                        writeOutput(null);
                        break;
                    case CMD_IF:
                        performIf();
                        break;
                    case CMD_ELIF:
                        performElif();
                        break;
                    case CMD_UNKNOWN:
                        performUnknown ();
                        break;
                    default:
                        error ("Internal error: bad command: " + str);
                        writeOutput(null);
                }
            }
        } catch (IOException e) {
            error ("Error while reading file: " + filename);
        } finally {
            if (input != null)
                try {
                    input.close ();
                } catch (IOException e) {
                }
        }
    }
    
    public static void main (String[] args, PrintStream out) throws PreprocessorException {
        Hashtable def = new Hashtable ();
        ArrayList undef = new ArrayList ();
        ArrayList dirs = new ArrayList ();
        ArrayList files = new ArrayList ();
        String pwdDir = null;
        String[] sdirs;
        for (int a = 0; a < args.length; a ++) {
            if (args[a].startsWith ("-I")) {
                dirs.add (args[a].substring(2));
            } else if (args[a].startsWith ("-D")) {
                int start = args[a].indexOf('=');
                if (start >= 3)
                    def.put (args[a].substring (2, start), args[a].substring (start + 1) + " ");
                else if (args[a].length () >= 3)
                    def.put (args[a].substring (2), " ");
                else
                    staticError ("Bad command argument for preprocessor: " + args[a]);
            } else if (args[a].startsWith ("-U")) {
                undef.add (args[a].substring(2));
            } else if (args[a].startsWith ("-W")) {
                if (args[a].length () > 2)
                    pwdDir = args[a].substring (2);
            } else {
                files.add (args[a]);
            }
        }
        for (int a = 0; a < undef.size (); a ++)
            def.remove (undef.get (a));
        sdirs = new String[dirs.size ()];
        for (int a = 0; a < dirs.size (); a ++)
            sdirs[a] = (String) dirs.get (a);
        for (int a = 0; a < files.size (); a ++)
            new Preprocessor (pwdDir, (String) files.get (a), def, sdirs, out).perform ();
    }
    
    public static void main (String[] args) throws PreprocessorException {
        main (args, System.out);
    }
    
    private BufferedReader getInput() throws PreprocessorException {
        if (systemFile) {
            for (int a = 0; a < sysDirs.length; a ++) {
                file = new File (sysDirs[a], filename);
                if (file != null  &&  file.exists ()  &&  file.isFile()  &&  file.canRead ())
                    break;
                file = null;
            }
        } else {
            file = new File (pwd, filename);
            if (file != null  &&  (!file.exists ()  ||  !file.isFile ()  ||  !file.canRead ()))
                file = null;
        }
        if (file != null) {
            try {
                return new BufferedReader (new FileReader (file));
            } catch (FileNotFoundException e) {
                error ("Error opening file: " + filename);
            }
        }
        else {
            FileSystem fs = null;
            String system_name = null;
            for (Enumeration ffs = TopManager.getDefault().getRepository().fileSystems(); ffs.hasMoreElements();) {
                fs = (FileSystem)ffs.nextElement();
                File f = NbClassPath.toFile(fs.getRoot());
                if (f != null)
                    system_name = f.getAbsolutePath();
                else
                    system_name = fs.getSystemName();
                if (filename.startsWith(system_name)) {
                    String _filename = filename.substring(system_name.length());
                    if (_filename.length() > 1) {
                        _filename = FileUtils.convert2Canonical(_filename);
                        _filename = _filename.replace(File.separatorChar, '/');
                        fobj = fs.findResource(_filename);
                        if (fobj != null)
                            break;
                    }
                }
            }
            if (fobj == null && pwd != null) {
                for (Enumeration ffs = TopManager.getDefault().getRepository().fileSystems(); ffs.hasMoreElements();) {
                    fs = (FileSystem)ffs.nextElement();
                    File f = NbClassPath.toFile(fs.getRoot());
                    if (f != null)
                        system_name = f.getAbsolutePath();
                    else
                        system_name = fs.getSystemName();
                    if (pwd.startsWith(system_name)) {
                        String _filename = pwd.substring(system_name.length());
                        if (_filename.length() > 1)
                            _filename += File.separatorChar + filename;
                        else
                            _filename = filename;
                        _filename = FileUtils.convert2Canonical(_filename);
                        _filename = _filename.replace(File.separatorChar, '/');
                        fobj = fs.findResource(_filename);
                        if (fobj != null)
                            break;
                    }
                }
            }
            if (fobj == null || fobj.isFolder() || !fobj.isValid())
                error ("File not found or cannot be read: " + filename);
            try {
                return new BufferedReader (new InputStreamReader(fobj.getInputStream()));
            } catch (FileNotFoundException e) {
                error ("Error opening file: " + filename);
            }
        }
        return null;
    }
}
