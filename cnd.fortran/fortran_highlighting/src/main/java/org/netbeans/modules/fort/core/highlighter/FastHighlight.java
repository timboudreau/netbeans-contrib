
package org.netbeans.modules.fort.core.highlighter;

import org.netbeans.editor.DrawContext;
import org.netbeans.editor.DrawLayer;
import org.netbeans.editor.DrawLayerFactory;
import org.netbeans.editor.MarkFactory;
import org.netbeans.editor.TokenID;

/**
 * highlighting support
 * @author Andrey Gubichev
 */
public abstract class FastHighlight extends DrawLayer.AbstractLayer {       
    
    /**
     * visibility
     */
    public static final int FAST_HIGHLIGHT_LAYER_VISIBILITY = 
                         DrawLayerFactory.HIGHLIGHT_SEARCH_LAYER_VISIBILITY;
     
    private boolean enabled;
    
    protected int []res;
    protected int cur;
    
    /**
     * creates a new instance of FastHighlighting
     */
    public FastHighlight(String name) {
        super(name);
        this.enabled = false;
        this.res = new int[0];
    }

    /**
     * set enabled
     */
    public void setEnabled(boolean enabled) {        
        this.enabled = enabled;                             
    }
    
    /**
     * initialize
     */
    public void init(DrawContext drawCtx) {
        super.init(drawCtx);
        cur = 0; 
    }

    /**
     * set result
     */
    public void setResult(int []res) {
        this.res = res;
        cur = 0;
    }
            
    /**
     * 
     * @return is active
     */
    public boolean isActive(DrawContext drawCtx, MarkFactory.DrawMark drawMark) {
        return enabled;
    }

    /**
     * update highlighter's context
     */
    public void updateContext(DrawContext drawCtx) {        
        TokenID tok = drawCtx.getTokenID();
        if (enabled && tok != null) {
            while (cur < res.length && res[cur] < drawCtx.getTokenOffset()) {
                cur++;
            }
            if (cur < res.length && res[cur] == drawCtx.getTokenOffset()) {                                            
                draw(drawCtx, cur);               
           }
       }
    }
    
    /**
     * draw token
     */
    protected abstract void draw(DrawContext drawCtx, int position);
}
