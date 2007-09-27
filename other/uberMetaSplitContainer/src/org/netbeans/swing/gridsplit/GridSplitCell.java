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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * A wrapper class for a component displayed in GridSplitPane.
 *
 * @author Stanislav Aubrecht
 */
public class GridSplitCell {

    public static final int NORTH = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 4;
    public static final int EAST = 8;

    public static final int NO_SPLITTER = 0;
    public staticfinal int HORIZONTAL_SPLITTER = NORTH + SOUTH;
    public static final int VERTICAL_SPLITTER = WEST + EAST;
    
    
    //the parent cell in the hierarchy or null if this is a root cell
    private GridSplitCell parent;
    //children cell, empty if this is a leaf cell (i.e. cell with an actual swing component)
    private List children = new ArrayList();
    //split orientation, HORIZONTAL means that the split bars are vertical
    private int splitterOrientation = NO_SPLITTER;
    //swing component displayed in this cell or null if this cell has children cells
    private Component component;
    //defines the portion of the resizing delta consumed by this cell
    private double resizeWeight = 0.0;
    //normalized resize weight, used internally only
    private double normalizedResizeWeight = 0.0;
    //true if this cell is collapsed (has zero size), splitter is shown for this cell
    private boolean collapsed = false;
    //true if this component is hidden in the hierarchy, no splitter is visible for this cell
    private boolean isHidden = false;
    //cells current dimensions
    private Dimension dimension = new Dimension(0,0);
    //cells location in the SplitPane
    private Point location = new Point(0,0);
    
    GridSplitCell( Component c, double resizeWeight ) {
        this( c, resizeWeight, new Dimension(0,0) );
    }

    GridSplitCell( Component c, double resizeWeight, Dimension initialDimension ) {
        this.component = c;
        this.resizeWeight = resizeWeight;
        this.dimension.width = initialDimension.width; 
        this.dimension.height = initialDimension.height; 
    }
    
    /**
     * Copy constructor
     */
    GridSplitCell( GridSplitCell src ) {
        copy( src, this );
        this.parent = src.parent;
    }
    
    public GridSplitCell getParent() {
        return parent;
    }
    
    void setParent( GridSplitCell newParent ) {
        this.parent = newParent;
    }
    
    /**
     * @return Number of children cells or zero if this cell is not split.
     */
    public int count() {
        return children.size();
    }
    
    /**
     * @return Number of children cells that are not hidden (i.e. a split bar is shown for them)
     */
    public int countVisibleCells() {
        //TODO refactor this method
        return getVisibleCells().size();
    }
    
    /**
     * @return Children cell at given position.
     */
    public GridSplitCell cellAt( int index ) {
        return (GridSplitCell) children.get( index );
    }
    
    /**
     * @return True if this is a root cell (i.e. the top-most cell in the hierarchy).
     */
    public boolean isRootCell() {
        return null == getParent();
    }
    
    /**
     * Add a new cell with given compent to this component's side. This cell may split itself if needed.
     * @param compToAdd Component to be added to split hierarchy.
     * @param side This cell's side that will be shared with the new component.
     * @param initialSize The percentage of this cell's parent's size that will be used as new component's initial dimension.
     * @param resizeWeight New component's resize weight.
     * @return Split cell encapsulating the new component.
     */
    public GridSplitCell addToSide( Component compToAdd, int side, double initialSize, double resizeWeight ) {
        GridSplitCell newChild = new GridSplitCell( compToAdd, resizeWeight );
        GridSplitCell owner;
        GridSplitCell neighbor;
        if( isRootCell() ) {
            //adding a component to the root cell, i.e. the component will be attached 
            //to split pane's side
            owner = this;
            if( isSplitterSide( side ) ) {
                //the root cell is already split and the new component will share
                //a split side
                if( side == NORTH || side == WEST ) {
                    neighbor = cellAt( 0 );
                } else {
                    neighbor = cellAt( count()-1 );
                }
            } else {
                //the root is either not split yet or the share side does not match split orientation
                owner = this;
                neighbor = split();
            }
        } else {
            owner = getParent();
            if( owner.isSplitterSide( side ) ) {
                //the cell is already split and the shared side matches existing split orientation
                neighbor = this;
            } else {
                //the cell is either not split yet or the share side does not match split orientation
                owner = this;
                neighbor = split();
            }
        }
        owner.addToSide( neighbor, newChild, side, initialSize );
        return newChild;
    }
    
