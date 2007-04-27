/*
 * CommandLineQueryContext.java
 *
 * Created on March 14, 2007, 3:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.jackpot.engine;

import com.sun.source.util.TreePath;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.jackpot.QueryContext;
import org.openide.filesystems.FileObject;

/**
 * Query environment when run from the command-line or something like Ant.
 * 
 * @author Tom Ball
 */
public class CommandLineQueryContext implements QueryContext {
    private List<Result> results;
    
    /** Creates a new instance of CommandLineQueryContext */
    public CommandLineQueryContext() {
        results = new ArrayList<Result>();
    }

    /**
     * Returns results accrued from query execution.
     * 
     * @return the list of results
     */
    public final List<Result> getResults() {
        return results;
    }

    public void addResult(TreePath path, FileObject file, int start, int end, String label, String note) {
        Result result = new Result(path, file, start, end, label, note, "");
        results.add(result);
    }

    public void addChange(TreePath path, FileObject file, int start,
                          int end, String label, String note, String newSource) {
        Result result = new Result(path, file, start, end, label, note, newSource);
        results.add(result);
    }

    public void sendStatusMessage(String message) {
        System.out.println(message);
    }

    public void sendErrorMessage(String message, String title) {
        System.err.println(title + ": " + message);
    }

    public PrintWriter getLogWriter() {
        return new PrintWriter(System.out, true);
    }
}
