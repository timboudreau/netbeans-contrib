/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.datatransfer.ExClipboard;

/** 
 * Utilities dealing with data transfer operations on todo items.
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
public final class TaskTransfer implements ExClipboard.Convertor {
    private static final Logger LOGGER = TLUtils.getLogger(TaskNode.class);
    
    static {
        LOGGER.setLevel(Level.OFF);
    }
    
    /** Flavor for tasks on the clipboard */    
    public static final DataFlavor TODO_FLAVOR = new DataFlavor(
        Task.class, NbBundle.getMessage(TaskTransfer.class, "LBL_todo_flavor")); // NOI18N
    
    /** Construct a task transfer object */    
    public TaskTransfer() {}
    
    /** Convert a transferable.
     * If it has just a text selection, make a corresponding task
     * by parsing it.
     * If just a task, make a corresponding text selection by writing it out.
     * If it has a multiple selection all of which are todo items, make a text
     * selection with all of them concatenated (and leave the todoitems there too
     * obviously).
     * Otherwise leave it alone.
     * @param t The transferable to convert
     * @return The converted transferable */
    public Transferable convert(final Transferable t) {
        boolean supportsString = t.isDataFlavorSupported(DataFlavor.stringFlavor);
        boolean supportsTodo = t.isDataFlavorSupported(TODO_FLAVOR);
        if (supportsString && !supportsTodo) {
	    // Return a new TodoItem from a string
            ExTransferable t2 = ExTransferable.create(t);
            t2.put(new ExTransferable.Single(TODO_FLAVOR) {
                protected Object getData() throws IOException, UnsupportedFlavorException {
                    String text = (String)t.getTransferData(DataFlavor.stringFlavor);
		    return Task.parse(new StringReader(text));
                }
            });
            return t2;
        } else if (!supportsString && supportsTodo) {
	    // Return a new string from a todo item
            ExTransferable t2 = ExTransferable.create(t);
            t2.put(new ExTransferable.Single(DataFlavor.stringFlavor) {
                protected Object getData() throws IOException, UnsupportedFlavorException {
                    Task item = (Task)t.getTransferData(TODO_FLAVOR);
                    StringWriter wr = new StringWriter();
		    Task.generate(item, wr);
                    return wr.toString();
                }
            });
            return t2;
        } else if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
            LOGGER.fine("multi selection");
	    // Multiselection
            try {
                final MultiTransferObject mto = (MultiTransferObject)
                    t.getTransferData(ExTransferable.multiFlavor);
                boolean allSupportTodo = mto.areDataFlavorsSupported(
                    new DataFlavor[] {TODO_FLAVOR});
                if (allSupportTodo) {
                    LOGGER.fine("multi selection all supports todo");
                    ExTransferable t2 = ExTransferable.create(t);
                    if (!supportsString) {
			// Create string representation
                        t2.put(new ExTransferable.Single(DataFlavor.stringFlavor) {
                            protected Object getData() throws IOException, UnsupportedFlavorException {
                                StringWriter wr = new StringWriter();
                                for (int i = 0; i < mto.getCount(); i++) {
                                    Task item =	
                                        (Task)mto.getTransferData(i, TODO_FLAVOR);
				    Task.generate(item, wr);
                                }
                                return wr.toString();
                            }
                        });
                    }
                    return t2;
                } // else: not all support todoitems - so don't do anything
            } catch (Exception e) {
                // Should not happen: IOException, UnsupportedFlavorException
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
        }
        return t;
    }

}
