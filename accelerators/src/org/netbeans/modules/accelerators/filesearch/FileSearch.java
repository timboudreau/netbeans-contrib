/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;

import java.util.Arrays;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

/**
 * This class is not thread-safe. All methods should be called from the
 * same thread.
 *
 * @author Andrei Badea
 */
public class FileSearch {
    
    private static final ErrorManager LOGGER = ErrorManager.getDefault().getInstance("org.netbeans.modules.accelerators.filesearch"); // NOI18N
    private static final boolean LOG = LOGGER.isLoggable(ErrorManager.INFORMATIONAL);
    
    private final RequestProcessor rp = new RequestProcessor("FileSearch Request Processor", 1, true); // NOI18N
    
    private final FileSearchResult result;
    private final SearchWorker worker;

    // the current filter
    private SearchFilter currentFilter;
    
    // the filters which make up currentFilter
    private SearchFilter fileFilter = new FileSearchFilter();
    
    private String prefix;
    private boolean caseSensitive;
    
    private RequestProcessor.Task finishedNotifierTask;
    
    public FileSearch(Project project, FileSearchResult result) {
        assert project != null;
        assert result != null;
        
        synchronized (this) {
            this.result = result;
        }

        SourceGroup[] groups = ProjectUtils.getSources(project).getSourceGroups(Sources.TYPE_GENERIC);
        if (LOG) {
            LOGGER.log(ErrorManager.INFORMATIONAL, "Source groups: " + Arrays.asList(groups)); // NOI18N
        }
        worker = new SearchWorker(rp, groups, result);
    }
    
    public void search(String prefix, boolean caseSensitive) {
        assert prefix != null;
        
        if (finishedNotifierTask != null && !finishedNotifierTask.isFinished()) {
            finishedNotifierTask.cancel();
            finishedNotifierTask.waitFinished();
        }
        
        boolean start = false;
        boolean changeFilter = false;
        boolean restart = false;
        boolean stop = false;
        
        if (prefix.length() <= 0) {
            // empty prefix -- stop the search
            stop = true;
            // for the next invocation of search() pretend
            // there was no prefix
            prefix = null; 
        } else if (this.prefix == null) {
            // search() called for the first time
            // just start the search
            start = true;
        } else if (prefix.startsWith(this.prefix)) {
            // chars added to this.prefix
            if (this.caseSensitive == caseSensitive) {
                // and same case sensitivity
                // can optimize by letting the search continue with a new
                // filter and removing the invalid result items when the worker finishes
                changeFilter = true;
            } else {
                // different case sensitivity
                // have to restart
                // TODO could optimize for case sensitivity going from false to true
                restart = true;
            }
        } else {
            // prefix changed too much
            // have to restart
            restart = true;
        }
        
        assert start || changeFilter || restart || stop;
        
        if (start) {
            if (LOG) {
                LOGGER.log(ErrorManager.INFORMATIONAL, "Starting the search."); // NOI18N
            }
            setCurrentFilter(new PrefixSearchFilter(prefix, caseSensitive));
            worker.setFilter(currentFilter);
            worker.start();
        } else if (stop) {
            worker.cancel();
            result.clear();
        } else if (changeFilter) {
            if (LOG) {
                LOGGER.log(ErrorManager.INFORMATIONAL, "Changing the filter."); // NOI18N
            }
            setCurrentFilter(new PrefixSearchFilter(prefix, caseSensitive));
            worker.setFilter(currentFilter);
        } else {
            if (LOG) {
                LOGGER.log(ErrorManager.INFORMATIONAL, "Restarting the search."); // NOI18N
            }
            worker.cancel();
            result.clear();
            setCurrentFilter(new PrefixSearchFilter(prefix, caseSensitive));
            worker.setFilter(currentFilter);
            worker.start();
        }
        
        this.prefix = prefix;
        this.caseSensitive = caseSensitive;
        
        // since the notifier is posted to the same RP as the search worker uses
        // and that RP's throughput is 1, the notifier will be run when
        // the worker finishes
        // we also have to cleanup if we just changed the filter
        finishedNotifierTask = rp.post(new SearchFinishedNotifier(changeFilter));
    }
    
    public void cancel() {
        if (finishedNotifierTask != null) {
            finishedNotifierTask.cancel();
        }
        worker.cancel();
    }
    
    public FileSearchResult getResult() {
        return result;
    }
    
    private void setCurrentFilter(SearchFilter filter) {
        currentFilter = new DelegatingSearchFilter(fileFilter, filter);
    }
    
    /**
     * Notifies the search result that the search has finished and
     * optionally removes from the result the files which don't match
     * the filter.
     *
     * <p>It is assumed currentFilter only changes when this class's run() 
     * method is not running.</p>
     */
    private final class SearchFinishedNotifier implements Runnable {
        
        private boolean cleanup;
        
        public SearchFinishedNotifier(boolean cleanup) {
            this.cleanup = cleanup;
        }
        
        public void run() {
            if (cleanup) {
                FileObject[] fos = result.getResult();
                for (int i = 0; i < fos.length; i++) {
                    if (Thread.interrupted()) {
                        return;
                    }
                    if (!currentFilter.accept(fos[i])) {
                        result.remove(fos[i]);
                    }
                }
            }
            result.finish();
        }
    }
}
