/*
 * FsUrlDisplayer.java
 *
 * Created on February 14, 2007, 7:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.flyingsaucer;

import java.net.URL;
import org.openide.awt.HtmlBrowser.URLDisplayer;

/**
 *
 * @author Tim
 */
public class FsUrlDisplayer extends URLDisplayer {
    
    public FsUrlDisplayer() {
    }
    
    public void showURL(URL u) {
        new FlyingSaucerTopComponent (u).open();
    }
}
