/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */

package org.netbeans.modules.javafx.editor;

import org.netbeans.modules.editor.indent.spi.ReformatTask;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.IndentTask;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Rastislav Komara (<a href="mailto:rastislav.komara@sun.com">RKo</a>)
 * @todo documentation
 */
public class IndentTaskFactory implements IndentTask.Factory{
    private static Logger log = Logger.getLogger(IndentTaskFactory.class.getName());
    /**
     * Create reformatting task.
     *
     * @param context non-null indentation context.
     * @return reformatting task or null if the factory cannot handle the given context.
     */
    public IndentTask createTask(Context context) {
        if (log.isLoggable(Level.INFO)) log.info("Creating reformat factory");
        return new JFXIndentTask(context);
    }
}