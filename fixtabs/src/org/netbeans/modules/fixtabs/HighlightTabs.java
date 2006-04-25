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
package org.netbeans.modules.fixtabs;

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
import org.netbeans.editor.FinderFactory;
import org.netbeans.editor.MarkFactory;
import org.netbeans.editor.Registry;
import org.netbeans.editor.Utilities;
import org.openide.util.Mutex;
import org.openide.util.WeakSet;


/**
 * Provides support for tab highlighting.
 * (This was based on the stripwhitespace module by Andrei Badea)
 *
 * @author Andrei Badea
 * @author Tor Norbye
 */
public class HighlightTabs implements PropertyChangeListener {
    private static final HighlightTabs DEFAULT = new HighlightTabs();
    private final Set /*<EditorUI>*/ INSTALLED = new WeakSet();

    private HighlightTabs() {
        ConvertTabsOptions.getDefault().addPropertyChangeListener(this);
    }

    public static HighlightTabs getDefault() {
        return DEFAULT;
    }

    public void install(final JTextComponent component) {
        if (component == null) {
            //No editors open
            return;
        }

        Mutex.EVENT.readAccess(new Runnable() {
                public void run() {
                    if (!ConvertTabsOptions.getDefault().getHighlightingEnabled()) {
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
        final boolean enabledChanged =
            (propertyName == null) ||
            propertyName.equals(ConvertTabsOptions.PROP_HIGHLIGHTING_ENABLED);
        final boolean colorChanged =
            (propertyName == null) ||
            propertyName.equals(ConvertTabsOptions.PROP_HIGHLIGHTING_COLOR);

        if (!enabledChanged && !colorChanged) {
            return;
        }

        Mutex.EVENT.readAccess(new Runnable() {
                public void run() {
                    boolean enabled = true;

                    if (enabledChanged) {
                        enabled = ConvertTabsOptions.getDefault().getHighlightingEnabled();
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
        private static final String NAME = "tabs-highlight-layer"; // NOI18N

        // just below the highlight whitespace layer (see whitespace highlighting module)
        private static final int VISIBILITY = 7990;
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
            int beginPos = ctx.getStartOffset();
            int endPos = ctx.getEndOffset();
            int pos = beginPos;
            int blockIndex = 0;

            curInd = 0;

            try {
                for (;;) {
                    int tab = doc.find(new FinderFactory.CharFwdFinder('\t'), pos, endPos);

                    if ((tab >= beginPos) && (tab < endPos)) {
                        blockIndex = addBlock(blockIndex, tab, tab + 1);
                    } else {
                        break;
                    }

                    pos = tab + 1;
                }
            } catch (BadLocationException e) {
                Utilities.annotateLoggable(e);
            } finally {
                blockIndex = addBlock(blockIndex, -1, -1);
            }
        }

        private int addBlock(int blockIndex, int start, int end) {
            if (blockIndex > (blocks.length - 2)) {
                int[] newBlocks = new int[(blocks.length > 0) ? (blocks.length * 2) : 2];
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

            if ((pos >= blocks[curInd]) && (pos < blocks[curInd + 1])) {
                if (coloring == null) {
                    coloring = new Coloring(null, null,
                            ConvertTabsOptions.getDefault().getHighlightingColor());
                }

                coloring.apply(ctx);
            }
        }
    }
}
