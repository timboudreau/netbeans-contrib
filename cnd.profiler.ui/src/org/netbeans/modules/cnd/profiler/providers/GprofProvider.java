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
import java.util.Collection;
import org.netbeans.modules.cnd.profiler.data.Function;

/**
 *
 * @author eu155513
 */
public class GprofProvider implements FunctionsProvider {
    private final File file;
    
    private final Collection<Function> functions;

    public GprofProvider(File file) {
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
    private static final String PLAIN_PROPERTIES[] = new String[] {"percent","secs","self","calls","selfsc","totalsc","name"};
    
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
                    res.setProperty(PLAIN_PROPERTIES[propIdx++], line.substring(start, pos));
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
    
    private static final String CALL_PROPERTIES[] = new String[] {"index","persents","self","children","called","name"};
    
    private boolean parseCallBlock(Collection<Function> fss, BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line.startsWith("\f")) {
            return false;
        }
        boolean callees = false;
        Function described = null;
        while (!line.startsWith("-")) {
            // save callees
            if (callees) {
                assert described != null : "described function should be initialized already";
                Function callee = parseCallLine(line);
                callee = getLikeThis(fss, callee);
                if (callee != null) {
                    described.addCallee(callee);
                }
            } else if (line.startsWith("[")) {
                // described function
                described = parseCallLine(line);
                described = getLikeThis(fss, described);
                if (described != null) {
                    callees = true;
                }
            }
            line = reader.readLine();
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
    
    private Function parseCallLine(String line) {
        //skip number [xxx]
        int brackPos = line.lastIndexOf("[");
        if (brackPos == -1) {
            return null;
        }
        int end = brackPos-2;
        
        //skip <...>
        int lPos = line.lastIndexOf("<");
        if (lPos != -1 && lPos<end) {
            end = lPos-2;
        }
        
        char[] symbols = line.toCharArray();
        int pos = end;
        // find the name
        while (symbols[pos] != DELIMITER) {
            pos--;
        }
        
        return new Function(line.substring(pos+1, end+1));
    }
}