    /**
     * Split this cell. Its current contents is copied to a new child cell. 
     */
    private GridSplitCell split() {
        GridSplitCell child = new GridSplitCell( this );
        
        children.clear();
        children.add( child );
        child.setParent( this );
        component = null;
        
        return child;
    }
    
    /**
     * Add a new child cell and set its initial dimensions.
     */
    private void addToSide( GridSplitCell neighbor, GridSplitCell newChild, int side, double initialSize ) {
        Dimension parentSize = getDimension( 0 );
        splitterOrientation = (side == NORTH || side == SOUTH) ? VERTICAL_SPLITTER : HORIZONTAL_SPLITTER;
        int insertIndex = children.indexOf( neighbor );
        assert insertIndex >= 0;
        if( side == SOUTH || side == EAST )
            insertIndex++;
        if( insertIndex > children.size() )
            children.add( newChild );
        else
            children.add( insertIndex, newChild );
        if( isHorizontalSplitter() ) {
            newChild.dimension = new Dimension( (int)(parentSize.width*initialSize), parentSize.height );
        } else {
            newChild.dimension = new Dimension( parentSize.width, (int)(parentSize.height*initialSize) );
        }
        newChild.setParent( this );
    }
    
    /**
     * @return True if given side matches current splitter orientation.
     */
    boolean isSplitterSide( int side ) {
        return (isHorizontalSplitter() && (side == WEST || side == EAST))
            || (isVerticalSplitter() && (side == SOUTH || side == NORTH));
    }

    /**
     * Remove given cell from the list of children cells. The hierarchy is rearranged
     * if this cell has only one child left after the removal.
     */
    public void remove( GridSplitCell child ) {
        assert children.contains( child );
        
        children.remove( child );
        if( children.size() == 1 ) {
            GridSplitCell orphan = cellAt( 0 );
            
            if( null != getParent() ) {
                //replace this cell with orphan's contents in the parent cell
                getParent().replace( this, orphan );
            } else {
                //this is a root cell, so just take over all properties from the orphan
                copy( orphan, this );
            }
        }
    }
    
    /**
     * Replace the origCell with newCell.
     */
    private void replace( GridSplitCell origCell, GridSplitCell newCell ) {
        assert children.contains( origCell );
        int index = children.indexOf( origCell );
        if( newCell.isSplit() && newCell.splitterOrientation == splitterOrientation ) {
            children.addAll( index, newCell.children );
            for( Iterator i=newCell.children.iterator(); i.hasNext(); ) {
                GridSplitCell c = (GridSplitCell)i.next();
                c.setParent( this );
            }
            children.remove( origCell );
        } else {
            children.set( index, newCell );
            newCell.setParent( this );
        }
    }
    
    /**
     * @return True if this cell is a splitter cell.
     */
    public boolean isSplit() {
        return null == component || children.size() == 1;
    }
    
    public int getSplitOrientation() {
        return splitterOrientation;
    }
    
    public boolean isHorizontalSplitter() {
        return splitterOrientation == HORIZONTAL_SPLITTER;
    }
    
    public boolean isVerticalSplitter() {
        return splitterOrientation == VERTICAL_SPLITTER;
    }

    public Component getComponent() {
        return component;
    }

    /**
     * @param dividerSize The width of splitter bar.
     * @return The minimum size of this cell. If this cell is a split cell then the
     * result is a sum of minimum sizes of all children cells.
     */
    public Dimension getMinimumSize( int dividerSize ) {
        if( isCollapsed() || isHidden() ) {
            return new Dimension( 0, 0 );
        } else {
            if( isSplit() ) {
                return calculateMinimumSize( dividerSize );
            } else {
                return component.getMinimumSize();
            }
        }
    }
    
    /**
     * @return Sum of minimum sizes of all children cells.
     */
    private Dimension calculateMinimumSize( int dividerSize ) {
        Dimension minSize = new Dimension( 0, 0 );
        ArrayList visibleCells = getVisibleCells();
        for( Iterator i=visibleCells.iterator(); i.hasNext(); ) {
            GridSplitCell child = (GridSplitCell)i.next();
            
            Dimension childMinSize = child.getMinimumSize( dividerSize );
            
            if( isHorizontalSplitter() ) {
                minSize.width += childMinSize.width;
                if( childMinSize.height > minSize.height )
                    minSize.height = childMinSize.height;
            } else {
                minSize.height += childMinSize.height;
                if( childMinSize.width > minSize.width )
                    minSize.width = childMinSize.width;
            }
        }
        if( isHorizontalSplitter() ) {
            minSize.width += (visibleCells.size()-1)*dividerSize;
        } else {
            minSize.height += (visibleCells.size()-1)*dividerSize;
        } 
        
        return minSize;
    }

