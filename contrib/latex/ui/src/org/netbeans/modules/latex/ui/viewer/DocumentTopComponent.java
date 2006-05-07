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
package org.netbeans.modules.latex.ui.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.netbeans.modules.latex.model.platform.FilePosition;
import org.openide.ErrorManager;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Lahoda
 */
public class DocumentTopComponent extends TopComponent /*implements KeyListener */{

    private static double sqrt2 = Math.sqrt(2);

    private static int[] resolutions = new int[] {
        (int) (96 / sqrt2 / sqrt2 / sqrt2 / sqrt2),
        (int) (96 / sqrt2 / sqrt2 / sqrt2),
        (int) (96 / sqrt2 / sqrt2),
        (int) (96 / sqrt2),
        (int) (96),
        (int) (96 * sqrt2),
        (int) (96 * sqrt2 * sqrt2),
        (int) (96 * sqrt2 * sqrt2 * sqrt2),
        (int) (96 * sqrt2 * sqrt2 * sqrt2 * sqrt2),
    };

    private DocumentComponent viewer;
    private int resolutionIndex;
    private ViewerImpl viewerImpl;
    private List<DVIPageDescription> desc;
    private FileObject source;
    
    private JComboBox pages;

    public DocumentTopComponent(FileObject source, ViewerImpl viewerImpl) {
        setLayout(new BorderLayout());
        setDisplayName(source.getNameExt());
        resolutionIndex = 4;
	
	this.viewerImpl = viewerImpl;

        JScrollPane spane = new JScrollPane(viewer = new DocumentComponent());

        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);

