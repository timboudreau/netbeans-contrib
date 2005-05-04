/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
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