    /**
     * @return List of children cells that are not hidden.
     */
    ArrayList getVisibleCells() {
        ArrayList res = new ArrayList( children.size() );
        for( Iterator i=children.iterator(); i.hasNext(); ) {
            GridSplitCell child = (GridSplitCell)i.next();
            if( child.isHidden() )
                continue;
            res.add( child );
        }
        return res;
    }
    
    /**
     * @return List of children cells that can be resized (not hidden and not collapsed).
     */
    ArrayList getResizeableCells() {
        ArrayList res = new ArrayList( children.size() );
        for( Iterator i=children.iterator(); i.hasNext(); ) {
            GridSplitCell child = (GridSplitCell)i.next();
            if( child.isHidden() || child.isCollapsed() )
                continue;
            res.add( child );
        }
        return res;
    }
    
    /**
     * @return List of children cells with non-zero resize weight.
     */
    ArrayList getResizeHungryCells() {
        ArrayList res = new ArrayList( children.size() );
        for( Iterator i=children.iterator(); i.hasNext(); ) {
            GridSplitCell child = (GridSplitCell)i.next();
            if( child.isHidden() || child.isCollapsed() || child.resizeWeight == 0.0 )
                continue;
            res.add( child );
        }
        return res;
    }
    
    /**
     * Adjust cell's dimensions.
     */
    void resize( int width, int height, int dividerSize ) {
        if( isSplit() ) {
            
            //find out what the delta is
            int currentSize = getSize( this, dividerSize );
            int newNetSize = (isHorizontalSplitter() ? width : height);
            int delta = newNetSize - currentSize;
            System.out.println( "Delta is: " + delta );
            
            if( delta > 0 ) {
                //the child cells will grow
                
                grow( delta, dividerSize );
                
            } else if( delta < 0 ) {
                
                delta = shrink( delta, dividerSize );

                if( delta > 0 ) {
                    //the complete delta couldn't be distributed because of minimum sizes
                    System.out.println( "Remaining size not distributed: " + delta );
                    newNetSize -= delta;
                    if( isHorizontalSplitter() )
                        width -= delta;
                    else
                        height -= delta;
                }
            }
            
            //resize all child cells
            int totalSize = 0;
            ArrayList visibleCells = getVisibleCells();
            for( int i=0; i<visibleCells.size(); i++ ) {
                GridSplitCell child = (GridSplitCell)visibleCells.get( i );
                int childWidth = isHorizontalSplitter() ? child.getSize( this, dividerSize ) : width;
                int childHeight = isHorizontalSplitter() ? height : child.getSize( this, dividerSize );
                
                if( isHorizontalSplitter() ) {
                    totalSize += childWidth;
                    if( i == visibleCells.size()-1 && totalSize < newNetSize ) {
                        //XXX this is probably overkill, this adjustment is already done in distributeDelta()
                        //adjust rounding errors (+/- one pixel) for the last cell
                        System.out.println( "Extra width: " + (newNetSize - totalSize));
                        childWidth += newNetSize - totalSize;
                        //child.dimension.width += newNetSize - totalSize;
                    } else {
                        totalSize += dividerSize;
                    }
                } else {
                    totalSize += childHeight;
                    if( i == visibleCells.size()-1 &&  totalSize < newNetSize ) {
                        //adjust rounding errors (+/- one pixel) for the last cell
                        //XXX this is probably overkill, this adjustment is already done in distributeDelta()
                        System.out.println( "Extra height: " + (newNetSize - totalSize));
                        childHeight += newNetSize - totalSize;
                        //child.dimension.height += newNetSize - totalSize;
                    } else {
                        totalSize += dividerSize;
                    }
                }
                child.resize( childWidth, childHeight, dividerSize );
            }
            if( !(isCollapsed() || isHidden()) ) {
                if( isHorizontalSplitter() )
                    setDimension( new Dimension( newNetSize, height ) );
                else
                    setDimension( new Dimension( width, newNetSize ) );
            }
        } else {
            setDimension( new Dimension( width, height ) );
        }
    }
    
