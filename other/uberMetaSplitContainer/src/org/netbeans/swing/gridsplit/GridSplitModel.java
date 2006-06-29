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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.swing.gridsplit;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Hierarchical model for split cells.
 *
 * @author  Stanislav Aubrecht
 */
public class GridSplitModel {

    private GridSplitCell root;
    private ArrayList listeners = new ArrayList();

    /** Creates a new instance of ComponentHierarchyModel */
    public GridSplitModel( Component rootComp, Dimension preferredSize, double resizeWeight ) {
        if( null != root ) {
            throw new IllegalArgumentException( "The hierarchy root is already set." );
        }
        
        root = new GridSplitCell( rootComp, resizeWeight, preferredSize );
    }
    
    /**
     * The topmost cell in the hierarchy.
     */
    public GridSplitCell getRootCell() {
        return root;
    }
    
    /**
     * Add a component to the topmost cell (i.e. attach a component to the given SplitPane's side).
     */
    public void addToRoot( Component compToAdd, int side ) {
        addToRoot( compToAdd, side, 0.5 );
    }
    
    /**
     * Add a component to the topmost cell (i.e. attach a component to the given SplitPane's side).
     * @param initialSize The portion of the root cell that will be used as new component's initial size.
     */
    public void addToRoot( Component compToAdd, int side, double initialSize ) {
        addToRoot( compToAdd, side, initialSize, 0.0 );
    }
    
    /**
     * Add a component to the topmost cell (i.e. attach a component to the given SplitPane's side).
     * @param initialSize The portion of the root cell that will be used as new component's initial size.
     * @param resizeWeight Component's resize weight, use 0.0 to preserve original component's size as much as possible.
     */
    public void addToRoot( Component compToAdd, int side, double initialSize, double resizeWeight ) {
        GridSplitCell newCell = root.addToSide( compToAdd, side, initialSize, resizeWeight );
        
        fireCellAdded( newCell );
    }
    
    /**
     * Append a component to given side of given existing model's component.
     */
    public void addToSide( Component neighbor, Component compToAdd, int side ) {
        addToSide( neighbor, compToAdd, side, 0.5 );
    }

    /**
     * Append a component to given side of given existing model's component.
     * @param initialSize The portion of the existing component's size that will be used as new component's initial size.
     */
    public void addToSide( Component neighbor, Component compToAdd, int side, double initialSize ) {
        addToSide( neighbor, compToAdd, side, initialSize, 0.0 );
    }
    
    /**
     * Append a component to given side of given existing model's component.
     * @param initialSize The portion of the existing component's size that will be used as new component's initial size.
     * @param resizeWeight New component's resize weight, use 0.0 to preserve original component's size as much as possible.
     */
    public void addToSide( Component neighbor, Component compToAdd, int side, double initialSize, double resizeWeight ) {
        GridSplitCell cell = findCellFor( neighbor );
        if( null == cell ) {
            throw new IllegalArgumentException( "Component not in hierarchy: " + neighbor );
        }
        
        GridSplitCell newCell = cell.addToSide( compToAdd, side, initialSize, resizeWeight );
        
        fireCellAdded( newCell );
    }

    /**
     * Remove component from model's hierarchy. Remaining cells may rearrange to merge
     * cells with the same split sides into one.
     */
    public void remove( Component c ) {
        GridSplitCell cell = findCellFor( c );
        if( null == cell ) {
            throw new IllegalArgumentException( "Component not in hierarchy: " + c );
        }
        
        if( cell == root ) {
            //what about this?
            throw new IllegalArgumentException( "Cannot remove the root cell" );
        }
        
        cell.getParent().remove( cell );
        
        fireCellRemoved( cell );
    }

    /**
     * Show/hide cell in the hierarchy. A hidden component is not visible in the 
     * split pane but it remembers its original position and size in the hierarchy
     * so it can 're-appear' at the some position when shown again.
     */
    public void setHidden( Component c, boolean hidden ) {
        GridSplitCell cell = findCellFor( c );
        if( null == cell ) {
            throw new IllegalArgumentException( "Component not in hierarchy: " + c );
        }
        
        if( cell.isHidden() == hidden )
            return;
        
        GridSplitCell oldValue = new GridSplitCell( cell );
                
        cell.setHidden( hidden );
        
        fireCellModified( oldValue, cell );
    }
    
    /**
     * @return True if given component is hidden in model's hierarchy.
     */
    public boolean isHidden( Component c ) {
        GridSplitCell cell = findCellFor( c );
        if( null == cell ) {
            throw new IllegalArgumentException( "Component not in hierarchy: " + c );
        }
        
        return cell.isHidden();
    }
    
    public void addChangeListener( GridSplitModelListener listener ) {
        synchronized( listeners ) {
            listeners.add( listener );
        }
    }
    
    public void removeChangeListener( GridSplitModelListener listener ) {
        synchronized( listeners ) {
            listeners.remove( listener );
        }
    }
    
    void fireCellAdded( GridSplitCell cell ) {
        GridSplitModelListener[] arr;
        synchronized( listeners ) {
            arr = new GridSplitModelListener[listeners.size()];
            listeners.toArray( arr );
        }
        for( int i=0; i<arr.length; i++ ) {
            arr[i].cellAdded( cell );
        }
    }
    
    void fireCellRemoved( GridSplitCell cell ) {
        GridSplitModelListener[] arr;
        synchronized( listeners ) {
            arr = new GridSplitModelListener[listeners.size()];
            listeners.toArray( arr );
        }
        for( int i=0; i<arr.length; i++ ) {
            arr[i].cellRemoved( cell );
        }
    }
    
    void fireCellModified( GridSplitCell oldCell, GridSplitCell newCell ) {
        GridSplitModelListener[] arr;
        synchronized( listeners ) {
            arr = new GridSplitModelListener[listeners.size()];
            listeners.toArray( arr );
        }
        for( int i=0; i<arr.length; i++ ) {
            arr[i].cellModified( oldCell, newCell );
        }
    }
    
    GridSplitCell findCellFor( Component c ) {
        if( null == root )
            return null;
        return findCell( root, c );
    }
    
    /**
     * Collection of all components this model contains.
     */
    Collection getComponents() {
        ArrayList res = new ArrayList();
        collectComponents( getRootCell(), res );
        return res;
    }
    
    private void collectComponents( GridSplitCell cell, Collection components ) {
        if( cell.isSplit() ) {
            for( int i=0; i<cell.count(); i++ ) {
                collectComponents( cell.cellAt( i ), components );
            }
        } else {
            components.add( cell.getComponent() );
        }
    }
    
    private static GridSplitCell findCell( GridSplitCell cell, Component c ) {
        if( cell.isSplit() ) {
            for( int i=0; i<cell.count(); i++ ) {
                GridSplitCell child = cell.cellAt( i );
                GridSplitCell res = findCell( child, c );
                if( null != res )
                    return res;
            }
            return null;
        } else {
            if( c == cell.getComponent() )
                return cell;
        }
        return null;
    }
}
