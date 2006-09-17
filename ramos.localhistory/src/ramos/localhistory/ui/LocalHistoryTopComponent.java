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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.TreeSet;
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
import org.openide.filesystems.FileLock;
//import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
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
import ramos.localhistory.Listener;

public final class LocalHistoryTopComponent extends TopComponent
   implements ExplorerManager.Provider {
  static final class ResolvableHelper
     implements Serializable {
    
    public Object readResolve() {
      return LocalHistoryTopComponent.getDefault();
    }
    
    private static final long serialVersionUID = 1L;
    
    ResolvableHelper() {
    }
  }
  private static final String COLOR = "    ";
  /** path to the icon used by the component and its open action */
  static final String ICON_PATH = "ramos/localhistory/resources/clock.png";
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
    
    
    this.add(split);
    //view.setBorder(HISTORY_BORDER);
    setName(NbBundle.getMessage(LocalHistoryTopComponent.class, "CTL_LocalHistoryTopComponent"));
    setToolTipText(NbBundle.getMessage(LocalHistoryTopComponent.class, "HINT_LocalHistoryTopComponent"));
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
    split.setLeftComponent(view);
    split.setRightComponent(diffContainer);
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
      ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find LocalHistory component. It will not be located properly in the window system.");
      return getDefault();
    }
    if(win instanceof LocalHistoryTopComponent) {
      return (LocalHistoryTopComponent)win;
    } else {
      ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the 'LocalHistoryTopComponent' ID. That is a potential source of errors and unexpected behavior.");
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
  public Collection<VersionNode> fillNodeList(File file) {
    FileObject theDir = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("local history");
    FileObject files[] = theDir.getChildren();
    
    String path = file.getAbsolutePath();
    TreeSet<VersionNode> listFN = new TreeSet<VersionNode>(COMPARATOR);
    FileObject arr$[] = files;
    int len$ = arr$.length;
    for(int i$ = 0; i$ < len$; i$++) {
      final FileObject fo = arr$[i$];
      if(fo.isFolder() || fo.getAttribute(PATH) == null || !fo.getAttribute(PATH).equals(path)) continue;
      try {
        if (!old(fo)) listFN.add(new VersionNode(fo));
      } catch(DataObjectNotFoundException ex) {
        ex.printStackTrace();
      }
      
    }
    //look if size > max.rev.count
    //if so
    //for(int i = 0; i < delta; I++){
    //VersionNode n = listFN.last();
    //listFN.remove(n);
    //n.getoriginal().delete;
    //n = null;
    //}
    return listFN;
  }
  final static String NEW = "new";
  private static String getMimeType(FileObject fo){
    String mimeType = fo.getMIMEType();
    try {
      DataObject dobj = DataObject.find(fo);
      CloneableEditorSupport ces = (CloneableEditorSupport) dobj.getNodeDelegate().getLookup().lookup(CloneableEditorSupport.class);
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
  
  public void setFile(final File file, final Collection<VersionNode> collection) {
    try {
      SaveCookie sc = (SaveCookie) DataObject.find(FileUtil.toFileObject(file)).
         getCookie(SaveCookie.class);
      if (sc != null) sc.save();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    diffFiles = null;
    updateRevertEnable();
    diffLabel.setText(" 0 difference(s)  ");
    currentFile = file;
    //String path = file.getAbsolutePath();
    FileObject theDir = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("local history");
    FileObject files[] = theDir.getChildren();
    
    
    //      TreeSet<FilterNode> listFN = new TreeSet<FilterNode>(COMPARATOR);
    //      FileObject arr$[] = files;
    //      int len$ = arr$.length;
    //      for(int i$ = 0; i$ < len$; i$++) {
    //         final FileObject fo = arr$[i$];
    //         if(fo.isFolder() || fo.getAttribute("path") == null || !fo.getAttribute("path").equals(path)) continue;
    //         try {
    //            listFN.add(new VersionNode(fo));
    //         } catch(DataObjectNotFoundException ex) {
    //            ex.printStackTrace();
    //         }
    //
    //      }
    //      if (listFN.size() == 0){ Toolkit.getDefaultToolkit().beep(); return;}
    
    Node array[] = (Node[])collection.toArray(new Node[collection.size()]);
    Children children = new Children.Array();
    children.add(array);
    FilterNode filterRoot = null;
    try {
      filterRoot = new FilterNode(new MyFileVersionRoot(), children);
    } catch (IntrospectionException ex) {
      ex.printStackTrace();
    }
    manager.setRootContext(filterRoot);
    FileObject fo = FileUtil.toFileObject(currentFile);
    String mimeType = getMimeType(fo);
    //String title = "Current Version";
    StreamSource stream1 = StreamSource.createSource(OLD,
       CURRENT_VERSION_TITLE,
       mimeType,currentFile);
    StreamSource stream2 = StreamSource.createSource(NEW,           CURRENT_VERSION_TITLE,
       mimeType,currentFile);
    DiffView diff = null;
    try {
      diff = Diff.getDefault().createDiff(stream1,stream2);
      diffListener.setDiffView(diff);
      
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    Component diffComp = diff.getComponent();//NPE?
    
    if (oldDiff != null) diffContainer.remove(oldDiff);
    diffContainer.add(diffComp,BorderLayout.CENTER);
    oldDiff = diffComp;
    //split.setRightComponent(diffComp);
    
    LocalHistoryTopComponent.this.revalidate();
    split.setDividerLocation(140);
    //      DiffProvider dp = Lookup.getDefault().lookup(DiffProvider.class);
    //      Difference[] diffs = dp.computeDiff(null,null);
    //      diffs[0].getFirstLineDiffs()[0].
  }
  final static String PATH = "path";
  final static String LOCAL_HISTORY = "local history";
  public void setFile(final File file) {
    try {
      SaveCookie sc = (SaveCookie) DataObject.find(FileUtil.toFileObject(file)).
         getCookie(SaveCookie.class);
      if (sc != null) sc.save();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    diffFiles = null;
    updateRevertEnable();
    diffLabel.setText(" 0 difference(s)  ");
    currentFile = file;
    String path = file.getAbsolutePath();
    FileObject theDir = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(LOCAL_HISTORY);
    FileObject files[] = theDir.getChildren();
    
    
    TreeSet<VersionNode> listFN = new TreeSet<VersionNode>(COMPARATOR);
    FileObject arr$[] = files;
    int len$ = arr$.length;
    for(int i$ = 0; i$ < len$; i$++) {
      final FileObject fo = arr$[i$];
      if(fo.isFolder() || fo.getAttribute(PATH) == null || !fo.getAttribute(PATH).equals(path)) continue;
      try {
        listFN.add(new VersionNode(fo));
      } catch(DataObjectNotFoundException ex) {
        ex.printStackTrace();
      }
      
    }
    
    Node array[] = (Node[])listFN.toArray(new Node[listFN.size()]);
    Children children = new Children.Array();
    children.add(array);
    FilterNode filterRoot = null;
    try {
      filterRoot = new FilterNode(new MyFileVersionRoot(), children);
    } catch (IntrospectionException ex) {
      ex.printStackTrace();
    }
    manager.setRootContext(filterRoot);
     FileObject fo = FileUtil.toFileObject(currentFile);
    String mimeType = getMimeType(fo);
    //String mimeType = FileUtil.toFileObject(currentFile).getMIMEType();
    //String title = "Current Version";
    StreamSource stream1 = StreamSource.createSource(OLD,
       CURRENT_VERSION_TITLE,
       mimeType,currentFile);
    StreamSource stream2 = StreamSource.createSource(NEW,
       CURRENT_VERSION_TITLE,
       mimeType,currentFile);
    DiffView diff = null;
    try {
      diff = Diff.getDefault().createDiff(stream1,stream2);
      diffListener.setDiffView(diff);
      
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    Component diffComp = diff.getComponent();//NPE?
    
    if (oldDiff != null) diffContainer.remove(oldDiff);
    diffContainer.add(diffComp,BorderLayout.CENTER);
    oldDiff = diffComp;
    //split.setRightComponent(diffComp);
    
    LocalHistoryTopComponent.this.revalidate();
    split.setDividerLocation(140);
    //      DiffProvider dp = Lookup.getDefault().lookup(DiffProvider.class);
    //      Difference[] diffs = dp.computeDiff(null,null);
    //      diffs[0].getFirstLineDiffs()[0].
  }
  
  public ExplorerManager getExplorerManager() {
    return manager;
  }
  static class MyComparator implements Comparator<VersionNode> {
    public int compare(VersionNode fn1, VersionNode fn2) {
      DataObject do1 = (DataObject) fn1.getLookup().lookup(DataObject.class);
      DataObject do2 = (DataObject) fn2.getLookup().lookup(DataObject.class);
      FileObject fo1 = do1.getPrimaryFile();
      FileObject fo2 = do2.getPrimaryFile();
      return fo1.lastModified().compareTo(fo2.lastModified());
      
    }
    
    public boolean equals(Object obj) {
      return false;
    }
    
    
  }
  static class MyFileVersionRoot extends BeanNode{
    public MyFileVersionRoot() throws IntrospectionException{
      super(X);
    }
    public String getName() {
      return "Version";
    }
  }
  
  private static LocalHistoryTopComponent instance;
  private final ExplorerManager manager = new ExplorerManager();
  private Lookup lookup;
  //   private final BeanTreeView view = new BeanTreeView();
  private final TreeTableView view = new TreeTableView();
  //private JPanel diffContainer = new JPanel();
  private static final String PREFERRED_ID = "LocalHistoryTopComponent";
  private File currentFile = null;
  private JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
  private JPanel dummyRightComponent = new JPanel();
  private static final Comparator<VersionNode> COMPARATOR = new MyComparator();
  private static final String CURRENT_VERSION_TITLE = "Current Version";
  //   private static final String DIFF_VIEW = "Diff View";
  //   private static final String HISTORY_VIEW = "History View";
  //   private static final Border DIFF_BORDER = BorderFactory.createCompoundBorder(
  //       BorderFactory.createTitledBorder(DIFF_VIEW),
  //       BorderFactory.createLineBorder(Color.gray));
  //   private static final Border HISTORY_BORDER = BorderFactory.createCompoundBorder(
  //       BorderFactory.createTitledBorder(HISTORY_VIEW),
  //       BorderFactory.createLineBorder(Color.gray));
  private static final String X = "x";
  //toolbar
  JPanel diffContainer = new JPanel(new BorderLayout());
  private JLabel diffLabel = new JLabel(" 0 difference(s)");
  //private JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
  private Box toolPanel = Box.createHorizontalBox();
  private JButton prev = new JButton(new ImageIcon(
     Utilities.loadImage("ramos/localhistory/resources/diff-prev.png")));
  
  
  
  private JButton next = new JButton(new ImageIcon(
     Utilities.loadImage("ramos/localhistory/resources/diff-next.png")));
  
  
  
  
  private Component oldDiff = null;
  private DiffListener diffListener = new DiffListener();
  private static final String ANNOTATION = "Annotation";
  private static final String PREV = "Prev";
  private static final String NEXT = "Next";
  
  private static class DiffListener implements ActionListener{
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
        if (cur < diffView.getDifferenceCount()-1) diffView.setCurrentDifference(cur+1);
        else diffView.setCurrentDifference(0);
      }
      
      
    }
    
    public DiffView getDiffView() {
      return diffView;
    }
    
    public void setDiffView(DiffView diffView) {
      this.diffView = diffView;
    }
    
    
    
  }
  private static final String OLD = "old";
  private class ShowDiffAtSelection implements VetoableChangeListener {
    
    public void vetoableChange(final PropertyChangeEvent evt) throws PropertyVetoException {
      if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
        //reset
        diffFiles = null;
        final Node[] selNodes = (Node[]) evt.getNewValue();
        final FileObject currentFileObject = FileUtil.toFileObject(currentFile);
        final String mime = currentFileObject.getMIMEType();
        Node selectedNode1 = selNodes[0];
        //System.out.println("selectedNode1 "+selectedNode1);
        DataObject dataObject = (DataObject) selectedNode1.getLookup().lookup(DataObject.class);
        if (dataObject == null){
          //context root node is selected.
          return;
        }
        final FileObject olderFileObject = dataObject.getPrimaryFile();
        File olderFile = FileUtil.toFile(olderFileObject);
        StreamSource stream1 = null;
        try {
          stream1 = StreamSource.createSource(OLD, selectedNode1.getName(), mime, new FileReader(olderFile));
        } catch (FileNotFoundException ex) {
          ex.printStackTrace();
        }
        StreamSource stream2 = null;
        if (selNodes.length == 2) {
          Node selectedNode2 = selNodes[1];
          DataObject dataObject2 = (DataObject) selectedNode2.getLookup().lookup(DataObject.class);
          FileObject olderFileObject2 = dataObject2.getPrimaryFile();
          File olderFile2 = FileUtil.toFile(olderFileObject2);
          try {
            stream2 = StreamSource.createSource(NEW, selectedNode2.getName(), mime, new FileReader(olderFile2));
          } catch (FileNotFoundException ex) {
            ex.printStackTrace();
          }
        } else {
          try {
            stream2 = StreamSource.createSource(NEW, CURRENT_VERSION_TITLE, mime, new FileReader(currentFile));
          } catch (FileNotFoundException ex) {
            ex.printStackTrace();
          }
          //can revert
          diffFiles = new FileObject[]{olderFileObject,currentFileObject};
        }
        updateRevertEnable();
        DiffView diff = null;
        try {
          diff = Diff.getDefault().createDiff(stream1, stream2);
          diffListener.setDiffView(diff);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
        final Component diffComp = diff.getComponent();
        int loc = split.getDividerLocation();
        if (oldDiff != null)
          diffContainer.remove(oldDiff);
        diffContainer.add(diffComp, BorderLayout.CENTER);
        diffLabel.setText(diff.getDifferenceCount() + " difference(s)  ");
        oldDiff = diffComp;
        LocalHistoryTopComponent.this.revalidate();
        split.setDividerLocation(loc);
      }
    }
    
    
  }
  private void updateRevertEnable() {
    revertAction.setEnabled(diffFiles!=null);
  }
  
  private boolean old(FileObject fo) {
    return false;
  }
  private static class AnnotationPropertyTemplate extends Property{
    AnnotationPropertyTemplate(){
      super(String.class);
    }
    public boolean canRead() {
      return true;
    }
    
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
      return null;
    }
    
    public boolean canWrite() {
      return true;
    }
    
    public void setValue(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
      try {
        
        FileLock lock = diffFiles[1].lock();
        InputStream is = diffFiles[0].getInputStream();
        OutputStream os = diffFiles[1].getOutputStream(lock);
        //DataObject  dataObject = DataObject.find(diffFiles[1]);
        //dataObject.setModified(false);
        //dataObject.setModified(true);
        FileUtil.copy(is, os);
        is.close();
        os.close();
        lock.releaseLock();
        Listener.getInstance().makeLocalHistoryCopy(
           DataObject.find(diffFiles[1]),
           "reverted from "+diffFiles[0].lastModified().toString());
        //dataObject.setModified(false);
        
      } catch (FileNotFoundException ex) {
        ex.printStackTrace();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      setFile(currentFile);
    }
    
    public Object getValue(String key) {
      if (key.equals(AbstractAction.NAME)) return "Revert";
      //else if (key.equals(AbstractAction.SMALL_ICON)) return new ImageIcon("ramos/localhistory/resources/clock.png");
      else return super.getValue(key);
    }
    
    
    
  }
  private FileObject[] diffFiles;
  private final RevertAction revertAction = new RevertAction();
  private final RefreshHistory refreshAction = new RefreshHistory();
  private final class RefreshHistory extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
      setFile(currentFile);
    }
    public Object getValue(String key) {
      if (key.equals(AbstractAction.NAME)) return "Refresh History";
      //else if (key.equals(AbstractAction.SMALL_ICON)) return new ImageIcon("ramos/localhistory/resources/clock.png");
      else return super.getValue(key);
    }
  }
  
  
}


