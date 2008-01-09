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
    private Object infoMapLock = new Object();
    
    RequestProcessor.Task task;

    private static final ErrorHighlighter instance = new ErrorHighlighter();
    
    public static final int delay = Integer.getInteger("cnd.synterr.delay", 2000); // NOI18N
    
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
        boolean isNew = false;
        FileHighliter info = null;
        synchronized( infoMapLock ) {
            info = infoMap.get(dao);
            if( info == null ) {
                info = new FileHighliter(dao, doc);
                infoMap.put(dao, info);
                isNew = true;
            }
        }
        if( isNew ) {
            info.init();
        }
        return info;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {

	if( DebugUtils.TRACE) System.err.printf("ErrorHighlighter.propertyChange %s\n", evt.getPropertyName());

	if (TopComponent.Registry.PROP_CURRENT_NODES.equals(evt.getPropertyName())) {
            checkCurrentNodes();
        }
	else if(TopComponent.Registry.PROP_TC_CLOSED.equals(evt.getPropertyName())) {
            Object newValue = evt.getNewValue();
            if( newValue instanceof TopComponent ) {
                TopComponent closedTC = (TopComponent) newValue;
                DataObject dao = closedTC.getLookup().lookup(DataObject.class);
                if( dao != null ) {
                    synchronized (infoMapLock) {
                        FileHighliter highlighter = infoMap.get(dao);
                        if (highlighter != null) {
                            highlighter.dispose();
                            infoMap.remove(dao);
                        }
                    }
                }
            }
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
