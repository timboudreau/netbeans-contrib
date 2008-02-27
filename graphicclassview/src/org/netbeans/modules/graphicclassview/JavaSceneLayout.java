package org.netbeans.modules.graphicclassview;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.UniversalGraph;
import org.netbeans.modules.graphicclassview.JavaScene.Conn;

class JavaSceneLayout extends GraphLayout <SceneElement, Conn> {

    private static class SC
            implements Comparator<SceneElement> {

        public int compare(SceneElement o1, SceneElement o2) {
            int outCount1 = o1.getOutboundReferences(SceneElement.class).size() - minOutgoing;
            int inCount1 = o1.getInboundReferences(SceneElement.class).size() - maxOutgoing;
            int outCount2 = o2.getOutboundReferences(SceneElement.class).size() - minOutgoing;
            int inCount2 = o2.getInboundReferences(SceneElement.class).size() - maxOutgoing;
            return (outCount1 + inCount1) - (outCount2 + inCount2);
        }
        private int maxOutgoing;
        private int maxIncoming;
        private int minOutgoing;
        private int minIncoming;

        SC(int maxIncoming, int maxOutgoing, int minOutgoing, int minIncoming) {
            this.maxIncoming = maxIncoming;
            this.maxOutgoing = maxOutgoing;
            this.minIncoming = minIncoming;
            this.minOutgoing = minOutgoing;
        }
    }

    JavaSceneLayout() {
    }

    protected void performGraphLayout(UniversalGraph<SceneElement, Conn> graph) {
        performNodesLayout(graph, graph.getNodes());
    }

    protected void xperformNodesLayout(UniversalGraph<SceneElement, Conn> graph, Collection<SceneElement> nodes) {
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

        double incomingRange = maxIncoming - minIncoming;
        double outgoingRange = maxOutgoing - maxIncoming;
        SceneElement e;
        double w;
        double h;
        double hBias;
        double vBias;
        for (Iterator i$ = nodes.iterator(); i$.hasNext(); setResolvedNodeLocation(graph, e, new Point((int) (w * hBias), (int) (h * vBias)))) {
            e = (SceneElement) i$.next();
            double outCount = e.getOutboundReferences(SceneElement.class).size() - minOutgoing;
            double inCount = e.getInboundReferences(SceneElement.class).size() - maxOutgoing;
            boolean weightUp = r.nextBoolean();
            w = 600D;
            h = 600D;
            hBias = outCount / (double) maxOutgoing;
            vBias = inCount / (double) maxIncoming;
            if (weightUp) {
                hBias /= 2D;
                vBias /= 2D;
            } else {
                hBias = 1.0D - hBias / 2D;
                vBias = 1.0D - vBias / 2D;
            }
        }

    }

    protected void performNodesLayout(UniversalGraph<SceneElement, Conn> graph, Collection<SceneElement> nodes) {
        if (nodes.size() == 0) {
            return;
        }
        int minOutgoing = 0x7fffffff;
        int maxOutgoing = 0x80000000;
        int minIncoming = 0x7fffffff;
        int maxIncoming = 0x80000000;
        for (Iterator i$ = nodes.iterator(); i$.hasNext();) {
            SceneElement e = (SceneElement) i$.next();
            minOutgoing = Math.min(e.getOutboundReferences(SceneElement.class).size(), minOutgoing);
            maxOutgoing = Math.max(e.getOutboundReferences(SceneElement.class).size(), maxOutgoing);
            minIncoming = Math.min(e.getInboundReferences(SceneElement.class).size(), minOutgoing);
            maxIncoming = Math.max(e.getInboundReferences(SceneElement.class).size(), maxOutgoing);
        }

//        double incomingRange = maxIncoming - minIncoming;
//        double outgoingRange = maxOutgoing - maxIncoming;
        List<SceneElement> l = new ArrayList<SceneElement>(nodes);
        Collections.sort(l, new SC(maxIncoming, maxOutgoing, minOutgoing, minIncoming));
        int size = l.size();
        int gw = (int) Math.sqrt(size);
        int gh = gw;
        if (gw * gh < size) {
            gh += size - gw * gh;
        }
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
                if (y >= 0) {
                    if (x >= 0) {
                        ;
                    }
                }
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
        int gap = 9;
        for (int xx = 0; xx < gw; xx++) {
            for (int yy = 0; yy < gh; yy++) {
                SceneElement e = grid[xx][yy];
                if (e != null) {
                    setResolvedNodeLocation(graph, e, new Point(xx * cellWidth + gap, yy * cellHeight + gap));
                }
            }
        }
    }
    private static final Random r = new Random(System.currentTimeMillis());
}