        JButton zoomInButton = new JButton("+");
        JButton zoomOutButton = new JButton("-");

        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resolutionIndex++; //!!!
                viewer.showPage();
            }
        });

        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resolutionIndex--; //!!!
                viewer.showPage();
            }
        });
        
        pages = new JComboBox();

        pages.setRenderer(new DVIPageDescriptionRenderer());
        pages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DVIPageDescription d = (DVIPageDescription) pages.getSelectedItem();
                
                viewer.setPage(d.getPageNumber());
            }
        });
        toolBar.add(zoomOutButton);
        toolBar.add(zoomInButton);
        toolBar.add(pages);

        add(toolBar, BorderLayout.PAGE_START);
        add(spane, BorderLayout.CENTER);

        viewer.spane = spane;

        toolBar.addKeyListener(viewer);
        addKeyListener(viewer);
	
        setFile(source);
    }
    
    private void setFile(final FileObject source) {
        final FileObject parent = source.getParent();
        final String name = source.getNameExt();
        this.source = source;
        
        final FileChangeListener sourceFileChangeListener = new FileChangeAdapter() {
            public void fileChanged(FileEvent fileEvent) {
                handleDVI(FileUtil.findBrother(source, "dvi"));
                viewer.showPage();
            }
        };
        
        source.addFileChangeListener(sourceFileChangeListener);
        
        parent.addFileChangeListener(new FileChangeAdapter() {
            public void fileDataCreated(FileEvent fileEvent) {
                if (name.equals(fileEvent.getFile().getNameExt())) {
                    parent.removeFileChangeListener(this);
                    source.removeFileChangeListener(sourceFileChangeListener);
                    setFile(fileEvent.getFile());
                    viewer.showPage();
                }
            }
        });
        
        handleDVI(FileUtil.findBrother(source, "dvi"));
    }

    private boolean wasInitialized;

    public void setFilePosition(FilePosition scrollTo) {
        if (scrollTo != null) {
            int scrollToPage = findPageForPosition(scrollTo);
            
            if (scrollToPage != (-1)) {
                viewer.setPage(scrollToPage);
                wasInitialized = true;
            }
        }

        if (!wasInitialized) {
            viewer.setPage(1);
            wasInitialized = true;
        }
    }

    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        return viewer.requestFocusInWindow();
    }

    public void requestFocus() {
        super.requestFocus();
        viewer.requestFocus();
    }

    protected void componentClosed() {
        super.componentClosed();
        viewerImpl.componentClosed(this);
    }

    private void handleDVI(FileObject dvi) {
        if (dvi == null) {
            desc = Collections.emptyList();
            return ;
        }

        try {
            File dviFile = FileUtil.toFile(dvi);
            desc = new DVIParser().parse(dviFile);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            desc = Collections.emptyList();
        }
        
        //update the pages combo:
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        
        for (DVIPageDescription d : desc) {
            model.addElement(d);
        }
        
        pages.setModel(model);
    }

    private int findPageForPosition(FilePosition position) {
        int closestDifference = Integer.MAX_VALUE;
        DVIPageDescription closestPage = null;
        for (DVIPageDescription d : desc) {
            for (FilePosition p : d.getSourcePositions()) {
                if (p.getFile() == position.getFile() && p.getLine() <= position.getLine()) {
                    int dviLine = p.getLine();
                    int givenLine = position.getLine();

                    if (dviLine <= givenLine && closestDifference > (givenLine - dviLine)) {
                        closestDifference = givenLine - dviLine;
                        closestPage = d;
                    }
                }
            }
        }

        if (closestPage != null) {
            return closestPage.getPageNumber();
        } else {
            return -1;
        }
    }

    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }
    
    public Object readResolve() {
        return this;
    }
    
    private static class DVIPageDescriptionRenderer extends DefaultListCellRenderer {
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof DVIPageDescription) {
                return super.getListCellRendererComponent(list, ((DVIPageDescription) value).getPageNumber(), index, isSelected, cellHasFocus);
            }
            
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }

    }

    private class DocumentComponent extends JComponent implements KeyListener, MouseListener, MouseMotionListener {

        private int page;
        private Image currentImage;

        private JScrollPane spane;
        
        /** Creates a new instance of DocumentTopComponent */
        public DocumentComponent() {
            this.page = 1;
            
            setFocusable(true);
            setRequestFocusEnabled(true);
            setVerifyInputWhenFocusTarget(false);
            addKeyListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }
        
        private void setPage(int page) {
            if (page < 1)
                page = 1;
            
            if (desc.size() != 0 && desc.size() < page)
                page = desc.size();
            
            this.page = page;
            
            if (pages.getModel().getSize() >= page)
                pages.setSelectedIndex(page - 1);
            
            showPage();
        }
        
        protected void paintComponent(Graphics g) {
            g.drawImage(currentImage, 0, 0, null);
        }
        
        private void showPage() {
            final Cursor original = getCursor();

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    final Image img = Renderer.getDefault().getImage(source, page, resolutions[resolutionIndex]);
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            currentImage = img;
                            invalidate();
                            repaint();

                            if (getCursor() == Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)) {
                                setCursor(original);
                            }
                        }
                    });
                }
            });
        }
        
        public void keyTyped(KeyEvent e) {
        }
        
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_PAGE_DOWN:
                    setPage(page + 1);
                    break;
                case KeyEvent.VK_PAGE_UP:
                    setPage(page - 1);
                    break;
            }
        }
        
        public void keyReleased(KeyEvent e) {
        }

        public Dimension getPreferredSize() {
            if (currentImage == null)
                return super.getPreferredSize();

            return new Dimension(currentImage.getWidth(null), currentImage.getHeight(null));
        }

        private Cursor beforeDragging;
        private Point dragStart;
        private Rectangle startRectangle;

        public void mouseDragged(MouseEvent e) {
            if (beforeDragging == null) {
                beforeDragging = getCursor();
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                dragStart = e.getPoint();
                startRectangle = spane.getViewport().getViewRect();
            }

            Point p = new Point(dragStart);

            p.translate((int) -e.getPoint().getX(), (int) -e.getPoint().getY());

            startRectangle.translate((int) p.getX(), (int) p.getY());
            
            int xCorrection = startRectangle.getX() < 0 ? (int) -startRectangle.getX() : 0;
            int yCorrection = startRectangle.getY() < 0 ? (int) -startRectangle.getY() : 0;
            
            startRectangle.translate(xCorrection, yCorrection);
            
            dragStart.translate(xCorrection, yCorrection);
            
            scrollRectToVisible(startRectangle);
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1 && e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK && e.getButton() == MouseEvent.BUTTON1) {
                for (DVIPageDescription d : desc) {
                    if (d.getPageNumber() == page) {
                        List<FilePosition> p = d.getSourcePositions();

                        if (!p.isEmpty())
                            open(p.get(0));
                    }
                }
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            if (getCursor() == Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)) {
                setCursor(beforeDragging);
                beforeDragging = null;
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        private void open(FilePosition e) {
            try {
                DataObject d = DataObject.find(e.getFile());
                LineCookie lc = (LineCookie) d.getCookie(LineCookie.class);
                
                lc.getLineSet().getCurrent(e.getLine() - 1).show(Line.SHOW_GOTO);
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }

    }
}
