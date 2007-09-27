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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Wrapper class for GridSplitPane's split divider rectangle.
 */
public class GridSplitDivider {

    GridSplitPane splitPane;
    Rectangle rect = new Rectangle();
    GridSplitCell first;
    GridSplitCell second;
    int size;
    static Border border = BorderFactory.createRaisedBevelBorder();
    
    Point currentDragLocation;
    int dragMin;
    int dragMax;

    public GridSplitDivider( GridSplitPane parent, int size, GridSplitCell first, GridSplitCell second ) {
        assert null != parent;
        assert null != first;
        assert null != second;
        this.splitPane = parent;
        this.size = size;
        this.first = first;
        this.second = second;
    }

    boolean isHorizontal() {
        return first.getParent().isHorizontalSplitter();
    }

    boolean isVertical() {
        return first.getParent().isVerticalSplitter();
    }
    
    int getDividerSize() {
        if( isHorizontal() )
            return rect.width;
        return rect.height;
    }

    boolean containsPoint( Point p ) {
        return rect.contains( p );
    }
    
    int getSize() {
        return size;
    }
    
    void paint( Graphics g ) {
        if( first.getParent().isCollapsed() )
            return;
        
        border.paintBorder( splitPane, g, rect.x, rect.y, rect.width, rect.height );
        
        paintCollapseButtons( g );
        
        if( null != currentDragLocation ) {
            Color oldColor = g.getColor();
            g.setColor( Color.BLACK );
            if( isHorizontal() ) {
                if( currentDragLocation.x != rect.x ) {
                    g.fillRect( currentDragLocation.x, rect.y, rect.width, rect.height );
                }
            } else {
                if( currentDragLocation.y != rect.y ) {
                    g.fillRect( rect.x, currentDragLocation.y, rect.width, rect.height );
                }
            }
            g.setColor( oldColor );
        }
    }
    
    void paintCollapseButtons( Graphics g ) {
        Color oldColor = g.getColor();
        
        g.setColor( Color.RED );
        Rectangle collapseRect = getCollapseFirstRect();
        g.fillRect( collapseRect.x, collapseRect.y, collapseRect.width, collapseRect.height );

        g.setColor( Color.GREEN );
        collapseRect = getCollapseSecondRect();
        g.fillRect( collapseRect.x, collapseRect.y, collapseRect.width, collapseRect.height );
        
        g.setColor( oldColor );
    }
    
    Rectangle getCollapseFirstRect() {
        if( first.getParent().isCollapsed() )
            return new Rectangle( 0,0,0,0 );
        Rectangle res = new Rectangle( rect );
        if( isHorizontal() ) {
            res.y += res.width;
            res.height = 2*res.width;
        } else {
            res.x += res.height;
            res.width = 2*res.height;
        }
        return res;
    }
    
    Rectangle getCollapseSecondRect() {
        if( first.getParent().isCollapsed() )
            return new Rectangle( 0,0,0,0 );
        Rectangle res = getCollapseFirstRect();
        if( isHorizontal() ) {
            res.y += 4*res.width;
        } else {
            res.x += 4*res.height;
        }
        return res;
    }
    
    void startDragging( Point p ) {
        currentDragLocation = new Point( rect.x, rect.y );
        
        initDragMinMax();
    }
    
    void dragTo( Point p ) {
        if( isHorizontal() ) {
            if( p.x < dragMin )
                p.x = dragMin;
            if( p.x > dragMax )
                p.x = dragMax;
        } else {
            if( p.y < dragMin )
                p.y = dragMin;
            if( p.y > dragMax )
                p.y = dragMax;
        }
        
        Point prevDragLocation = currentDragLocation;
        currentDragLocation = p;
        
        repaintSplitPane( prevDragLocation );
        repaintSplitPane( currentDragLocation );
    }
    
    private void repaintSplitPane( Point location ) {
        if( isHorizontal() ) {
            splitPane.repaint( location.x, rect.y, rect.width, rect.height );
        } else {
            splitPane.repaint( rect.x, location.y, rect.width, rect.height );
        }
    }
    
