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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.syntaxerr.highlighter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.cnd.syntaxerr.DebugUtils;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 * 
 * @author Vladimir Kvashin
 */
public class ErrorHighlighter implements PropertyChangeListener {
    
    private Map<DataObject, FileHighliter> infoMap = new HashMap<DataObject, FileHighliter>();
    
    RequestProcessor.Task task;

    private static final ErrorHighlighter instance = new ErrorHighlighter();
    
    public static final int delay = Integer.getInteger("cnd.synterr.delay", 2000);
    
    public static final ErrorHighlighter instance() {
        return instance;
    }

    /** private constructor - prevents external creation */
    private ErrorHighlighter() {
    }
    
    public void startup() {
        if( DebugUtils.TRACE ) System.err.printf("ErrorHighlighter.startup\n");
        TopComponent.getRegistry().addPropertyChangeListener(this);
        checkCurrentNodes();
    }
    
    public void shutdown() {
        if( DebugUtils.TRACE ) System.err.printf("ErrorHighlighter.shutdown\n");
        TopComponent.getRegistry().removePropertyChangeListener(this);
    }

    private FileHighliter getOrCreateFileInfo(DataObject dao, BaseDocument doc) {
	FileHighliter info = infoMap.get(dao);
	if( info == null ) {
	    info = new FileHighliter(dao, doc);
	    infoMap.put(dao, info);
            info.init();
	}
	return info;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {

	if( DebugUtils.TRACE) System.err.printf("ErrorHighlighter.propertyChange %s\n", evt.getPropertyName());

	if (TopComponent.Registry.PROP_CURRENT_NODES.equals(evt.getPropertyName())) {
            checkCurrentNodes();
        }
	else if(TopComponent.Registry.PROP_OPENED.equals(evt.getPropertyName())) {
            // TODO: process closure; remove doc listener
	}
	    
    }
    
    private void checkCurrentNodes() {
        //if( DebugUtils.TRACE) System.err.printf("ErrorHighlighter.checkNodes\n");
        Node[] nodes = TopComponent.getRegistry().getCurrentNodes();
        if (nodes != null) {
            for (int i = 0; i < nodes.length; i++) {
                Node node = nodes[i];
                EditorCookie editor = node.getLookup().lookup(EditorCookie.class);
                //if( DebugUtils.TRACE) System.err.printf("\tErrorHighlighter.  Node %s Editor %s\n", node, editor);
                if (editor != null) {
                    DataObject dao = node.getLookup().lookup(DataObject.class);
                    //if( DebugUtils.TRACE) System.err.printf("\t\tErrorHighlighter.  dao %s\n", dao);
                    if (dao != null) {
                        onActivated(dao, editor);
                    }
                }
            }
        }
    }


    private void onActivated(final DataObject dao, final EditorCookie editor) {
        if( ! tryOnActivated(dao, editor) ) {
            final EditorCookie.Observable observable = dao.getCookie(EditorCookie.Observable.class);
            //if( DebugUtils.TRACE) System.err.printf("ErrorHighlighter.  onActivated observable = %s\n", observable);
            if( observable != null ) {
                observable.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent ev) {
                        //if( DebugUtils.TRACE) System.err.printf("ErrorHighlighter.$1.propertyChange %s\n", ev.getPropertyName());
                        if( EditorCookie.Observable.PROP_DOCUMENT.equals(ev.getPropertyName()) ) {
                            if( tryOnActivated(dao, editor) ) {
                                observable.removePropertyChangeListener(this);
                            }
                        }
                    }
                });
            }
        }
    }
    
    private boolean tryOnActivated(DataObject dao, EditorCookie editor) {
	Document doc = editor.getDocument();
        //if( DebugUtils.TRACE) System.err.printf("ErrorHighlighter.  tryGetDoc doc = %s\n", doc);
	if (doc instanceof BaseDocument) {
	    FileHighliter info = getOrCreateFileInfo(dao, (BaseDocument) doc);
            return true;
	}
        return false;
    }

    /* package-local - for FileHighliter only */
    void schedule(final FileHighliter info) {
        if( task != null ) {
            task.cancel();
        }
	Runnable r = new Runnable() {
	    public void run() {
                Thread.currentThread().setName("C/C++ Syntax Error Highlighting"); // NOI18N);
                info.updateAnnotations();
	    }
	};
	task = RequestProcessor.getDefault().create(r, true);
	task.schedule(delay);
    }
}
