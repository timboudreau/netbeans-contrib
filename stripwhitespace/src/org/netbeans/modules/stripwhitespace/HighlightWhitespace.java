/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.stripwhitespace;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.DrawContext;
import org.netbeans.editor.DrawLayer;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.MarkFactory;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Utilities;
import org.openide.util.Mutex;
import org.openide.util.WeakSet;

/**
 * Provides support for whitespace highlighting.
 *
 * @author Andrei Badea
 */
public class HighlightWhitespace implements PropertyChangeListener {

    private static final HighlightWhitespace DEFAULT = new HighlightWhitespace();

    private final Set/*<EditorUI>*/ INSTALLED = new WeakSet();

    public static HighlightWhitespace getDefault() {
        return DEFAULT;
    }

    private HighlightWhitespace() {
        StripWhitespaceOptions.getDefault().addPropertyChangeListener(this);
    }

    public void install(final JTextComponent component) {
        if (component == null) {
            //No editors open
            return;
        }
        Mutex.EVENT.readAccess(new Runnable() {

            public void run() {
                if (!StripWhitespaceOptions.getDefault().getHighlightingEnabled()) {
                    return;
                }

                EditorUI editorUI = Utilities.getEditorUI(component);
                if (editorUI == null) {
                    return;
                }

                Layer layer = (Layer)editorUI.findLayer(Layer.NAME);
                if (layer == null) {
                    layer = new Layer();
                    editorUI.addLayer(new Layer(), Layer.VISIBILITY);
                    INSTALLED.add(editorUI);

                    // needed in order to repaint the active component when the module
                    // is installed/enabled through Module Manager/Update Center
                    editorUI.repaint(0);
                }
            }
        });
    }

    public void uninstall() {
        Mutex.EVENT.readAccess(new Runnable() {

            public void run() {
                for (Iterator i = INSTALLED.iterator(); i.hasNext();) {
                    EditorUI editorUI = (EditorUI)i.next();
                    editorUI.removeLayer(Layer.NAME);
                }
                INSTALLED.clear();

                // repainting the active component when the module
                // is installed/enabled through Module Manager/Update Center
                repaintMostActiveComponent();
            }
        });
    }

    public void setColor(final JTextComponent component, final Color color) {
        Mutex.EVENT.readAccess(new Runnable() {

            public void run() {
                EditorUI editorUI = Utilities.getEditorUI(component);
                if (editorUI == null) {
                    return;
                }

                Layer layer = (Layer)editorUI.findLayer(Layer.NAME);
                if (layer == null) {
                    return;
                }

                layer.setColor(color);
                editorUI.repaint(0);
            }
        });
    }

    private void repaintMostActiveComponent() {
        assert SwingUtilities.isEventDispatchThread();

        EditorUI editorUI = Utilities.getEditorUI(Registry.getMostActiveComponent());
        if (editorUI != null) {
            editorUI.repaint(0);
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        String propertyName = event.getPropertyName();
        final boolean enabledChanged = propertyName == null || propertyName.equals(StripWhitespaceOptions.PROP_HIGHLIGHTING_ENABLED);
        final boolean colorChanged = propertyName == null || propertyName.equals(StripWhitespaceOptions.PROP_HIGHLIGHTING_COLOR);

        if (!enabledChanged && !colorChanged) {
            return;
        }

        Mutex.EVENT.readAccess(new Runnable() {

            public void run() {

                boolean enabled = true;

                if (enabledChanged) {
                    enabled = StripWhitespaceOptions.getDefault().getHighlightingEnabled();
                }

                for (Iterator i = INSTALLED.iterator(); i.hasNext();) {
                    EditorUI editorUI = (EditorUI)i.next();
                    Layer layer = (Layer)editorUI.findLayer(Layer.NAME);
                    if (layer != null) {
                        if (enabledChanged) {
                            layer.setEnabled(enabled);
                        }
                        if (colorChanged) {
                            layer.colorChanged();
                        }
                    }
                }

                if (enabled && enabledChanged) {
                    // enabled -> true
                    // handle the case when the layer has not been yet installed in the active component
                    install(Registry.getMostActiveComponent());
                }
                repaintMostActiveComponent();
            }
        });
    }

    private static final class Layer extends DrawLayer.AbstractLayer {

        private static final String NAME = "whitespace-highlight-layer"; // NOI18N

        // just below the block search layer (see DrawLayerFactory)
        private static final int VISIBILITY = 8000;

        private boolean enabled = true;

        private Coloring coloring;

        private int[] blocks = new int[] { -1, -1 };
        private int curInd = 0;

        public Layer() {
            super(NAME);
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setColor(Color color) {
            coloring = new Coloring(null, null, color);
        }

        public void colorChanged() {
            coloring = null;
            // will be recreated in updateContext
        }

        public void init(DrawContext ctx) {
            if (!enabled) {
                return;
            }

            BaseDocument doc = (BaseDocument)ctx.getEditorUI().getDocument();
            int pos = ctx.getStartOffset();
            int endPos = ctx.getEndOffset();
            int blockIndex = 0;

            curInd = 0;

            try {
                for (;;) {
                    int rowEnd = Utilities.getRowEnd(doc, pos) + 1;
                    int lastNonWhite = Utilities.getRowLastNonWhite(doc, pos);

                    int firstWhite;
                    if (lastNonWhite == -1) {
                        firstWhite = Utilities.getRowStart(doc, pos);
                    } else {
                        firstWhite = lastNonWhite + 1;
                    }

                    if (firstWhite < rowEnd) {
                        blockIndex = addBlock(blockIndex, firstWhite, rowEnd);
                    }

                    if (rowEnd >= endPos) {
                        break;
                    }

                    pos = rowEnd + 1; // beginning of the next row
                }
            } catch (BadLocationException e) {
                Utilities.annotateLoggable(e);
            } finally {
                blockIndex = addBlock(blockIndex, -1, -1);
            }
        }

        private int addBlock(int blockIndex, int start, int end) {
            if (blockIndex > blocks.length - 2) {
                int[] newBlocks = new int[blocks.length > 0 ? blocks.length * 2 : 2];
                System.arraycopy(blocks, 0, newBlocks, 0, blocks.length);
                blocks = newBlocks;
                // we never shrink the blocks array, but in practice it
                // doesn't seem to matter, because the editor calls init() for each line,
                // so the max length of the array will be 4 (ws start, ws end, -1, -1)
            }
            blocks[blockIndex++] = start;
            blocks[blockIndex++] = end;
            return blockIndex;
        }

        public boolean isActive(DrawContext ctx, MarkFactory.DrawMark mark) {
            if (!enabled) {
                return false;
            }

            boolean active;

            int pos = ctx.getFragmentOffset();
            if (pos == blocks[curInd]) {
                active = true;
                setNextActivityChangeOffset(blocks[curInd + 1]);

            } else if (pos == blocks[curInd + 1]) {
                active = false;
                curInd += 2;
                setNextActivityChangeOffset(blocks[curInd]);
                if (pos == blocks[curInd]) { // just follows
                    setNextActivityChangeOffset(blocks[curInd + 1]);
                    active = true;
                }

            } else {
                setNextActivityChangeOffset(blocks[curInd]);
                active = false;
            }

            return active;
        }

        public void updateContext(DrawContext ctx) {
            int pos = ctx.getFragmentOffset();
            if (pos >= blocks[curInd] && pos < blocks[curInd + 1]) {
                if (coloring == null) {
                    coloring = new Coloring(null, null, StripWhitespaceOptions.getDefault().getHighlightingColor());
                }
                coloring.apply(ctx);
            }
        }
    }
}
