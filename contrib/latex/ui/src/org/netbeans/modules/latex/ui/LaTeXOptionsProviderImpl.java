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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.ui;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.Line;

/**
 *
 * @author Jan Lahoda
 */
public final class LaTeXOptionsProviderImpl extends OptionProcessor {

    private Logger LOG = Logger.global;
    
    public LaTeXOptionsProviderImpl() {
    }

    @Override
    protected Set<Option> getOptions() {
        return Collections.<Option>emptySet();
//        Option<String> scroll = Option.requiredArgument(-1, "latex-scroll");
////        , new OneArgumentProcessor() {
////            public Object process(Option option, Env env, String arg) {
////                scroll(env.getErrorStream(), env.getCurrentDirectory(), arg);
////                return null;
////            }
////        });
//        
//        scroll = Option.shortDescription(scroll, "org.netbeans.modules.latex.ui.Bundle", "HLP_latex-scroll"); // NOI18N
//        
//        Option<String> folder = Option.requiredArgument(-1, "latex-folder");
//        
//        //TODO: document folder
//        
//        folder = Option.defaultOneOf(null, folder);
//        
//        //TODO: current folder:
//        return new Option[] {
//            Option.pair(folder, scroll, new PairProcessor<Void, String, String>() {
//                    public Void process(Option option, Env env, String first, String second) throws CommandException {
//                        File dir;
//                        
//                        if (first == null) {
//                            dir = env.getCurrentDirectory();
//                        } else {
//                            dir = new File(first);
//                            
//                            if (!dir.exists()) {
//                                env.getErrorStream().println("Folder " + first + " does not exist.");
//                                throw CommandException.exitCode(1);
//                            }
//                            if (!dir.isDirectory()) {
//                                env.getErrorStream().println("File " + first + " is not a directory.");
//                                throw CommandException.exitCode(1);
//                            }
//                        }
//                        
//                        scroll(env.getErrorStream(), dir, second);
//                        
//                        return null;
//                    }
//            })
//        };
    }
    
    protected void process(Env env, Map<Option,String[]> optionValues) throws CommandException {
        
    }
    
    private void scroll(PrintStream err, File folder, String argument) throws CommandException {
//        String[] splitted = argument.trim().split("\\?\\*\\?");
//        
//        if (splitted.length != 3 && splitted.length != 2) {
//            err.println("latex-scroll: Incorrect fields count: " + splitted.length);
//            throw CommandException.exitCode(1);
//        }
//        
//        int line = (-1);
//        int column = (-1);
//        
//        if (!"".equals(splitted[1])) {
//            try {
//                line = Integer.parseInt(splitted[1]);
//            } catch(NumberFormatException e) {
//                err.println("Not a correct number: " + line);
//            }
//        }
//        
//        if (splitted.length == 3 && !"".equals(splitted[2])) {
//            try {
//                column = Integer.parseInt(splitted[2]);
//            } catch(NumberFormatException e) {
//                err.println("Not a correct number: " + column);
//            }
//        }
//        
//        scroll(err, folder, splitted[0], line, column);
    }
    
    private void scroll(PrintStream err, File folder, String file, final int lineNumber, final int column) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "scroll:"); // NOI18N
            LOG.log(Level.FINE, "file = " + file ); // NOI18N
            LOG.log(Level.FINE, "line = " + lineNumber ); // NOI18N
            LOG.log(Level.FINE, "column = " + column ); // NOI18N
        }
        
        try {
            File toOpen = new File(folder, file);
            
            if (!toOpen.exists()) 
                toOpen = new File(file);
            
            if (!toOpen.exists()) {
                err.println("latex-scroll: The file " + file + " relative to " + folder + " does not exist!");
                return ;
            }
            
            FileObject fo = FileUtil.toFileObject(toOpen);
            DataObject od = DataObject.find(fo);
            LineCookie lc = (LineCookie) od.getCookie(LineCookie.class);
            final Line line = lc.getLineSet().getCurrent(lineNumber);
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    line.show(Line.SHOW_GOTO, column);
                }
            });
        } catch (IOException e) {
            Logger.getLogger("global").log(Level.WARNING, "Unexpected exception.", e); // NOI18N
        }
    }
}
