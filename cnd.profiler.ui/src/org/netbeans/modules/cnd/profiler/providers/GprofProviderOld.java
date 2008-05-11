/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.netbeans.modules.cnd.profiler.data.Call;
import org.netbeans.modules.cnd.profiler.data.Function;

/**
 *
 * @author eu155513
 */
public class GprofProviderOld implements FunctionsProvider {
    private final File file;
    
    private final Collection<Function> functions;

    public GprofProviderOld(File file) {
        this.file = file;
        functions = parseFunctions();
    }
    
    public Function[] getFunctions() {
        // todo: maybe we do not need to recreate it always
        return functions.toArray(new Function[functions.size()]);
    }
    
    private Collection<Function> parseFunctions() {
        Collection<Function> fss = new ArrayList<Function>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            parsePlainList(fss, reader);
            parseCallGraph(fss, reader);

            reader.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return fss;
    }
    
    private void parsePlainList(Collection<Function> fss, BufferedReader reader) throws IOException {
        String line = "";
        while (!line.contains("s/call  name")) {
            line = reader.readLine();
        }
        
        line = reader.readLine();
        // todo: check exit condition
        while (line != null && !line.startsWith("\f")) {
            fss.add(parsePlainLine(line));
            line = reader.readLine();
        }
    }
    
    private static final char DELIMITER = ' ';
    private static final String PLAIN_PROPERTIES[] = new String[] {"self_percent"/*0*/,"cumul_secs"/*1*/,"self"/*2*/,"calls"/*3*/,"selfsc"/*4*/,"totalsc"/*5*/,"name"};
    private static final Collection PLAIN_PROPERTIES_INCLUDED = Arrays.asList(new Integer[] {}); // will get all properties from call list
    
    private Function parsePlainLine(String line) {
        char[] symbols = line.toCharArray();
        int namePos = symbols.length-1;
        // find the name
        while (symbols[namePos] != DELIMITER) {
            namePos--;
        }
        Function res = new Function(line.substring(namePos+1));
        
        int pos = 0;
        int start = -1;
        int propIdx = 0;
        while (pos < namePos) {
            if (symbols[pos] == DELIMITER) {
                if (start != -1) {
                    if (PLAIN_PROPERTIES_INCLUDED.contains(propIdx)) {
                        res.setProperty(PLAIN_PROPERTIES[propIdx], line.substring(start, pos));
                    }
                    propIdx++;
                    start = -1;
                }
            } else {
                if (start == -1) {
                    start = pos;
                }
            }
            pos++;
        }
        return res;
    }
    
    private void parseCallGraph(Collection<Function> fss, BufferedReader reader) throws IOException {
        String line = "";
        while (!line.contains("called     name")) {
            line = reader.readLine();
        }
        
        // todo: check exit condition
        while (parseCallBlock(fss, reader)){}
    }
    
    private boolean parseCallBlock(Collection<Function> fss, BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line.startsWith("\f")) {
            return false;
        }
        boolean inCallees = false;
        Function described = null;
        Collection<Call> callers = new ArrayList<Call>();
        while (!line.startsWith("-")) {
            Function function = parseCallLine(line);
            if (function != null) {
                Function origFunction = getLikeThis(fss, function);
                if (origFunction == null) {
                    origFunction = function;
                }
                if (line.startsWith("[")) {
                    // described function
                    described = origFunction;
                    described.addProperties(function);
                    inCallees = true;
                } else {
                    Call call = new Call(origFunction);
                    call.addProperties(function);
                    if (inCallees) {
                        assert described != null;
                        described.addCallee(call);
                    } else {
                        callers.add(call);
                    }
                }
            }
            line = reader.readLine();
        }
        for (Call call : callers) {
            described.addCaller(call);
        }
        return true;
    }
    
    private static Function getLikeThis(Collection<Function> fc, Function f) {
        for (Function function : fc) {
            if (function.equals(f)) {
                return function;
            }
        }
        return null;
    }
    
    private static final String CALL_PROPERTIES[] = new String[] {"index"/*0*/,"total_percents"/*1*/,"self"/*2*/,"children"/*3*/,"called"/*4*/,"name"};
    private static final Collection CALL_PROPERTIES_INCLUDED = Arrays.asList(new Integer[] {2,3});
    
    private Function parseCallLine(String line) {
        //skip number [xxx] at the end
        int brackPos = line.lastIndexOf("[");
        if (brackPos == -1) {
            return null;
        }
        int end = brackPos-2;
        
        //skip <...> at the end
        int lPos = line.lastIndexOf("<");
        if (lPos != -1 && lPos<end) {
            end = lPos-2;
        }
        
        char[] symbols = line.toCharArray();
        int namePos = end;
        // find the name
        while (symbols[namePos] != DELIMITER) {
            namePos--;
        }
        
        Function res = new Function(line.substring(namePos+1, end+1));
        
        int pos = 0;
        int start = -1;
        int propIdx = 0;
        while (pos < namePos) {
            if (symbols[pos] == DELIMITER) {
                if (start != -1) {
                    String prop = line.substring(start, pos);
                    if (propIdx == 0 && !prop.startsWith("[")) {
                        propIdx = 2;
                    }
                    if (CALL_PROPERTIES_INCLUDED.contains(propIdx)) {
                        res.setProperty(CALL_PROPERTIES[propIdx], Double.parseDouble(prop));
                    }
                    propIdx++;
                    start = -1;
                }
            } else {
                if (start == -1) {
                    start = pos;
                }
            }
            pos++;
        }
        
        return res;
    }
}
