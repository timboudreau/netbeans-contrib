/*
 * StringParser.java
 *
 * Created on January 25, 2007, 7:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.javatextcopypaste;

import java.io.IOException;

/**
 *
 * @author Owner
 */
public class StringParser {
    
    /** Creates a new instance of StringParser */
    StringParser() {
        super();
    }
    
    public static String convertTextToJava(String textValue, boolean newlinesToSpaces) throws Exception{
        
        if(textValue==null){
            textValue="";
        }
        java.io.LineNumberReader lr = new java.io.LineNumberReader(new java.io.StringReader(textValue));
        StringBuffer sb = new StringBuffer(textValue.length() + 2000);
        String curline = null;
        
        while( (curline = lr.readLine()) != null ) {
            //now we need a loop to iterate over the line and write make out string
            //into java code and make it nice for code.
            boolean putAnotherNewLine = false;
            if(sb.length()>0){
                sb.append(System.getProperty("line.separator", "\n"));
                sb.append('+');
            }
            //quote line
            sb.append('\"');
            for(int i = 0; i < curline.length(); i++) {
                
                if( curline.charAt(i) == '"' ) {
                    sb.append('\\');
                    sb.append( curline.charAt(i) );
                } else if(curline.charAt(i) == '\\' ) {
                    sb.append('\\');
                    sb.append( curline.charAt(i) );
                } else if( curline.charAt(i) == '\n') {
                    sb.append('\\');
                    sb.append('n');
                } else if( curline.charAt(i) == '\r') {
                    sb.append('\\');
                    sb.append('r');
                } else if( curline.charAt(i) == '\t') {
                    sb.append('\\');
                    sb.append('t');
                } else {
                    sb.append( curline.charAt(i) );
                }
                
            }//end line for loop
            if(!newlinesToSpaces){
                sb.append('\\');
                sb.append('n');
            }else{
                sb.append(' ');
            }
            sb.append('"');
        }//end while lines
        sb.append(System.getProperty("line.separator", "\n"));
        sb.append("+\"\";");
        return sb.toString();
    }
    
    public static String convertJavaToText(String textValue, boolean newlinesToSpaces, boolean javaNewLinesToSpaces, boolean blankNewlines, boolean blankJavaNewlines) throws Exception{
        
        if(textValue==null){
            textValue="";
        }
        java.io.LineNumberReader lr = new java.io.LineNumberReader(new java.io.StringReader(textValue));
        StringBuffer sb = new StringBuffer(textValue.length() + 2000);
        StringBuffer outString = new StringBuffer();
        String curline = null;
        boolean inJString = false;
        boolean inSQuote = false;
        boolean inesc = false;
        while( (curline = lr.readLine()) != null ) {
            //now we need a loop to iterate over the line and write SQL
            
            for(int i = 0; i < curline.length(); i++) {
                if( curline.charAt(i) == '"') {
                    if (inJString&&!inesc){
                        inJString = false;
                    } else if(inJString&&inesc) {
                        if(curline.charAt(i)=='"'){
                            sb.append('"');
                        }
                    } else {
                        inJString = true;
                        String ostring = outString.toString().trim();
                        if(ostring.length()>0){
                            sb.append(" <JavaCode>");
                            sb.append(ostring);
                            sb.append("</JavaCode> ");
                            outString = new StringBuffer();
                        }
                    }
                } else if( curline.charAt(i) == '\'') {
                    if (inJString){
                        sb.append('\'');
                    } else if (inSQuote&&!inesc){
                        inSQuote = false;
                    } else if(inSQuote&&inesc) {
                        sb.append('\'');
                    } else {
                        inSQuote = true;
                        String ostring = outString.toString().trim();
                        if(ostring.length()>0){
                            sb.append(" <JavaCode>");
                            sb.append(ostring);
                            sb.append("</JavaCode> ");
                            outString = new StringBuffer();
                        }
                    }
                }else if(curline.charAt(i) == '\\' ) {
                    if(inJString||inSQuote){
                        if(i!=curline.length()-1){
                            //need to go ahead and attempt to parse out
                            //the escape sequence here instead of making it more complicated
                            //if the next character is u then we need to parse out a Unicode character
                            //and we should have 4 digits for the unicode character
                            //otherwise we must have a t,n,r,",', or a \, and everything else
                            //will be an error.
                            i++;
                            char c1 = curline.charAt(i);
                            switch(c1){
                                case 'u':
                                    //we now need to have 4 hex digits
                                    //otherwise we have an error.
                                    if( (i+4)<curline.length() ){
                                        char c2 = curline.charAt(++i);
                                        char c3 = curline.charAt(++i);
                                        char c4 = curline.charAt(++i);
                                        char c5 = curline.charAt(++i);
                                        String ucode = ""+c2+c3+c4+c5;
                                        int uchar = Integer.parseInt(ucode, 16);
                                        char uc = (char)uchar;
                                        sb.append(uc);
                                    }else{
                                        throw new Exception("The Java source is invalid.");
                                    }
                                    break;
                                case 't':
                                    sb.append('\t');
                                    break;
                                case 'n':
                                    if(!blankNewlines){
                                        if(!newlinesToSpaces){
                                            sb.append(System.getProperty("line.separator", "\n"));
                                        }else{
                                            sb.append(' ');
                                        }
                                    }
                                    break;
                                case 'r':
                                    if(!blankNewlines){
                                        if(!newlinesToSpaces){
                                            sb.append(System.getProperty("line.separator", "\n"));
                                        }else{
                                            sb.append(' ');
                                        }
                                    }
                                    break;
                                case '"':
                                    sb.append('\"');
                                    break;
                                case '\'':
                                    sb.append('\'');
                                    break;
                                case '\\':
                                    sb.append('\\');
                                    break;
                            }
                        }
                    }else{
                        throw new IOException("The input contains an out of order \\.  Can not parse invalid Java code.");
                    }
                    
                } else if( curline.charAt(i) == '\n') {
                    if(!blankJavaNewlines){
                        if(!javaNewLinesToSpaces){
                            sb.append(System.getProperty("line.separator", "\n"));
                        }else{
                            sb.append(' ');
                        }
                    }
                } else if( curline.charAt(i) == '\r') {
                    if(!blankJavaNewlines){
                        if(!javaNewLinesToSpaces){
                            sb.append(System.getProperty("line.separator", "\n"));
                        }else{
                            sb.append(' ');
                        }
                    }
                } else {
                    if(inJString||inSQuote){
                        sb.append( curline.charAt(i) );
                    }else{
                        if(curline.charAt(i)=='+'){
                            //we will just discard this...
                        }else{
                            outString.append(curline.charAt(i));
                        }
                    }
                }
                
            }//end line for loop
            String ostring = outString.toString().trim();
            if(ostring.length()>0){
                sb.append(" <JavaCode>");
                sb.append(ostring);
                sb.append("</JavaCode> ");
                outString = new StringBuffer();
            }
            
            if(!blankJavaNewlines){
                if(!javaNewLinesToSpaces){
                    sb.append(System.getProperty("line.separator", "\n"));
                }else{
                    sb.append(' ');
                }
            }
            
        }//end while lines
        return sb.toString();
    }
    
    public static String convertJavaToText(String textValue, boolean newlinesToSpaces, boolean javaNewLinesToSpaces) throws Exception{
        return convertJavaToText(textValue, newlinesToSpaces, javaNewLinesToSpaces, false, false);
    }
    
}
