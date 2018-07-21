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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * A generic split pane that can display a hierarchy of nested split components separated
 * by draggable split bars.
 *
 * @author Stanislav Aubrecht
 */
public class GridSplitPane extends JPanel 
                implements GridSplitModelListener, MouseMotionListener, MouseListener {
    
    private GridSplitDivider draggingDivider;
    private GridSplitModel model;
    private ArrayList dividers = new ArrayList();
    private boolean dividersAreOutdated = true;
    /**
     * Location of the divider when the dragging session began.
     */
    protected int beginDragDividerLocation;
    private int lastDragLocation;
    
    /** Creates a new instance of NwaySplitPane */
    GridSplitPane( GridSplitModel model ) {
        setLayout( new GridSplitLayout() );
        this.model = model;
        model.addChangeListener( this );
        addMouseMotionListener( this );
        addMouseListener( this );
        
        addAll( model.getRootCell() );
    }
    
    private void addAll( GridSplitCell cell ) {
        if( cell.isHidden() )
            return;
        
        if( null != cell.getComponent() )
            add( cell.getComponent() );
        
        for( int i=0; i<cell.count(); i++ ) {
            addAll( cell.cellAt( i ) );
        }
    }
    
    private void removeAll( GridSplitCell cell ) {
        if( null != cell.getComponent() )
            remove( cell.getComponent() );
        
        for( int i=0; i<cell.count(); i++ ) {
            removeAll( cell.cellAt( i ) );
        }
    }
    
    public int getDividerSize() {
        return 5;
    }
    
    public GridSplitModel getModel() {
        return model;
    }

    public Dimension getMinimumSize() {
        return model.getRootCell().getMinimumSize( getDividerSize() );
    }

    public void cellAdded( GridSplitCell cell ) {
        addAll( cell );
        dividersAreOutdated = true;
        revalidate();
    }

    public void cellRemoved( GridSplitCell cell ) {
        removeAll( cell );
        dividersAreOutdated = true;
        revalidate();
    }
    
    public void cellModified( GridSplitCell oldCell, GridSplitCell newCell ) {
        if( newCell.isHidden() != oldCell.isHidden() ) {
            if( newCell.isHidden() ) {
                removeAll( newCell );
            } else {
                addAll( newCell );
            }
            dividersAreOutdated = true;
            revalidate();
        }
    }
    
    public void mouseMoved( MouseEvent e ) {
        switchCursor( e );
        e.consume();
    }

    public void mouseDragged( MouseEvent e ) {
        if( null == draggingDivider )
            return;
        
        draggingDivider.dragTo( e.getPoint() );
        e.consume();
    }

    public void mouseReleased(MouseEvent e) {
        if( null == draggingDivider )
            return;
        
        final Point p = new Point( e.getPoint() );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                draggingDivider.finishDraggingTo( p );
                draggingDivider = null;
            }
        });
        switchCursor( e );
        e.consume();
    }

    public void mousePressed(MouseEvent e) {
        GridSplitDivider divider = dividerAtPoint( e.getPoint() );
        if( null == divider )
            return;
        
        if( divider.isOnCollapseButton( e.getPoint() ) ) {
            e.consume();
            return;
        }
        
        draggingDivider = divider;
        divider.startDragging( e.getPoint() );
        e.consume();
    }

    public void mouseExited(MouseEvent e) {
        if( null == draggingDivider ) {
            setCursor( Cursor.getDefaultCursor() );
        }
        e.consume();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        GridSplitDivider divider = dividerAtPoint( e.getPoint() );
        if( null == divider )
            return;
        
        if( divider.collapse( e.getPoint() ) ) {
            dividersAreOutdated = true;
            e.consume();
            return;
        }
    }
    

    private void switchCursor( MouseEvent e ) {
        GridSplitDivider divider = dividerAtPoint( e.getPoint() );
        if( null == divider || divider.isOnCollapseButton( e.getPoint() ) ) {
            setCursor( Cursor.getDefaultCursor() );
        } else {
            if( divider.isHorizontal() ) {
                setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
            } else {
                setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
            }
        }
    }

    private GridSplitDivider dividerAtPoint( Point p ) {
        for( Iterator i=dividers.iterator(); i.hasNext(); ) {
            GridSplitDivider d = (GridSplitDivider)i.next();
            if( d.containsPoint( p ) )
                return d;
        }
        return null;
    }

    public void paint( Graphics g ) {
        super.paint(g);
        for( Iterator i=dividers.iterator(); i.hasNext(); ) {
            GridSplitDivider divider = (GridSplitDivider)i.next();
            divider.paint( g );
        }
    }
    
    protected class GridSplitLayout implements LayoutManager {
        
        public void layoutContainer( Container c ) {
            if( c != GridSplitPane.this )
                return;
            
            GridSplitCell root = model.getRootCell();
            
            root.resize( getWidth(), getHeight(), getDividerSize() );
            root.setLocation( 0, 0, getDividerSize() );

            if( dividersAreOutdated ) {
                dividers.clear();
                createDividers( root );
                dividersAreOutdated = false;
            }
            reshapeDividers();
        }
        
        private void createDividers( GridSplitCell cell ) {
            if( !cell.isSplit() )
                return;
            
            ArrayList visibleChildren = cell.getVisibleCells();
            for( int i=0; i<visibleChildren.size(); i++ ) {
                GridSplitCell first = (GridSplitCell)visibleChildren.get( i );
                if( i < visibleChildren.size()-1 ) {
                    GridSplitCell second = (GridSplitCell)visibleChildren.get( i+1 );

                    GridSplitDivider divider = new GridSplitDivider( GridSplitPane.this, getDividerSize(), first, second );
                    dividers.add( divider );
                }
                createDividers( first );
            }
        }

        private void reshapeDividers() {
            for( Iterator i=dividers.iterator(); i.hasNext(); ) {
                GridSplitDivider divider = (GridSplitDivider)i.next();
                divider.reshape();
            }
        }

        public Dimension minimumLayoutSize(Container container) {
            return container.getSize();
        }

        public Dimension preferredLayoutSize(Container container) {
            return container.getSize();
        }

        public void removeLayoutComponent(Component c) {}

        public void addLayoutComponent(String string, Component c) {}
    } // End of class BasicSplitPaneDivider.DividerLayout
}
