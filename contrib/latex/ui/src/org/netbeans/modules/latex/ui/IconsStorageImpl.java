/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.latex.model.IconsStorage;
import org.netbeans.modules.latex.model.Queue;
import org.netbeans.modules.latex.ui.IconsCreator;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public final class IconsStorageImpl extends IconsStorage {
    
    private Map/*<String, List<String>>*/ cathegory2Names;
    
    /** Creates a new instance of IconsStorageImpl */
    public IconsStorageImpl() {
//        Thread.dumpStack();
        expression2Icon = new HashMap/*<String, SoftReference<Icon>>*/();
        listeners = new HashMap();
        iconsToCreate = new Queue();
        iconsCreator  = new RequestProcessor("LaTeX Icons Creator");
        
        iconsCreator.post(new IconCreatorTask());
    }

    public ChangeableIcon getIcon(String command) {
        return getIconForExpression(command, 16, 16);
    }

    public boolean getIconsInstalled() {
        return IconsCreator.getDefault().isConfigurationUsable();
    }

    public List getIconNamesForCathegory(String catName) {
        assureLoaded();
        
        return (List) cathegory2Names.get(catName);
    }

    public List getAllIconNames() {
        assureLoaded();
        
        List result = new ArrayList();
        
        for (Iterator i = cathegory2Names.values().iterator(); i.hasNext(); ) {
            result.addAll((List) i.next());
        }
        
        return result;
    }
    
    private File getIconDirectory() throws IOException {
        File iconDir = new File(new File(new File(new File(System.getProperty("netbeans.user"), "var"), "cache"), "latex"), "icons");
        
        return iconDir;
    }

    public Collection getCathegories() {
        assureLoaded();
        return cathegory2Names.keySet();
    }
    
    private void addIconDescription(String iconDescription) {
        if ("".equals(iconDescription))
            return ;
        
        int colon = iconDescription.indexOf(':');
        String command = "";
        String attributes = "";
        
        if (colon == (-1)) {
            command = iconDescription;
        } else {
            command = iconDescription.substring(0, colon);
            attributes = iconDescription.substring(colon + 1);
        }
        
        String cathegory = DEFAULT_CATHEGORY;
        Pattern p = Pattern.compile(".*symbols_([^,]*),?.*");
        Matcher m = p.matcher(attributes);
        
        if (m.find()) {
            cathegory = m.group(1);
        } else {
            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "latex.IconsStorageImpl: default cathegory for \"" + iconDescription + "\"");
        }
        
        List names = (List) cathegory2Names.get(cathegory);
        
        if (names == null) {
            names = new ArrayList();
            
            cathegory2Names.put(cathegory, names);
        }
        
        names.add(command);
    }
    
    private synchronized void assureLoaded() {
        if (cathegory2Names != null)
            return ;
        
        cathegory2Names = new HashMap();
        
        String[] icons = readIconsFile();
        
        for (int cntr = 0; cntr < icons.length; cntr++) {
            addIconDescription(icons[cntr]);
        }
    }
    
    public/*module private*/ static String[] readIconsFile() {
        try {
            InputStream ins = IconsStorageImpl.class.getResourceAsStream("/org/netbeans/modules/latex/ui/symbols/symbols.txt");
            BufferedReader bins = new BufferedReader(new InputStreamReader(ins));
            List icons = new ArrayList();
            String line;
            
            while ((line = bins.readLine()) != null) {
                icons.add(line);
            }
            
            return (String[] ) icons.toArray(new String[0]);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            return new String[0];
        }
    }

    public String getCathegoryDisplayName(String catName) {
        try {
            return NbBundle.getBundle(IconsStorageImpl.class).getString("CATLBL_" + catName);
        } catch (MissingResourceException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            return catName;
        }
    }
    
    private Map/*<String, SoftReference<Icon>>*/ expression2Icon;
    
    private final RequestProcessor iconsCreator;
    private final Queue iconsToCreate;
    
    private class IconCreatorTask implements Runnable {
        public void run() {
            while (true) {
                DelegatingIcon toCreate = null;
                
                synchronized (iconsToCreate) {
                    while (iconsToCreate.empty()) {
                        try {
                            iconsToCreate.wait();
                        } catch (InterruptedException e) {
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                        }
                    }
                    
                    toCreate = (DelegatingIcon) iconsToCreate.pop();
                }
                
                createOrLoadIcon(toCreate);
            }
        }
    }
    
    private String constructSizeString(DelegatingIcon icon) {
        if (icon.getDesiredSizeX() == (-1) || icon.getDesiredSizeY() == (-1))
            return null;
        
        return "" + icon.getDesiredSizeX() + "x" + icon.getDesiredSizeY();
    }
    
    private void createOrLoadIcon(DelegatingIcon icon) {
        Icon i = null;
        
        try {
            File iconFile = new File(getIconDirectory(), IconsCreator.constructFileName(icon.getText(), constructSizeString(icon)));
            
            if (!iconFile.exists()) {
                IconsCreator creator = IconsCreator.getDefault();
                
                creator.createIconForExpression(icon.getText(), constructSizeString(icon));
            }
            
            i = new ImageIcon(iconFile.getAbsolutePath());
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            i = new ImageIcon();
        }
        
        icon.setIcon(i);
    }
    
    private ChangeableIcon getIconForExpressionImpl(String expression, int sizeX, int sizeY) {
        DelegatingIcon icon = new DelegatingIcon(expression, sizeX, sizeY);
        
        synchronized (iconsToCreate) {
            iconsToCreate.put(icon);
            iconsToCreate.notifyAll();
        }
        
        return icon;
    }

    public ChangeableIcon getIconForExpression(String expression) {
        return getIconForExpression(expression, -1, -1);
    }
    
    public ChangeableIcon getIconForExpression(String expression, int sizeX, int sizeY) {
        ChangeableIcon i = null;
        SoftReference sr = (SoftReference) expression2Icon.get(expression2Icon);
        
        if (sr != null) {
            i = (ChangeableIcon) sr.get();
        }
        
        if (i == null) {
            i = getIconForExpressionImpl(expression, sizeX, sizeY);
            expression2Icon.put(expression, new SoftReference(i));
        }
        
        return i;
    }
    
    private Map/*<DelegatingIcon, ChangeListener or List<ChangeListener>>*/ listeners;
    
    private class DelegatingIcon implements ChangeableIcon {
        
        private Icon   delegateTo;
        private String text;
        private int    desiredSizeX;
        private int    desiredSizeY;
        
        private int    textSizeX;
        private int    textSizeY;
        
        public DelegatingIcon(Icon delegateTo, String text, int desiredSizeX, int desiredSizeY) {
            this.delegateTo = delegateTo;
            this.text       = text;
            this.desiredSizeX = desiredSizeX;
            this.desiredSizeY = desiredSizeY;
            this.textSizeX = (-1);
            this.textSizeY = (-1);
        }
        
        public DelegatingIcon(String text, int desiredSizeX, int desiredSizeY) {
            this(null, text, desiredSizeX, desiredSizeY);
        }
        
        public DelegatingIcon(String text) {
            this(null, text, -1, -1);
        }
        
        //Only enclosing class should call this:
        private void setIcon(Icon delegateTo) {
            this.delegateTo = delegateTo;
            
            fireStateChanged();
        }
        
        private String getText() {
            return text;
        }
        
        private int getDesiredSizeX() {
            return desiredSizeX;
        }
        
        private int getDesiredSizeY() {
            return desiredSizeY;
        }
        
        private void fireStateChanged() {
            ChangeListener[] listenersList = null;
            
            synchronized (IconsStorageImpl.this) {
                Object content = listeners.get(this);
                
                if (content == null)
                    return ;
                
                if (content instanceof ChangeListener) {
                    listenersList = new ChangeListener[] {(ChangeListener) content};
                } else {
                    listenersList = (ChangeListener[] ) ((List) content).toArray(new ChangeListener[0]);
                }
            }
            
            ChangeEvent evt = new ChangeEvent(this);
            
            for (int cntr = 0; cntr < listenersList.length; cntr++) {
                listenersList[cntr].stateChanged(evt);
            }
        }
        
        public void removeChangeListener(ChangeListener l) {
            synchronized (IconsStorageImpl.this) {
                Object content = listeners.get(this);
                
                if (content == null) {
                    return ;
                }
                
                if (content instanceof ChangeListener) {
                    if (content == l) {
                        listeners.remove(this);
                    }
                    return ;
                }
                
                ((List) content).remove(l);
            }
        }

        public void addChangeListener(ChangeListener l) {
            synchronized (IconsStorageImpl.this) {
                Object content = listeners.get(this);
                
                if (content == null) {
                    listeners.put(this, l);
                    return ;
                }
                
                if (content instanceof ChangeListener) {
                    if (content != l) {
                        List listenersList = new ArrayList();
                        
                        listenersList.add(content);
                        listenersList.add(l);
                        listeners.put(this, listenersList);
                    }
                    return ;
                }
                
                ((List) content).add(l);
            }
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (delegateTo != null) {
                delegateTo.paintIcon(c, g, x, y);
            } else {
                //draw text:
                Color oldColor = g.getColor();
                Font oldFont = g.getFont();
                
                if (c != null) {
                    g.setColor(c.getForeground());
                } else {
                    g.setColor(Color.BLACK);
                }
                
                g.setFont(Font.decode("Monospaced"));
                
                g.drawString(getText(), x, y + getIconHeight() /*/ 2*/);
                
                g.setFont(oldFont);
                g.setColor(oldColor);
            }
        }

        public int getIconWidth() {
            if (delegateTo != null)
                return delegateTo.getIconWidth();
            
            if (textSizeX == (-1))
                computeTextMetrics();
            
            return textSizeX;
        }

        public int getIconHeight() {
            if (delegateTo != null)
                return delegateTo.getIconHeight();
            
            if (textSizeY == (-1))
                computeTextMetrics();
            
            return textSizeY;
        }
        
        private void computeTextMetrics() {
            Font f = Font.decode("Monospaced");
            
            Rectangle2D bounds = f.getStringBounds(getText(), new FontRenderContext(null, false, false));
            
            textSizeX = (int) bounds.getWidth();
            
            if (textSizeX < bounds.getWidth())
                textSizeX++;
            
            textSizeY = (int) bounds.getHeight();
            
            if (textSizeY < bounds.getHeight())
                textSizeY++;
        }
        
    }

}
