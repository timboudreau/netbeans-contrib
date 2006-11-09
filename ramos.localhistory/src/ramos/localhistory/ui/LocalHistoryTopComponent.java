/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is Ramon Ramos. The Initial Developer of the Original
 * Software is Ramon Ramos. All rights reserved.
 *
 * Copyright (c) 2006 Ramon Ramos
 */
package ramos.localhistory.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

import org.netbeans.api.diff.Diff;
import org.netbeans.api.diff.DiffView;
import org.netbeans.api.diff.StreamSource;
import org.openide.ErrorManager;
import org.openide.cookies.SaveCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.TreeTableView;
//import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.util.Collection;
import ramos.localhistory.LocalHistoryRepository;
import ramos.localhistory.VersionNode;

public final class LocalHistoryTopComponent extends TopComponent
   implements ExplorerManager.Provider {
  
  private static final String COLOR = "    ";
  /** path to the icon used by the component and its open action */
  static final String ICON_PATH = "ramos/localhistory/resources/clock.png";
  private static final String OLD = "old";
  private static final String ANNOTATION = "Annotation";
  private static final String PREV = "Prev";
  private static final String NEXT = "Next";
  private static final String PREFERRED_ID = "LocalHistoryTopComponent";
  private static final String CURRENT_VERSION_TITLE = "Current Version";
  private static final String X = "x";
  final static String PATH = "path";
  final static String LOCAL_HISTORY = "local history";
  final static String NEW = "new";
  //private FileObject[] diffFiles;
  private int max;
  private final RevertAction revertAction = new RevertAction();
  private final RefreshHistory refreshAction = new RefreshHistory();
  
  private VersionNode reverter = null;
  private static LocalHistoryTopComponent instance;
  private final ExplorerManager manager = new ExplorerManager();
  private Lookup lookup;
  //   private final BeanTreeView view = new BeanTreeView();
  private final TreeTableView view = new TreeTableView();
  //private JPanel diffContainer = new JPanel();
  private File currentFile = null;
  private Component oldDiff = null;
  private PrevNextDiffListener diffListener = new PrevNextDiffListener();
  private JSplitPane historyDiffSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
  private JPanel dummyRightComponent = new JPanel();
  
  //   private static final String DIFF_VIEW = "Diff View";
  //   private static final String HISTORY_VIEW = "History View";
  //   private static final Border DIFF_BORDER = BorderFactory.createCompoundBorder(
  //       BorderFactory.createTitledBorder(DIFF_VIEW),
  //       BorderFactory.createLineBorder(Color.gray));
  //   private static final Border HISTORY_BORDER = BorderFactory.createCompoundBorder(
  //       BorderFactory.createTitledBorder(HISTORY_VIEW),
  //       BorderFactory.createLineBorder(Color.gray));
  //toolbar
  JPanel diffContainer = new JPanel(new BorderLayout());
  private JLabel diffLabel = new JLabel(" 0 difference(s)");
  //private JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
  private Box toolPanel = Box.createHorizontalBox();
  private JButton prev = new JButton(new ImageIcon(
     Utilities.loadImage("ramos/localhistory/resources/diff-prev.png")));
  private JButton next = new JButton(new ImageIcon(
     Utilities.loadImage("ramos/localhistory/resources/diff-next.png")));
  private List<FileObject> oldies = new ArrayList<FileObject>();
  public  LocalHistoryTopComponent() {
    //initComponents();
    //toolbar
    //make color labels
    // <editor-fold defaultstate="collapsed" desc="layouting">
    Border border = BorderFactory.createEtchedBorder();
    JLabel deleteColor = new JLabel(COLOR);
    deleteColor.setBorder(border);
    deleteColor.setBackground(Color.decode("#FFA0B4"));
    deleteColor.setOpaque(true);
    JLabel addColor = new JLabel(COLOR);
    addColor.setBorder(border);
    addColor.setBackground(Color.decode("#B4FFB4"));
    addColor.setOpaque(true);
    JLabel changedColor = new JLabel(COLOR);
    changedColor.setBackground(Color.decode("#A0C8FF"));
    changedColor.setOpaque(true);
    changedColor.setBorder(border);
    JLabel delete = new JLabel("  removed   ");
    JLabel added = new JLabel("  added    ");
    JLabel changed = new JLabel("  changed   ");
    //toolPanel.setBorder(null);
    //      JPanel revertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    //      revertPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    //      revertPanel.add(new JButton(revertAction));
    //      revertPanel.add(Box.createGlue());
    toolPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    toolPanel.add(new JButton(revertAction));
    toolPanel.add(Box.createGlue());
    toolPanel.add(new JButton(refreshAction));
    toolPanel.add(Box.createGlue());
    toolPanel.add(deleteColor);
    toolPanel.add(delete);
    toolPanel.add(changedColor);
    toolPanel.add(changed);
    toolPanel.add(addColor);
    toolPanel.add(added);
    
    toolPanel.add(diffLabel);
    toolPanel.add(prev);
    toolPanel.add(Box.createHorizontalStrut(2));
    toolPanel.add(next);
    prev.setActionCommand(PREV);
    next.setActionCommand(NEXT);
    prev.addActionListener(diffListener);
    next.addActionListener(diffListener);
    next.setMargin(new java.awt.Insets(2,3,2,3));
    prev.setMargin(new java.awt.Insets(2,3,2,3));
    //prev.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    //prev.setHorizontalAlignment(JButton.LEADING); // optional
    //prev.setBorderPainted(false);
    prev.setContentAreaFilled(false);
    next.setContentAreaFilled(false);
    //      next.setBorder(null);
    //      prev.setBorder(null);
    //diffContainer.setBorder(DIFF_BORDER);
    diffContainer.add(dummyRightComponent,BorderLayout.CENTER);
    diffContainer.add(toolPanel,BorderLayout.NORTH);
    // </editor-fold>
    
    
    this.add(historyDiffSplit);
    //view.setBorder(HISTORY_BORDER);
    setName(NbBundle.getMessage(LocalHistoryTopComponent.class,
       "CTL_LocalHistoryTopComponent"));
    setToolTipText(NbBundle.getMessage(LocalHistoryTopComponent.class,
       "HINT_LocalHistoryTopComponent"));
    ActionMap map = getActionMap();
    map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
    map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
    map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
    map.put("delete", ExplorerUtils.actionDelete(manager, true)); // or false
    lookup = ExplorerUtils.createLookup(manager, map);
    manager.addVetoableChangeListener(new ShowDiffAtSelection());
    setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));//could not remove this
    view.setProperties(new Property[]{new AnnotationPropertyTemplate()});
    view.setRootVisible(false);
    historyDiffSplit.setLeftComponent(view);
    historyDiffSplit.setRightComponent(diffContainer);
    setIcon(Utilities.loadImage(ICON_PATH));
  }
  
  public Lookup getLookup() {
    return lookup;
  }
  
  public void addNotify() {
    super.addNotify();
    //ExplorerUtils.activateActions(manager, true);
  }
  
  public void removeNotify() {
    //ExplorerUtils.activateActions(manager, false);
    super.removeNotify();
  }
  
  
  public static synchronized LocalHistoryTopComponent getDefault() {
    if(instance == null)
      instance = new LocalHistoryTopComponent();
    return instance;
  }
  
  public static synchronized LocalHistoryTopComponent findInstance() {
    TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
    if(win == null) {
      ErrorManager.getDefault().log(ErrorManager.WARNING,
         "Cannot find LocalHistory component. It will not be located properly" +
         " in the window system.");
      return getDefault();
    }
    if(win instanceof LocalHistoryTopComponent) {
      return (LocalHistoryTopComponent)win;
    } else {
      ErrorManager.getDefault().log(ErrorManager.WARNING,
         "There seem to be multiple components with the " +
         "'LocalHistoryTopComponent' ID. That is a potential source of errors" +
         " and unexpected behavior.");
      return getDefault();
    }
  }
  
  public int getPersistenceType() {
    return TopComponent.PERSISTENCE_NEVER;
  }
  
  public void componentOpened() {
  }
  
  public void componentClosed() {
  }
  
  public Object writeReplace() {
    return new ResolvableHelper();
  }
  
  protected String preferredID() {
    return PREFERRED_ID;
  }
  public ExplorerManager getExplorerManager() {
    return manager;
  }
  
  
  private static String getMimeType(FileObject fo){
    if (fo.getExt().equalsIgnoreCase("properties")) return "text/x-properties";
    String mimeType = fo.getMIMEType();
    try {
      DataObject dobj = DataObject.find(fo);
      CloneableEditorSupport ces =
         (CloneableEditorSupport) dobj.getNodeDelegate().getLookup()
         .lookup(CloneableEditorSupport.class);
      if (ces != null){
        Document doc = ces.getDocument();
        if (doc != null){
          String mimeTypeProp = (String)doc.getProperty("mimeType");
          if (mimeTypeProp != null) mimeType = mimeTypeProp;
        }
      }
    } catch (DataObjectNotFoundException ex) {
      ex.printStackTrace();
    }
    return mimeType;
  }
  
  public void setFileForHistory(final File file,
     final Collection<VersionNode> versionNodesCollection) {
    //save
    try {
      save(file);
      //diffFiles = null;
      reverter = null;
      updateRevertEnable();
      currentFile = file;
      //explorer view
      setUpExplorerView(versionNodesCollection);
      //diff view
      FileObject fo = FileUtil.toFileObject(currentFile);
      String mimeType = getMimeType(fo);
      StreamSource stream1 = StreamSource.createSource(OLD,
         CURRENT_VERSION_TITLE,
         mimeType,currentFile);
      StreamSource stream2 = StreamSource.createSource(NEW,
         CURRENT_VERSION_TITLE,
         mimeType,currentFile);
      setUpDiffView(stream1, stream2, 140);
      
      deleteOld();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
  
  private void setUpExplorerView(
     final Collection<VersionNode> versionNodesCollection) {
    
    Node versionNodesArray[] = getAsNodeArray(versionNodesCollection);
    Children versionNodesChildren = new Children.Array();
    versionNodesChildren.add(versionNodesArray);
    FilterNode filterRoot = null;
    try {
      filterRoot = new FilterNode(new MyFileVersionRoot(), versionNodesChildren);
    } catch (IntrospectionException ex) {
      ex.printStackTrace();
    }
    manager.setRootContext(filterRoot);
  }
  
  private void save(final File file) throws IOException,
     DataObjectNotFoundException {
    SaveCookie sc = (SaveCookie) DataObject.find(FileUtil.toFileObject(file)).
       getCookie(SaveCookie.class);
    if (sc != null) sc.save();
  }
  
  
  private Node[] getAsNodeArray(
     final Collection<VersionNode> versionNodesCollection) {
    //could cut here and delete "old" files in a thread
    //    VersionNode[] ret = null;
    VersionNode array[] = (VersionNode[])versionNodesCollection
       .toArray(new VersionNode[versionNodesCollection.size()]);
    //    if (array.length <= max){
    //
    //      ret =  array;
    //    }else{
    //      ret = new VersionNode[max];
    //      System.arraycopy(array, 0, ret, 0, max);
    //    }
    return array;
  }
  private void setUpDiffView(final StreamSource stream1,
     final StreamSource stream2, final int dividerLocation) throws IOException {
    DiffView diff = Diff.getDefault().createDiff(stream1, stream2);
    diffListener.setDiffView(diff);
    
    final Component diffComp = diff.getComponent();
    if (oldDiff != null){
      diffContainer.remove(oldDiff);
    }
    diffContainer.add(diffComp, BorderLayout.CENTER);
    diffLabel.setText(diff.getDifferenceCount() + " difference(s)  ");
    oldDiff = diffComp;
    LocalHistoryTopComponent.this.revalidate();
    historyDiffSplit.setDividerLocation(dividerLocation);
  }
  
  public void reloadHistory() {
    Collection<VersionNode> versionNodesCollection =
       LocalHistoryRepository.getInstance().fillRevisionsList(currentFile);
    setFileForHistory(currentFile, versionNodesCollection);
  }
  
  
  
  private void updateRevertEnable() {
    revertAction.setEnabled(reverter!=null);
  }
  
  
  
  private void deleteOld() {
    //delete fos in oldies list
    //throw new UnsupportedOperationException("Not yet implemented");
  }
  
  //************** inner classes ************************//
  
  static class MyFileVersionRoot extends BeanNode{
    public MyFileVersionRoot() throws IntrospectionException{
      super(X);
    }
    public String getName() {
      return "Version";
    }
  }
  
  
  private static class PrevNextDiffListener implements ActionListener{
    private DiffView diffView;
    
    public void actionPerformed(ActionEvent e){
      if (diffView == null || diffView.getDifferenceCount() == 0) return ;
      if (e.getActionCommand().equals(PREV)){
        int cur = diffView.getCurrentDifference();
        if (cur > 0) diffView.setCurrentDifference(cur-1);
        else if (cur == 0){
          diffView.setCurrentDifference(diffView.getDifferenceCount()-1);
        }
      }else if (e.getActionCommand().equals(NEXT)){
        int cur = diffView.getCurrentDifference();
        if (cur < diffView.getDifferenceCount()-1) {
          diffView.setCurrentDifference(cur+1);
        } else diffView.setCurrentDifference(0);
      }
    }
    
    public DiffView getDiffView() {
      return diffView;
    }
    
    public void setDiffView(DiffView diffView) {
      this.diffView = diffView;
    }
    
    
    
  }
  
  private class ShowDiffAtSelection implements VetoableChangeListener {
    
    /**
     * call when nodes in the history list are selected. triggers display of diff.
     * @param evt
     */
    public void vetoableChange(final PropertyChangeEvent evt)
       throws PropertyVetoException {
      if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
        //reset (was here!)
        Node[] selNodes = (Node[]) evt.getNewValue();
        Node selectedNode1 = selNodes[0];
        if ((selectedNode1 instanceof VersionNode)){//MyFileVersionRoot
          showDiff(selNodes);
        }
      }
    }
    
    private void showDiff(final Node[] selNodes) {
      reverter = null;
      final FileObject currentFileObject = FileUtil.toFileObject(currentFile);
      final String mime = getMimeType(currentFileObject);
      StreamSource stream1, stream2;
      VersionNode eins = (VersionNode)selNodes[0];
      try {
        stream1 = StreamSource.createSource(OLD, eins.getName(),
           mime, eins.getReader());
        if (selNodes.length == 2) {
          VersionNode selectedNode2 = (VersionNode) selNodes[1];
          stream2 = StreamSource.createSource(NEW, selectedNode2.getName(),
             mime, selectedNode2.getReader());
        } else {
          stream2 = StreamSource.createSource(NEW, CURRENT_VERSION_TITLE,
             mime, new FileReader(currentFile));
          //can revert
          reverter = eins;
        }
        updateRevertEnable();
        int dividerLocation = historyDiffSplit.getDividerLocation();
        setUpDiffView(stream1, stream2, dividerLocation);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    
    
  }
  
  
  private static class AnnotationPropertyTemplate extends Property{
    AnnotationPropertyTemplate(){
      super(String.class);
    }
    public boolean canRead() {
      return true;
    }
    
    public Object getValue() throws IllegalAccessException,
       InvocationTargetException {
      return "<template>";
    }
    
    public boolean canWrite() {
      return true;
    }
    
    public void setValue(Object object) throws IllegalAccessException,
       IllegalArgumentException, InvocationTargetException {
    }
    
    public String getName() {
      return ANNOTATION;
    }
    
    
  }
  
  private class RevertAction extends AbstractAction{
    public RevertAction(){
      setEnabled(false);
    }
    public void actionPerformed(ActionEvent e) {
      reverter.revert(FileUtil.toFileObject(currentFile));
      reloadHistory();
    }
    
    public Object getValue(String key) {
      if (key.equals(AbstractAction.NAME)) return "Revert";
      //else if (key.equals(AbstractAction.SMALL_ICON))
      //return new ImageIcon("ramos/localhistory/resources/clock.png");
      else return super.getValue(key);
    }
    
    
    
  }
  
  private final class RefreshHistory extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
      reloadHistory();
    }
    public Object getValue(String key) {
      if (key.equals(AbstractAction.NAME)) return "Refresh History";
      //else if (key.equals(AbstractAction.SMALL_ICON))
      //return new ImageIcon("ramos/localhistory/resources/clock.png");
      else return super.getValue(key);
    }
  }
  
  
  static final class ResolvableHelper
     implements Serializable {
    
    public Object readResolve() {
      return LocalHistoryTopComponent.getDefault();
    }
    
    private static final long serialVersionUID = 1L;
    
    ResolvableHelper() {
    }
  }
}


