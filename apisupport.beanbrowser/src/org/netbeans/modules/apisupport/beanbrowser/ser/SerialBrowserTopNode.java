/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.apisupport.beanbrowser.ser;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.netbeans.modules.apisupport.beanbrowser.ser.SerParser.Stream;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.windows.WindowManager;

@ServicesTabNodeRegistration(name="ser", displayName="Serialized Beans", iconResource="org/netbeans/modules/apisupport/beanbrowser/ser/ser.gif", position=2015)
public class SerialBrowserTopNode extends AbstractNode {

    private static final class Model extends Observable {
        final List<SerParser.Stream> streams = new ArrayList<SerParser.Stream>();
        void add(SerParser.Stream stream) {
            streams.add(stream);
            setChanged();
            notifyObservers();
        }
    }

    private final Model model;

    public SerialBrowserTopNode() {
        this(new Model());
    }
    private SerialBrowserTopNode(Model model) {
        super(Children.create(new Ch(model), false));
        this.model = model;
        setIconBase("org/netbeans/modules/apisupport/beanbrowser/ser/ser"); // NOI18N
        setName("ser");
        setDisplayName("Serialized Beans");
    }

    private void add(InputStream is, String label) throws IOException {
        try {
            model.add(new SerParser(is).parse(label));
        } finally {
            is.close();
        }
    }

    public @Override Action[] getActions(boolean context) {
        return new Action[] {
            new AbstractAction("Parse File") {
                public @Override void actionPerformed(ActionEvent e) {
                    JFileChooser c = new JFileChooser();
                    c.setFileFilter(new FileNameExtensionFilter("Serialized files", "ser"));
                    if (c.showOpenDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
                        File f = c.getSelectedFile();
                        try {
                            add(new FileInputStream(f), f.getName());
                        } catch (IOException x) {
                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(x.toString(), NotifyDescriptor.ERROR_MESSAGE));
                        }
                    }
                }
            },
            new AbstractAction("Parse Hex String") {
                public @Override void actionPerformed(ActionEvent e) {
                    NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine("Serial data as hex bytes (spaces OK):", "Enter Serialized Text");
                    if (DialogDisplayer.getDefault().notify(input) == NotifyDescriptor.OK_OPTION) {
                        try {
                            String text = input.getInputText().replaceAll("\\s+", "");
                            InputStream is = new ByteArrayInputStream(new BigInteger(text, 16).toByteArray());
                            is.read(); // discard initial zero sign byte
                            add(is, text.substring(0, Math.min(20, text.length())) + "…");
                        } catch (IOException x) {
                            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(x.toString(), NotifyDescriptor.ERROR_MESSAGE));
                        }
                    }
                }
            },
        };
    }

    private static final class Ch extends ChildFactory.Detachable<SerParser.Stream> implements Observer {

        private final Model model;

        @SuppressWarnings("LeakingThisInConstructor")
        Ch(Model model) {
            this.model = model;
        }

        protected @Override boolean createKeys(List<Stream> toPopulate) {
            toPopulate.addAll(model.streams);
            return true;
        }

        protected @Override Node createNodeForKey(Stream key) {
            return new SerStructureNode.StreamNode(key);
        }

        protected @Override void addNotify() {
            model.addObserver(this);
        }

        protected @Override void removeNotify() {
            model.deleteObserver(this);
        }

        public @Override void update(Observable o, Object arg) {
            refresh(false);
        }

    }

}