    void finishDraggingTo( Point p ) {
        if( isHorizontal() ) {
            if( p.x < dragMin )
                p.x = dragMin;
            if( p.x > dragMax )
                p.x = dragMax;
        } else {
            if( p.y < dragMin )
                p.y = dragMin;
            if( p.y > dragMax )
                p.y = dragMax;
        }
        currentDragLocation = null;
    
        int dividerSize = getDividerSize();
        first.setCollapsed( false );
        second.setCollapsed( false );
        
        if( isHorizontal() ) {
            int delta = p.x - rect.x;
            Dimension d = first.getDimension( dividerSize );
            Point location = first.getLocation();
            d.width += delta;
            d.height = rect.height;
            first.resize( d.width, d.height, dividerSize );
            first.setLocation( location.x, location.y, dividerSize );
            
            d = second.getDimension( dividerSize );
            location = second.getLocation();
            d.width -= delta;
            d.height = rect.height;
            location.x += delta;
            second.resize( d.width, d.height, dividerSize );
            second.setLocation( location.x, location.y, dividerSize );
            
            rect.x = p.x;
        } else {
            int delta = p.y - rect.y;
            Dimension d = first.getDimension( dividerSize );
            Point location = first.getLocation();
            d.height += delta;
            d.width = rect.width;
            first.resize( d.width, d.height, dividerSize );
            first.setLocation( location.x, location.y, dividerSize );
            
            d = second.getDimension( dividerSize );
            location = second.getLocation();
            d.height -= delta;
            d.width = rect.width;
            location.y += delta;
            second.resize( d.width, d.height, dividerSize );
            second.setLocation( location.x, location.y, dividerSize );

            rect.y = p.y;
        }
        splitPane.validate();//invalidate();
    }
    
    private void initDragMinMax() {
        Point firstLocation = first.getLocation();
        int dividerSize = getDividerSize();
        Dimension firstDim = first.getDimension( dividerSize );
        Dimension secondDim = second.getDimension( dividerSize );
        Dimension firstMinSize = first.getMinimumSize( dividerSize );
        Dimension secondMinSize = second.getMinimumSize( dividerSize );
        
        if( isHorizontal() ) {
            dragMin = rect.x;
            dragMax = rect.x;
            
            if( firstDim.width >= firstMinSize.width ) {
                dragMin -= firstDim.width-firstMinSize.width;//-firstLocation.x;
            }
            if( secondDim.width >= secondMinSize.width ) {
                dragMax += secondDim.width-secondMinSize.width;
            }
        } else {
            dragMin = rect.y;
            dragMax = rect.y;
            if( firstDim.height >= firstMinSize.height ) {
                dragMin -= firstDim.height-firstMinSize.height;//-firstLocation.y;
            }
            if( secondDim.height >= secondMinSize.height ) {
                dragMax += secondDim.height-secondMinSize.height;
            }
        }
    }
    
    void reshape() {
        if( first.getParent().isCollapsed() ) {
            rect.x = 0;
            rect.y = 0;
            rect.width = 0;
            rect.height = 0;
            return;
        }
        Dimension d = first.getParent().getDimension( getDividerSize() );
        Point location = second.getLocation();

        if( isHorizontal() ) {
            rect.x = location.x-getSize();
            rect.y = location.y;
            rect.width = getSize();
            rect.height = d.height;
        } else {
            rect.x = location.x;
            rect.y = location.y-getSize();
            rect.width = d.width;
            rect.height = getSize();
        }
    }
    
    boolean isOnCollapseButton( Point p ) {
        return getCollapseFirstRect().contains( p ) || getCollapseSecondRect().contains( p );
    }
    
    boolean collapse( Point p ) {
        Rectangle firstRect = getCollapseFirstRect();
        Rectangle secondRect = getCollapseSecondRect();
        
        Dimension d = first.getParent().getDimension( getDividerSize() );
        Point location = first.getParent().getLocation();
        boolean res = false;
        if( firstRect.contains( p ) ) {
            first.setCollapsed( !first.isCollapsed() );
            second.setCollapsed( false );
            res = true;
        }
        if( secondRect.contains( p ) ) {
            second.setCollapsed( !second.isCollapsed() );
            first.setCollapsed( false );
            res = true;
        }
        if( res ) {
//            first.getParent().resize( d.width, d.height, getDividerSize() );
//            first.getParent().setLocation( location.x, location.y, getDividerSize() );
            splitPane.invalidate();
        }
        return res;
    }
}