    /**
     * Grow children cell dimensions.
     */
    private void grow( int delta, int dividerSize ) {
        //children with resize weight > 0 that are not collapsed
        ArrayList hungryCells = getResizeHungryCells();

        //grow some/all child windows
        if( !hungryCells.isEmpty() ) {
            //we have children with non-zero resize weight so let them consume the whole delta
            normalizeResizeWeights( hungryCells );
            distributeDelta( delta, hungryCells, dividerSize );
        } else {
            //resize all children proportionally
            ArrayList resizeableCells = getResizeableCells();
            normalizeResizeWeights( resizeableCells );
            distributeDelta( delta, resizeableCells, dividerSize );
        }
    }
    
    /**
     * Shrink children cell dimensions.
     * The children cells will not shrink below their minimum sizes.
     *
     * @return The remaining resize delta that has not been distributed among children cells.
     */
    private int shrink( int negativeDelta, int dividerSize ) {
        int delta = -negativeDelta;

        //children with resize weight > 0 that are not collapsed
        ArrayList hungryCells = getResizeHungryCells();

        //first find out how much cells with non-zero resize weight can shrink
        int resizeArea = calculateShrinkableArea( hungryCells, dividerSize );
        if( resizeArea >= delta ) {
            resizeArea = delta;
            delta = 0;
        } else {
            delta -= resizeArea;
        }
        if( resizeArea > 0  ) {
            //shrink cells with non-zero resize weight
            distributeDelta( -resizeArea, hungryCells, dividerSize );
        }

        if( delta > 0 ) {
            //hungry cells did not consume the complete delta, 
            //distribute the remaining delta among other resizeable cells
            ArrayList resizeableCells = getResizeableCells();

            resizeArea = calculateShrinkableArea( resizeableCells, dividerSize );
            if( resizeArea >= delta ) {
                resizeArea = delta;
                delta = 0;
            } else {
                delta -= resizeArea;
            }
            if( resizeArea > 0 ) {
                distributeDelta( -resizeArea, resizeableCells, dividerSize );
            }
        }
        return delta;
    }
    
    /**
     * Sum up the available resize space of given cells. The resize space is the difference
     * between child cell's current size and child cell's minimum size.
     * Children cells that cannot be resized are removed from the given list and
     * resize weights of remaining cells are normalized.
     */
    private int calculateShrinkableArea( ArrayList cells, int dividerSize ) {
        int res = 0;
        ArrayList nonShrinkable = new ArrayList( cells.size() );
        for( int i=0; i<cells.size(); i++ ) {
            GridSplitCell c = (GridSplitCell)cells.get( i );
            if( c.isCollapsed() || c.isHidden() )
                continue;
            int currentSize = c.getSize( c.getParent(), dividerSize );
            int minSize = c.getMinSize( c.getParent(), dividerSize );
            if( currentSize - minSize > 0 ) {
                res += currentSize - minSize;
            } else {
                nonShrinkable.add( c );
            }
        }
        
        cells.removeAll( nonShrinkable );
        for( int i=0; i<cells.size(); i++ ) {
            GridSplitCell c = (GridSplitCell)cells.get( i );
            int currentSize = c.getSize( c.getParent(), dividerSize );
            int minSize = c.getMinSize( c.getParent(), dividerSize );
            c.normalizedResizeWeight = 1.0*(currentSize-minSize)/res;
        }        
        return res;
    }
    
    /**
     * Distribute the given delta among given cell dimensions using their normalized weights.
     */
    private void distributeDelta( int delta, ArrayList cells, int dividerSize ) {
        int totalDistributed = 0; 
        for( int i=0; i<cells.size(); i++ ) {
            GridSplitCell child = (GridSplitCell)cells.get( i );
            int childDelta = (int)(child.normalizedResizeWeight*delta);
            totalDistributed += childDelta;
            if( i == cells.size()-1 ) //fix rounding errors
                childDelta += delta - totalDistributed;
            child.setSize( this, child.getSize( this, dividerSize ) + childDelta );
        }
    }
    
    /**
     * Normalize resize weights so that their sum equals to 1.
     */
    private void normalizeResizeWeights( List cells ) {
        if( cells.isEmpty() )
            return;
        
        double totalWeight = 0.0;
        for( Iterator i=cells.iterator(); i.hasNext(); ) {
            GridSplitCell c = (GridSplitCell)i.next();
            totalWeight += c.resizeWeight;
        }
        
        double deltaWeight = (1.0 - totalWeight) / cells.size();

        for( Iterator i=cells.iterator(); i.hasNext(); ) {
            GridSplitCell c = (GridSplitCell)i.next();
            c.normalizedResizeWeight = c.resizeWeight + deltaWeight;
        }
    }
    
