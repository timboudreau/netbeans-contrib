package org.netbeans.modules.graphicclassview;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.UniversalGraph;
import org.netbeans.modules.graphicclassview.JavaScene.Conn;
import org.openide.util.TopologicalSortException;
import org.openide.util.Utilities;

public class TopologicalLayout extends GraphLayout <SceneElement, Conn> {

    TopologicalLayout() {
    }

    protected void performGraphLayout(UniversalGraph<SceneElement, Conn> graph) {
        performNodesLayout(graph, graph.getNodes());
    }

    protected void performNodesLayout(UniversalGraph<SceneElement, Conn> graph, Collection<SceneElement> nodes) {
        Set <SceneElement> hubs = findHubs(nodes);
        int x = 12;
        Set <SceneElement> done = new HashSet<SceneElement>();
        for (SceneElement hub : hubs) {
            System.err.println((new StringBuilder()).append("iter hub ").append(hub).append(System.currentTimeMillis()).toString());
            Set<SceneElement> edges = new HashSet<SceneElement>();
            edges.addAll(hub.getInboundReferences(SceneElement.class));
            edges.addAll(hub.getOutboundReferences(SceneElement.class));
            edges.removeAll(hubs);
            edges.removeAll(done);
            System.err.println((new StringBuilder()).append("  one round ").append(System.currentTimeMillis()).toString());
            x = (int) ((double) x + (20D + doGrid(graph, edges, x).getWidth()));
            done.addAll(edges);
        }
    }

    private Rectangle doGrid(UniversalGraph<SceneElement,Conn> graph, Collection<SceneElement> nodes, int xStart) {
        List<SceneElement> l = new ArrayList<SceneElement>(nodes);
        try {
            Map <SceneElement, Set<SceneElement>> m = new IdentityHashMap<SceneElement, Set<SceneElement>>();
            SceneElement e;
            Set<SceneElement> edges;
            for (Iterator i$ = l.iterator(); i$.hasNext(); m.put(e, edges)) {
                e = (SceneElement) i$.next();
                edges = new HashSet<SceneElement>();
                edges.addAll(e.getOutboundReferences(SceneElement.class));
            }
            Utilities.topologicalSort(l, m);
        } catch (TopologicalSortException topologicalSortException) {
            topologicalSortException.printStackTrace();
        }
        int size = l.size();
        int gw = (int) Math.sqrt(size);
        int gh = gw;
        if (gw * gh < size) {
            gh += size - gw * gh;
        }
        Rectangle result = new Rectangle();
        int gap = 5;
        result.x = xStart;
        result.y = gap;
        SceneElement grid[][] = new SceneElement[gw][gh];
        int total = 0;
        int x = 0;
        int y = 0;
        int left = 0;
        int right = gw;
        int top = 0;
        int bottom = gh;
        int iter = 0;
        label0:
        do {
            if (total >= size) {
                break;
            }
            for (; x < right - 1; x++) {
                grid[x][y] = l.get(total);
                if (++total == size) {
                    break label0;
                }
            }

            top++;
            for (; y < bottom - 1; y++) {
                grid[x][y] = l.get(total);
                if (++total == size) {
                    break label0;
                }
            }

            right--;
            for (; x > left; x--) {
                grid[x][y] = l.get(total);
                if (++total == size) {
                    break label0;
                }
            }

            bottom--;
            for (; y > top; y--) {
                grid[x][y] = l.get(total);
                if (++total == size) {
                    break label0;
                }
            }

            left++;
            iter++;
        } while (true);
        int cellWidth = 275;
        int cellHeight = 90;
        int maxY = 0;
        int maxX = 0;
        for (int xx = 0; xx < gw; xx++) {
            for (int yy = 0; yy < gh; yy++) {
                SceneElement e = grid[xx][yy];
                if (e != null) {
                    int ypos = yy * (cellHeight + gap);
                    int xpos = result.x + xx * (cellWidth + gap);
                    maxX = Math.max(xpos + cellWidth, maxX);
                    maxY = Math.max(ypos + cellHeight, maxY);
                    setResolvedNodeLocation(graph, e, new Point(xpos, ypos));
                }
            }

        }

        result.width = (maxX + gap) - result.x;
        result.height = (maxY + gap) - result.y;
        System.err.println((new StringBuilder()).append("Bounding rect ").append(result).toString());
        return result;
    }

    private Set<SceneElement> findHubs(Collection<SceneElement> nodes) {
        int minOutgoing = 0x7fffffff;
        int maxOutgoing = 0x80000000;
        int minIncoming = 0x7fffffff;
        int maxIncoming = 0x80000000;
        for (SceneElement e : nodes) {
            minOutgoing = Math.min(e.getOutboundReferences(SceneElement.class).size(), minOutgoing);
            maxOutgoing = Math.max(e.getOutboundReferences(SceneElement.class).size(), maxOutgoing);
            minIncoming = Math.min(e.getInboundReferences(SceneElement.class).size(), minOutgoing);
            maxIncoming = Math.max(e.getInboundReferences(SceneElement.class).size(), maxOutgoing);
        }

        int incomingRange = maxIncoming - minIncoming;
        int outgoingRange = maxOutgoing - maxIncoming;
        Set<SceneElement> result = new HashSet <SceneElement>();
        int range = Math.max(incomingRange, outgoingRange);
        int amt = (int) ((double) range - (double) range * 0.20000000000000001D);
        for (SceneElement e : nodes) {
            int outs = e.getOutboundReferences(SceneElement.class).size() - minOutgoing;
            int ins = e.getInboundReferences(SceneElement.class).size() - minIncoming;
            if (ins + outs > amt) {
                result.add(e);
            }
        }
        return result;
    }
}
