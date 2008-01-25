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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.javafx.model.impl;

import java.io.StringReader;
import javafx.ui.*;
import javafx.ui.canvas.*;
import net.java.javafx.typeImpl.Compilation;
import net.java.javafx.ui.UIContext;
import net.java.javafx.type.expr.ValidationError;
import org.netbeans.modules.javafx.model.FXDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import java.lang.Exception;
import java.lang.Throwable;
import java.lang.Runnable;
import java.lang.System;
import java.util.ArrayList;

class JavaFXWidget extends CompositeWidget {
    attribute document:JavaFXDocument;
    operation getCContent();
    operation compileContent();
    operation getSource();
}

operation JavaFXWidget.getSource(): String {
    return document.getSourceToRender();
}

operation JavaFXWidget.composeWidget() {
    return Canvas { content: bind compileContent() };
}
    
operation JavaFXWidget.compileContent(): Node* {
    var compilation;
    var unit;
    try {
        compilation = JavaFXModel.getPreviewCompilation(document);
        unit = JavaFXModel.getPreviewCompilationUnit(document);
        
        var error = compilation.getLastError();
        if (error <> null){
            var errlist = [];
            while (error <> null) {
                var msg: String = error.getErrorMessage();
                var ind = msg.indexOf("\n");
                if (ind <> -1){
                    msg = msg.substring(0, ind);
                }
                insert msg as last into errlist;
                error = error.getNextError();
            }
            return Group {
                content: [
                    Text {
                        x: 15
                        y: 15
                        content: "Preview can't be shown due to the following compilation errors:"
                        font: Font {face: VERDANA, style: [ITALIC, BOLD], size: 16}
                        fill: red 
                    },
                    Group{
                        content: [foreach(i in [1..sizeof errlist])
                            Text{
                                x: 20
                                y: 20 + i * 20
                                font: Font {face: VERDANA, style: [ITALIC], size: 14}
                                fill: red
                                content: "{i}.  {errlist[i-1]}"
                            }
                        ]
                    }
                ]
            };
        }
    } catch (err:Throwable) {
        err.printStackTrace();
    }
    var result;
    try {
        result = unit.execute();
    } catch (err:Throwable) {
        err.printStackTrace();
    }
    if (result instanceof Frame) {
        var f = (Frame)result;
        var w = f.width;
        var h = f.height;
        var widget = ((Frame)result).content;
        widget = RootPane {
            menubar: f.menubar
            content: BorderPanel {
               opaque: true
               center: widget
            }
        };
        f.content = null;
        f.visible = false;
        result = Group {
            content: View {
                content: widget
                size: {height: h, width: w}
            }
        };
 
    }
    else if (result instanceof Dialog) {
        var f = (Dialog)result;
        var w = f.width ;
        var h = f.height;
        var title = ((Dialog)result).title;
        var buttons = ((Dialog)result).buttons;
        var widget = ((Dialog)result).content;
        widget = RootPane {
            content: BorderPanel {
               border: EmptyBorder {top: 10 left: 10 right: 10 bottom: 10}
               opaque: true
               center: widget
               bottom: Box{
                           orientation: HORIZONTAL
                           content:[HorizontalGlue{},buttons]
                       }
            }
        };
        f.content = null;
        do later {
            f.visible = false;
        }
        result = Group {
            content: 
                View {
                    content: widget
                    size: {height: h, width: w}
               }
        };
    }
    return select if x instanceof Widget then View {content: (Widget)x} else if x instanceof Node then (Node)x else null from x in result;
}

do later {
    while(JavaFXModel.hasMoreDocuments()){
        var doc = (FXDocument)JavaFXModel.getNextDocument();
        doc.renderPreview(JavaFXWidget{
            document: doc
        }.getComponent());
    }
}