    /**
     * Set the collapsed flag for this cell and all its children.
     */
    public void setCollapsed( boolean collapsed ) {
        if( this.collapsed == collapsed ) {
            return;
        }
        this.collapsed = collapsed;
        if( isSplit() ) {
            for( Iterator i=children.iterator(); i.hasNext(); ) {
                GridSplitCell cell = (GridSplitCell)i.next();
                cell.setCollapsed( collapsed );
            }
        } else {
            component.setVisible( !collapsed );
        }
    }
    
    public boolean isCollapsed() {
        return collapsed;
    }
    
    public void setHidden( boolean hidden ) {
        this.isHidden = hidden;
    }
    
    public boolean isHidden() {
        return isHidden;
    }
    
    private int getSize( GridSplitCell splitCell, int dividerSize ) {
        if( splitCell.isHorizontalSplitter() )
            return getDimension( dividerSize ).width;
        return getDimension( dividerSize ).height;
    }
    
    private int getMinSize( GridSplitCell splitCell, int dividerSize ) {
        if( splitCell.isHorizontalSplitter() )
            return getMinimumSize( dividerSize ).width;
        return getMinimumSize( dividerSize ).height;
    }
    
    private void setSize( GridSplitCell splitCell, int newSize ) {
        if( isCollapsed() || isHidden() )
            return;
        
        if( splitCell.isHorizontalSplitter() )
            dimension.width = newSize;
        else 
            dimension.height = newSize;
    }
    
    private void setDimension( Dimension newDimension ) {
        if( isCollapsed() || isHidden() )
            return;
        
        this.dimension = newDimension;
    }
    
    public Dimension getDimension( int dividerSize ) {
        if( isHidden() || isCollapsed() )
            return new Dimension( 0, 0 );
        
        if( isSplit() ) {
            Dimension res = new Dimension( 0, 0 );
            ArrayList visibleCells = getVisibleCells();
            for( int i=0; i<visibleCells.size(); i++ ) {
                GridSplitCell child = (GridSplitCell)visibleCells.get( i );
                Dimension childDim = child.getDimension( dividerSize );
                if( isHorizontalSplitter() ) {
                    res.height = this.dimension.height;
                    res.width += childDim.width;
                } else {
                    res.height += childDim.height;
                    res.width = this.dimension.width;
                }
            }
            if( isHorizontalSplitter() ) {
                res.width += (visibleCells.size()-1)*dividerSize;
            } else {
                res.height += (visibleCells.size()-1)*dividerSize;
            }
            return res;
        }
        return new Dimension( dimension );
    }
    
    private static void copy( GridSplitCell source, GridSplitCell target ) {
        target.component = source.component;
        target.children = new ArrayList( source.children );
        for( Iterator i=target.children.iterator(); i.hasNext(); ) {
            GridSplitCell cell = (GridSplitCell)i.next();
            cell.setParent( target );
        }
        //target.dividerSize = source.dividerSize;
        target.resizeWeight = source.resizeWeight;
        target.splitterOrientation = source.splitterOrientation;
        target.collapsed = source.collapsed;
        target.dimension = new Dimension( source.dimension );
        target.isHidden = source.isHidden;
        target.location = new Point( source.location );
    }
    
    /**
     * Set cell's new location. If the cell contains an actual swing component
     * then its bounds and location are also set.
     */
    void setLocation( int x, int y, int dividerSize ) {
        location.x = x;
        location.y = y;
        if( isSplit() ) {
            ArrayList visibleCells = getVisibleCells();
            int childX = x;
            int childY = y;
            for( int i=0; i<visibleCells.size(); i++ ) {
                GridSplitCell child = (GridSplitCell)visibleCells.get( i );
                Dimension d = child.getDimension( dividerSize );
                child.setLocation( childX, childY, dividerSize );

                if( isHorizontalSplitter() )
                    childX += d.width + dividerSize;
                else
                    childY += d.height + dividerSize;
            }
        } else {
            component.setLocation( location );
            component.setSize( dimension );
        }
    }
    
    Point getLocation() {
        return new Point( location );
    }
}