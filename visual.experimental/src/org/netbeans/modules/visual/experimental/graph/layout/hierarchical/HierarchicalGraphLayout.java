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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.visual.experimental.graph.layout.hierarchical;

import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.UniversalGraph;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.visual.experimental.graph.layout.ConnectionManager;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 *
 * @author Thomas
 */
public class HierarchicalGraphLayout<N, E> extends GraphLayout<N, E>{
    
	
	private ConnectionManager<E> connectionManager;
	
	
    public HierarchicalGraphLayout() {
	    this(null);
    }
    
    public HierarchicalGraphLayout(ConnectionManager<E> connectionManager) {
	    this.connectionManager = connectionManager;
    }
    
    private class LinkWrapper implements Link {
        
        private VertexWrapper from;
        private VertexWrapper to;
	private E edge;
        
        public LinkWrapper(E edge, VertexWrapper from, VertexWrapper to) {
	    this.edge = edge;	
            this.from = from;
            this.to = to;
        }
        
        public Port getFrom() {
            return from.getSlot();
        }

        public Port getTo() {
            return to.getSlot();
        }

        public List<Point> getControlPoints() {
            return new ArrayList<Point>();
        }

        public void setControlPoints(List<Point> list) {
		if(connectionManager != null) {
			connectionManager.setControlPoints(edge, list);
		}
        }
    
    }
    
    private class VertexWrapper implements Vertex {
        
        private N node;
        private UniversalGraph<N, E> graph;
        private Port slot;
        private Point position;
        
        public VertexWrapper(N node, UniversalGraph<N, E> graph) {
            this.node = node;
            this.graph = graph;
            final VertexWrapper vertex = this;
            this.slot = new Port() {
                public Vertex getVertex() {
                    return vertex;
                }

                public Point getRelativePosition() {
                    return new Point((int)(vertex.getSize().getWidth()/2), (int)(vertex.getSize().getHeight()/2));
                }
                
            };
            
            Widget w = graph.getScene().findWidget(node);
            this.position = w.getPreferredLocation();
        }
        
        public Cluster getCluster() {
            return null;
        }

        public Dimension getSize() {
            Widget w = graph.getScene().findWidget(node);
            return w.getBounds().getSize();
        }

        public Point getPosition() {
            return position;
        }

        public void setPosition(Point p) {
            HierarchicalGraphLayout.this.setResolvedNodeLocation(graph, node, p);
            position = p;
	    System.out.println("Setting position of " + node + " to " + p);
        }

        public boolean isDirty() {
            return false;
        }

        public boolean isRoot() {
            return false;
        }

        public int compareTo(Vertex o) {
            VertexWrapper vw = (VertexWrapper)o;
            return node.toString().compareTo(vw.node.toString());
        }
        
        public Port getSlot() {
            return slot;
        }

        public boolean isExpanded() {
            return false;
        }

        public boolean isFixed() {
            return false;
        }

        public boolean isMarked() {
            return false;
        }
    
    }
    
    protected void performGraphLayout(UniversalGraph<N, E> graph) {

        
        Set<LinkWrapper> links = new HashSet<LinkWrapper>();
        Set<VertexWrapper> vertices = new HashSet<VertexWrapper>();
        Map<N, VertexWrapper> vertexMap = new HashMap<N, VertexWrapper>();
        
        for(N node : graph.getNodes()) {
            VertexWrapper v = new VertexWrapper(node, graph);
            vertexMap.put(node, v);
            vertices.add(v);
        }
        
        for(E edge : graph.getEdges()) {
            N source = graph.getEdgeSource(edge);
            N target = graph.getEdgeTarget(edge);
            LinkWrapper l = new LinkWrapper(edge, vertexMap.get(source), vertexMap.get(target));
            links.add(l);
        }
        
        HierarchicalLayoutManager m = new HierarchicalLayoutManager(HierarchicalLayoutManager.Combine.NONE);
        
        LayoutGraph layoutGraph = new LayoutGraph(links, vertices);
        m.doLayout(layoutGraph);
        
    }

    protected void performNodesLayout(UniversalGraph<N, E> graph, Collection<N> nodes) {
          //throw new UnsupportedOperationException();
    }
    
}
