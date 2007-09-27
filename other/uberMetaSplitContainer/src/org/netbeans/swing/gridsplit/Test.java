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

package org.netbeans.swing.gridsplit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;


public class Test {
    
    GridSplitModel model;
    
    private static int index = 0;
    
    /** Creates a new instance of Test */
    public Test() {
        model = buildModel();
    }
    
    public String toString() {
        return "\33\n\"'\\\ue161";      // FULL OF BUGS IN TOOLTIP
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Test t = new Test();
        TestFrame jf = new TestFrame();
        jf.getContentPane().setLayout (new BorderLayout());
        jf.addWindowListener (new WA());

//        jf.setSplitter( new MultiSplitDisplayer( t.model ) );
        jf.setSplitter( new GridSplitPane( t.model ) );
        jf.setBounds (20, 20, 400, 400);
        
        jf.show();
    }
    
//    Component buildSplitter() {
//        return buildModel().getRootCell().getComponent();
//    }
//    
    GridSplitModel buildModel() {
        Component c1 = buildComponent();
        Component c2 = buildComponent();
        Component c3 = buildComponent();
//        Component c4 = buildComponent();
//        Component c5 = buildComponent();
//        Component c6 = buildComponent();

        GridSplitModel model = new GridSplitModel( c1, new Dimension( 450,300 ), 1.0 );
        model.addToRoot(c2, GridSplitCell.EAST, 0.3, 0.0 );
        model.addToSide(c1, c3, GridSplitCell.SOUTH, 0.5, 0.0 );
        
//        model.add( c1, c2, MultiSplitCell.SOUTH, 0.2 );
//        model.add( c1, c3, MultiSplitCell.WEST, 0.2 );
//        model.add( c1, c4, MultiSplitCell.EAST, 0.2 );
//        model.add( c4, c5, MultiSplitCell.SOUTH, 0.5 );
//        model.add( c1, c6, MultiSplitCell.WEST, 0.2 );

        return model;
    }
    
    Component buildComponent() {
//        JComponent c = new JScrollPane( new JTree() );
//        c.setPreferredSize( new Dimension( 50*(index+1),50 ) );
//        return c;
        return new TestComponent();
    }
    
    private static class WA implements WindowListener {
        
        public void windowActivated(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            System.exit(0);
        }
        
        public void windowDeactivated(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowDeiconified(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowIconified(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowOpened(java.awt.event.WindowEvent windowEvent) {
        }
        
    }
 
    private class TestComponent extends JPanel {
        
        private int index;
        
        public TestComponent() {
            setLayout( new GridBagLayout() );
            
            final JButton btn = new JButton( "Click Me!" );
            btn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    JPopupMenu popup = buildPopupMenu();
                    popup.show( btn, 0, 0 );
                }
            });
            
            add( btn, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0 ,0 ) );
            add( new JScrollPane(new JTree()), new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0 ,0 ) );
            
            this.index = Test.index++;
        }
        
        public String toString() {
            return "Comp " + index;
        }
        
        JPopupMenu buildPopupMenu() {
            JPopupMenu res = new JPopupMenu();
            AbstractAction north = new AbstractAction( "Add North" ) {
                public void actionPerformed( ActionEvent e ) {
                    model.addToSide( TestComponent.this, buildComponent(), GridSplitCell.NORTH, 0.3 );
                }
            };
            AbstractAction east = new AbstractAction( "Add East" ) {
                public void actionPerformed( ActionEvent e ) {
                    model.addToSide( TestComponent.this, buildComponent(), GridSplitCell.EAST, 0.3 );
                }
            };
            AbstractAction south = new AbstractAction( "Add South" ) {
                public void actionPerformed( ActionEvent e ) {
                    model.addToSide( TestComponent.this, buildComponent(), GridSplitCell.SOUTH, 0.3 );
                }
            };
            AbstractAction west = new AbstractAction( "Add West" ) {
                public void actionPerformed( ActionEvent e ) {
                    model.addToSide( TestComponent.this, buildComponent(), GridSplitCell.WEST, 0.3 );
                }
            };
            AbstractAction remove = new AbstractAction( "Remove" ) {
                public void actionPerformed( ActionEvent e ) {
                    model.remove( TestComponent.this );
                }
            };
            res.add( north );
            res.add( east );
            res.add( south );
            res.add( west );
            res.addSeparator();
            res.add( remove );
            res.addSeparator();
            res.add( buildAddRootMenu() );
            res.addSeparator();
            res.add( buildShowHideMenu() );
            
            return res;
        }
        
        JMenu buildAddRootMenu() {
            JMenu res = new JMenu( "Add to root" );
            
            res.add( new AbstractAction( "North" ) {
                public void actionPerformed( ActionEvent e ) {
                    model.addToRoot( buildComponent(), GridSplitCell.NORTH, 0.3 );
                }
            });
            
            res.add( new AbstractAction( "East" ) {
                public void actionPerformed( ActionEvent e ) {
                    model.addToRoot( buildComponent(), GridSplitCell.EAST, 0.3 );
                }
            });
            
            res.add( new AbstractAction( "South" ) {
                public void actionPerformed( ActionEvent e ) {
                    model.addToRoot( buildComponent(), GridSplitCell.SOUTH, 0.3 );
                }
            });
            
            res.add( new AbstractAction( "West" ) {
                public void actionPerformed( ActionEvent e ) {
                    model.addToRoot( buildComponent(), GridSplitCell.WEST, 0.3 );
                }
            });
            
            return res;
        }
        
        JMenu buildShowHideMenu() {
            JMenu res = new JMenu( "Show / Hide" );
            
            Collection components = model.getComponents();
            for( Iterator i=components.iterator(); i.hasNext(); ) {
                final Component c = (Component)i.next();
                JCheckBoxMenuItem item = new JCheckBoxMenuItem( new AbstractAction( c.toString() ) {
                        public void actionPerformed( ActionEvent e ) {
                            model.setHidden( c, !model.isHidden( c ) );
                        }
                    });
                res.add( item );
                item.setSelected( !model.isHidden( c ) );
            }
            
            return res;
        }
    }
    
    private static class TestFrame extends JFrame {
        JComponent splitPane;
        
        public void setSplitter( JComponent splitter ) {
            this.splitPane = splitter;
            getContentPane().add( splitter, BorderLayout.CENTER );
            invalidate();
        }
        
        public java.awt.Dimension getMinimumSize() {

            if( null != splitPane )
                return splitPane.getMinimumSize();
            
            java.awt.Dimension retValue;
            
            retValue = super.getMinimumSize();
            return retValue;
        }
        
    }
}
