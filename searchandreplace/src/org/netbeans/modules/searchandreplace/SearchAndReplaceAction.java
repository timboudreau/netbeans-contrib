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
package org.netbeans.modules.searchandreplace;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.searchandreplace.model.Item;
import org.netbeans.modules.searchandreplace.model.Search;
import org.netbeans.modules.searchandreplace.model.SearchDescriptor;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.LifecycleManager;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 * Action invoking a search and replace action.  Expects one or more
 * DataFolders to be available from the activated nodes.
 *
 * @author Tim Boudreau
 */
public final class SearchAndReplaceAction extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        Lookup[] lkps = new Lookup[activatedNodes.length];
        for (int i=0; i < activatedNodes.length; i++) {
            lkps[i] = activatedNodes[i].getLookup();
        }
        performAction (lkps);
    }

    void performAction (Lookup[] lookups) {
        Cancel c = new Cancel();
        ProgressHandle progress = ProgressHandleFactory.createHandle(NbBundle.getMessage (SearchAndReplaceAction.class, "LBL_SearchProgress"), c);
        progress.start();
        Set folders = new HashSet ();
        try {
            LifecycleManager.getDefault().saveAll();
            try {
                Lookup.Template tpl = new Lookup.Template(DataFolder.class);
                for (int i=0; i < lookups.length; i++) {
                    for (Iterator it = lookups[i].lookup(tpl).allInstances().iterator(); it.hasNext();) {
                        DataFolder fld = (DataFolder) it.next();
                        if (fld.isValid()) {
                            FileObject fob = fld.getPrimaryFile();
                            if (!fob.isVirtual()) {
                                try {
                                    File f = FileUtil.toFile (fob).getCanonicalFile();
                                    folders.add (f);
                                } catch (IOException ioe) {
                                    ErrorManager.getDefault().notify (ioe);
                                }
                            }
                        }
                        if (c.cancelled) {
                            return;
                        }
                    }
                }
            } finally {
                progress.finish();
            }

            progress.finish();

            OKEnabler enabler = new OKEnabler();

            SearchInput input = new SearchInput(containsFoldersUnderVCSControl(folders), enabler);
            DialogDescriptor dlg = new DialogDescriptor (input, NbBundle.getMessage(SearchAndReplaceAction.class, "LBL_SearchAndReplace"));
            enabler.dlg = dlg;
            enabler.input = input;
            enabler.stateChanged(null);

            if (!DialogDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dlg))) {
                return;
            }

            final SearchDescriptor descriptor = input.getSearchDescriptor();

            if ("".equals(descriptor.getSearchText())) { //NOI18N
                return;
            }

            progress = ProgressHandleFactory.createHandle(NbBundle.getMessage (SearchAndReplaceAction.class, "LBL_SearchProgress"), c);
            progress.start();

            final Search search = new Search (folders, descriptor, c);

            try {
                final Item[] items = search.search(progress);
                if (items.length == 0) {
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(Search.class, "LBL_NoMatches"));
                    return;
                } else {
                    //Get back on the event thread
                    EventQueue.invokeLater (new Runnable() {
                        public void run() {
                            SearchPreview preview = new SearchPreview (search,
                                    items);

                            preview.open();
                            preview.setDisplayName (NbBundle.getMessage (
                                    SearchAndReplaceAction.class,
                                    "TTL_Search",
                                    descriptor.getSearchText()));

                            preview.requestActive();
                        }
                    });
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify (ErrorManager.USER, ioe);
            }
        } finally {
            progress.finish();
        }
    }

    private void findFiles (File f, Set all) throws IOException {
        if (f.exists()) {
            if (f.isFile()) {
                all.add (f.getCanonicalFile());
            } else if (f.isDirectory()) {
                findFiles (f.getCanonicalFile(), all);
            }
        }
    }

    private static boolean containsFoldersUnderVCSControl(Collection /* <File> */ c) {
        boolean result = true;
        for (Iterator i = c.iterator(); i.hasNext();) {
            File f = (File) i.next();
            result &= !Search.isIgnoredFile(f);
            if (result) {
                break;
            }
        }
        return result;
    }

    protected int mode() {
        return CookieAction.MODE_ANY;
    }
    
    public String getName() {
        return NbBundle.getMessage(SearchAndReplaceAction.class,
                "CTL_SearchAndReplaceAction"); //NOI18N
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataFolder.class
        };
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        //Search should not run in the event thread
        return true;
    }

    private static class OKEnabler implements ChangeListener {
        DialogDescriptor dlg = null;
        SearchInput input = null;

        public void stateChanged(ChangeEvent e) {
            if (dlg != null && input != null) {
                dlg.setValid(input.hasSearchText());
            }
        }
    }

}

