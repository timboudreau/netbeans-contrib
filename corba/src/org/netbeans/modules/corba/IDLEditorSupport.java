/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.*;
import java.lang.reflect.*;
import java.util.Iterator;
import java.text.MessageFormat;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.Timer;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.EditorKit;
import javax.swing.text.BadLocationException;


import org.openide.util.WeakListener;
import org.openide.util.NbBundle;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.RequestProcessor;
import org.openide.text.EditorSupport;
import org.openide.text.PositionRef;
import org.openide.cookies.EditCookie;
import org.openide.cookies.SaveCookie;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.DataObject;
import org.openide.filesystems.FileObject;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.Node;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;

import org.netbeans.modules.corba.settings.CORBASupportSettings;

/** Support for viewing porperties files (EditCookie) by opening them in a text editor */
public class IDLEditorSupport extends EditorSupport implements EditCookie {
    //, Serializable {

    //public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;

    /** Timer which countdowns the auto-reparsing time. */
    javax.swing.Timer timer;

    /** New lines in this file was delimited by '\n' */
    static final byte NEW_LINE_N = 0;

    /** New lines in this file was delimited by '\r' */
    static final byte NEW_LINE_R = 1;

    /** New lines in this file was delimited by '\r\n' */
    static final byte NEW_LINE_RN = 2;

    /** The type of new lines */
    byte newLineType = NEW_LINE_N;

    /** The flag saying if we should listen to the document modifications */
    private boolean listenToEntryModifs = true;

    private Document listenDocument;

    /** Listener to the document changes - entry. The superclass holds a saving manager
    * for the whole dataobject. */
    //private EntrySavingManager entryModifL;

    /** Properties Settings */
    CORBASupportSettings settings;

    static final long serialVersionUID =1787354011149868490L;
    /** Constructor */

    MultiDataObject.Entry idl_file;

    private PositionRef position;

    public IDLEditorSupport(MultiDataObject.Entry entry) {
        super (entry);
        if (DEBUG)
            System.out.println ("IDLEditorSupport(" + entry + ");");
        idl_file = entry;
        //System.out.println("editor support constructor - " + entry.getFile().getName());
        //Thread.dumpStack();
        //initialize();
    }
    /*
      public void initialize() {
      myEntry = (MultiDataObject.Entry)entry;
      super.setModificationListening(false);
      //setMIMEType (IDLDataObject.MIME_PROPERTIES);
      //initTimer();
      
      // listen to myself so I can add a listener for changes when the document is loaded
      addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent evt) {
      if (isDocumentLoaded()) {
      //setListening(true);
      }
      }
      });
      
      //PENDING
      // set actions
      //setActions (new SystemAction [] {
      //  SystemAction.get (CutAction.class),
      //  SystemAction.get (CopyAction.class),
      //  SystemAction.get (PasteAction.class),
      //});
      }
    */

    void setRef(CloneableTopComponent.Ref ref) {
        allEditors = ref;
    }

    //Object writeReplace() throws ObjectStreamException {
    //  return new SerialProxy(myEntry);
    //}
    /*
      public static class SerialProxy implements Serializable {
      
      static final long serialVersionUID =2675098551717845346L;
      public SerialProxy (MultiDataObject.Entry serialEntry) {
      this.serialEntry = serialEntry;
      }
      
      private MultiDataObject.Entry serialEntry;
      
      //Object readResolve() throws ObjectStreamException {
      //System.out.println("deserializing properties editor");
      //System.out.println("serialEntry " + serialEntry);
      //System.out.println("dataobject " + serialEntry.getDataObject());
      //Thread.dumpStack();
      //Object pe = serialEntry.getIDLEditor();
      //System.out.println("deserializing properties editor END");
      //return pe;
      //}
      }
    */

    /** Visible view of underlying file entry */
    //transient MultiDataObject.Entry myEntry;

    /** Focuses existing component to open, or if none exists creates new.
    * @see OpenCookie#open
    */

    public EditorSupport.Editor openAt (PositionRef pos) {
        if (DEBUG)
            System.out.println ("openAt (" + pos + ");");
        return super.openAt (pos);
    }


    public void open () {
        int line = 1;
        int column = 1;
        if (DEBUG)
            System.out.println ("open ();");
        IDLDataObject ido = (IDLDataObject)idl_file.getDataObject ();
        if (ido != null) {
            //position = ido.getPositionRef ();
            line = ido.getLinePosition ();
            column = ido.getColumnPosition () + 1;
        }
        /*
          if (position != null) {
          PositionRef tmp_pos = position;
          position = null;
          openAt (tmp_pos);
          }
        */
        if (line > 1 & column > 1) {
            ido.setLinePosition (1);
            ido.setColumnPosition (1);
            ido.openAtPosition (line, column);
        }
        //else {
        CloneableTopComponent editor = openCloneableTopComponent2();
        editor.requestFocus();
        //y}
    }

    /** Simply open for an editor. */
    protected final CloneableTopComponent openCloneableTopComponent2() {
        if (DEBUG)
            System.out.println("openCloneableTopComponent2()");

        MessageFormat mf = new MessageFormat (NbBundle.getBundle(IDLEditorSupport.class).
                                              getString ("CTL_IDL_OPEN"));

        synchronized (allEditors) {
            try {
                CloneableTopComponent ret = (CloneableTopComponent)allEditors.getAnyComponent ();
                ret.open();
                return ret;
            } catch (java.util.NoSuchElementException ex) {
                // no opened editor
                TopManager.getDefault ().setStatusText
                (mf.format (new Object[] {entry.getFile().getName()}));

                CloneableTopComponent editor = createCloneableTopComponent ();
                allEditors = editor.getReference ();
                editor.open();

                TopManager.getDefault ().setStatusText
                (NbBundle.getBundle(IDLEditorSupport.class).getString ("CTL_IDL_OPENED"));
                return editor;
            }
        }
    }




    /** Launches the timer for autoreparse */
    /*
      private void initTimer() {
      // initialize timer
      timer = new Timer(0, new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
      myEntry.getHandler().autoParse();
      }
      });
      timer.setInitialDelay(settings.getAutoParsingDelay());
      timer.setRepeats(false);
      }              
    */
    /** Returns whether there is an open component (editor or open). */
    /*
      public synchronized boolean hasOpenComponent() {
      return (hasOpenTableComponent() || hasOpenEditorComponent());
      }  
    */
    /*
      private synchronized boolean hasOpenTableComponent() {
      //System.out.println("hasOpenComponent (table) " + myEntry.getFile().getPackageNameExt('/','.') + " " + ((IDLDataObject)myEntry.getDataObject()).getOpenSupport().hasOpenComponent());
      return ((IDLDataObject)myEntry.getDataObject()).getOpenSupport().hasOpenComponent();
      }
    */
    /** Returns whether there is an open editor component. */
    /*
      public synchronized boolean hasOpenEditorComponent() {
      java.util.Enumeration en = allEditors.getComponents ();
      //System.out.println("hasOpenComponent (editor) " + myEntry.getFile().getPackageNameExt('/','.') + " " + en.hasMoreElements ());
      return en.hasMoreElements ();
      }  
    */
    /*
      public void saveThisEntry() throws IOException {
      super.saveDocument();
      myEntry.setModified(false);
      }
    */
    /*
      public boolean close() {
      SaveCookie savec = (SaveCookie) myEntry.getCookie(SaveCookie.class);
      if ((savec != null) && hasOpenTableComponent()) {
      return false;
      }
      //System.out.println("closing");      
      if (!super.close())
      return false;

      //System.out.println("closed - document open = " + isDocumentLoaded());
      
      closeDocumentEntry();                  
      myEntry.getHandler().reparseNowBlocking();  
      return true;  
      }
    */
    /** Clears all data from memory.
     */
    /*  protected void closeDocument () {
        super.closeDocument();
        closeDocumentEntry();
        }
    */

    /** Utility method which enables or disables listening to modifications
    * on asociated document.
    * <P>
    * Could be useful if we have to modify document, but do not want the
    * Save and Save All actions to be enabled/disabled automatically.
    * Initially modifications are listened to.
    * @param listenToModifs whether to listen to modifications
    */
    /*
      public void setModificationListening (final boolean listenToModifs) {
      //System.out.println("set modification listening - " + listenToModifs);
      this.listenToEntryModifs = listenToModifs;
      if (getDocument() == null) return;
      //setListening(listenToEntryModifs);
      }
    */
    /* A method to create a new component. Overridden in subclasses.
     * @return the {@link Editor} for this support
     */
    protected CloneableTopComponent createCloneableTopComponent () {
        // initializes the document if not initialized
        if (DEBUG)
            System.out.println ("createCloneableTopComponent ()");
        prepareDocument ();

        DataObject obj = idl_file.getDataObject ();
        Editor editor = new IDLEditor (obj, this);
        return editor;
    }


    /** Should test whether all data is saved, and if not, prompt the user
    * to save. Called by my topcomponent when it wants to close its last topcomponent, but the table editor may still be open
    *
    * @return <code>true</code> if everything can be closed
    */
    /*
      protected boolean canClose () {
      SaveCookie savec = (SaveCookie) myEntry.getCookie(SaveCookie.class);
      if (savec != null) {                                                           
      // if the table is open, can close without worries, don't remove the save cookie
      if (hasOpenTableComponent())
      return true;
      
      // PENDING - is not thread safe
      MessageFormat format = new MessageFormat(NbBundle.getBundle(IDLEditorSupport.class).
      getString("MSG_SaveFile"));
      String msg = format.format(new Object[] { entry.getFile().getName()});
      NotifyDescriptor nd = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_CANCEL_OPTION);
      Object ret = TopManager.getDefault().notify(nd);
      
      // cancel 
      if (NotifyDescriptor.CANCEL_OPTION.equals(ret))
      return false;
      
      // yes         
      if (NotifyDescriptor.YES_OPTION.equals(ret)) {
      try {
      savec.save();
      }
      catch (IOException e) {
      TopManager.getDefault().notifyException(e);
      return false;
      }
      }
      
      // no      
      if (NotifyDescriptor.NO_OPTION.equals(ret)) {
      return true;  
      }    
      
      }
      return true;
      }
    */

    /** Read the file from the stream, filter the guarded section
    * comments, and mark the sections in the editor.
    *
    * @param doc the document to read into
    * @param stream the open stream to read from
    * @param kit the associated editor kit
    * @throws IOException if there was a problem reading the file
    * @throws BadLocationException should not normally be thrown
    * @see #saveFromKitToStream
    */
    /*
      protected void loadFromStreamToKit (StyledDocument doc, InputStream stream, EditorKit kit) throws IOException, BadLocationException {
      
      NewLineInputStream is = new NewLineInputStream(stream);
      try {
      kit.read(is, doc, 0);
      newLineType = is.getNewLineType();
      }
      finally {
      is.close();
      }
      }
    */

    /** Store the document and add the special comments signifying
    * guarded sections.
    *
    * @param doc the document to write from
    * @param kit the associated editor kit
    * @param stream the open stream to write to
    * @throws IOException if there was a problem writing the file
    * @throws BadLocationException should not normally be thrown
    * @see #loadFromStreamToKit
    */

    /*
      protected void saveFromKitToStream(StyledDocument doc, EditorKit kit, OutputStream stream) throws IOException, BadLocationException {
      //System.out.println("saving - doc = " + doc);
      OutputStream os = new NewLineOutputStream(stream, newLineType);
      try {
      kit.write(os, doc, 0, doc.getLength());
      }
      finally {
      if (os != null) {
      try {
      os.close();
      }
      catch (IOException e) {
      }
      }
      }
      }
    */

    /** Does part of the cleanup - removes a listener.
    */
    /*
      private void closeDocumentEntry () {
      // listen to modifs
      if (listenToEntryModifs) {
      getEntryModifL().clearSaveCookie();
      
      setListening(false);
      }
      }
    */
    /*
      private void setListening(boolean listen) {
      if (listen) {
      if ((getDocument() == null) || (listenDocument == getDocument()))
      return;
      if (listenDocument != null) // also holds that listenDocument != getDocument()
      listenDocument.removeDocumentListener(getEntryModifL());
      listenDocument = getDocument();
      listenDocument.addDocumentListener(getEntryModifL());
      }
      else {
      if (listenDocument != null) {
      listenDocument.removeDocumentListener(getEntryModifL());
      listenDocument = null;
      }
      }
      }
    */
    /** Visible view of the underlying method. */
    /*
      public Editor openAt(PositionRef pos) {
      return super.openAt(pos);
      }
    */                                 
    /** Returns a EditCookie for editing at a given position. */
    /*
      public IDLEditAt getViewerAt(String key) {
      return new IDLEditAt (key);
      }
    */
    /** Class for opening at a given key. */
    /*
      public class IDLEditAt implements EditCookie {
      
      private String key;                          
      
      IDLEditAt(String key) {
      this.key   = key;
      }                
      
      public void setKey(String key) {
      this.key = key;
      }                            
      
      public String getKey() {
      return key;
      }
      
      public void edit() {   
      
      Element.ItemElem item = myEntry.getHandler().getStructure().getItem(key);
      if (item != null) {                   
      PositionRef pos = item.getKeyElem().getBounds().getBegin();
      IDLEditorSupport.this.openAt(pos);
      }
      else {
      
      IDLEditorSupport.this.edit();
      //}          
      }
      
      }
    */
    /*
      private synchronized EntrySavingManager getEntryModifL () {
      if (entryModifL == null) {
      entryModifL = new EntrySavingManager();
      // listens whether to add or remove SaveCookie
      myEntry.addPropertyChangeListener(entryModifL);
      }
      return entryModifL;
      }
      
      String getModifiedAppendix() {
      return modifiedAppendix;
      }                       
    */
    /** Cloneable top component to hold the editor kit.
    */

    public static class IDLEditor extends EditorSupport.Editor {

        protected transient MultiDataObject.Entry entry;

        private transient IDLEditorSupport propSupport;

        private transient PropertyChangeListener saveCookieLNode;
        private transient NodeAdapter nodeL;

        static final long serialVersionUID =-2702087884943509637L;

        public IDLEditor() {
            super();
            if (DEBUG)
                System.out.println("IDLEditor");

        }

        public IDLEditor(DataObject obj, IDLEditorSupport support) {
            super(obj, support);
            if (DEBUG)
                System.out.println("IDLEditor (" + obj + ", " + support + ");");

            this.propSupport = support;
            initMe();
        }

        private void initMe() {
            this.entry = propSupport.idl_file;

            // add to EditorSupport - patch for a bug in deserialization
            propSupport.setRef(getReference());
            /*
            entry.getNodeDelegate().addNodeListener (
            new WeakListener.Node(nodeL = 
            new NodeAdapter () {
            public void propertyChange (PropertyChangeEvent ev) {
            if (ev.getPropertyName ().equals (Node.PROP_DISPLAY_NAME)) {
            updateName();
        }         
        }  
        }
            ));
            */
            Node n = entry.getDataObject ().getNodeDelegate ();
            setActivatedNodes (new Node[] { n });

            //updateName();

            // entry to the set of listeners
            /*
            saveCookieLNode = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
            if (PresentableFileEntry.PROP_COOKIE.equals(evt.getPropertyName()) ||
            PresentableFileEntry.PROP_NAME.equals(evt.getPropertyName())) {
            updateName();
        }
        }
        };
            this.entry.addPropertyChangeListener(
            new WeakListener.PropertyChange(saveCookieLNode));
        }
            */
        }

        /** When closing last view, also close the document.
         * @return <code>true</code> if close succeeded
         */
        /*
          protected boolean closeLast () {
            // instead of super
            if (!propSupport.canClose ()) {
            // if we cannot close the last window
            return false;
            }
                     
            boolean doCloseDoc = !propSupport.hasOpenTableComponent();
            //SaveCookie savec = (SaveCookie) entry.getCookie(SaveCookie.class);
            try {
            if (doCloseDoc) {
            // propSupport.closeDocument (); by reflection
            Method closeDoc = EditorSupport.class.getDeclaredMethod("closeDocument", new Class[0]);
            closeDoc.setAccessible(true);
            closeDoc.invoke(propSupport, new Object[0]);
            }  
            
            //if (propSupport.lastSelected == this) {
            //propSupport.lastSelected = null; by reflection 
            Field lastSel = EditorSupport.class.getDeclaredField("lastSelected");
            lastSel.setAccessible(true);
            if (lastSel.get(propSupport) == this)
            lastSel.set(propSupport, null);
            } 
            catch (Exception e) { 
            if (Boolean.getBoolean("netbeans.debug.exceptions"))
            e.printStackTrace(); 
            }  
            
            // end super
            //boolean canClose = super.closeLast();
            //if (!canClose)
            //return false;
            if (doCloseDoc) {  
            propSupport.closeDocumentEntry();
            entry.getHandler().reparseNowBlocking();  
            }  
            return true;
            }
        */
        /** Updates the name of this top component according to
        * the existence of the save cookie in ascoiated data object
        */
        /*
          protected void updateName () {
          if (entry == null) {
          setName("");
          return;
          }
          else {
          String name = entry.getFile().getName();
          if (entry.getCookie(SaveCookie.class) != null)
          setName(name + propSupport.getModifiedAppendix());
          else
          setName(name);
          }  
          }

          public void writeExternal (ObjectOutput out)
          throws IOException {
          super.writeExternal(out);
          out.writeObject(propSupport);
          }
          
          public void readExternal (ObjectInput in)
          throws IOException, ClassNotFoundException {
          super.readExternal(in);
          propSupport = (IDLEditorSupport)in.readObject();
          initMe();
          }
          
          } // end of IDLEditor inner class
        */
        /** EntrySavingManager manages two tasks concerning saving:<P>
        * 1) It tracks changes in document asociated with ther entry and
        *    sets modification flag appropriately.<P>
        * 2) This class also implements functionality of SaveCookie interface
        */
        /*
          private final class EntrySavingManager implements DocumentListener, SaveCookie, PropertyChangeListener {
          
          public void changedUpdate(DocumentEvent ev) {
          // do nothing - just an attribute
          }

          public void insertUpdate(DocumentEvent ev) {
          modified();
          changeStructureStatus();
          }
          
          public void removeUpdate(DocumentEvent ev) {
          modified();
          changeStructureStatus();
          }
          
          private void changeStructureStatus() {
          int delay = settings.getAutoParsingDelay();
          myEntry.getHandler().setDirty(true);
          if (delay > 0) {
          timer.setInitialDelay(delay);
          timer.restart();
          }
          }

          public void propertyChange(PropertyChangeEvent ev) {
          if ((ev.getSource() == myEntry) &&
          (IDLFileEntry.PROP_MODIFIED.equals(ev.getPropertyName()))) {
          
          if (((Boolean) ev.getNewValue()).booleanValue()) {
          addSaveCookie();
          } else {
          removeSaveCookie();
          }
          }
          }

          public void save () throws IOException {
          // do saving job
          saveThisEntry();
          }
          
          void clearSaveCookie() {
          // remove save cookie (if save was succesfull)
          myEntry.setModified(false);
          }
          
          private void modified () {
          myEntry.setModified(true);
          }
          
          private void addSaveCookie() {
          if (myEntry.getCookie(SaveCookie.class) == null) {
          myEntry.getCookieSet().add(this);
          }
          ((IDLDataObject)myEntry.getDataObject()).updateModificationStatus();
          if (!hasOpenComponent()) {
          RequestProcessor.postRequest(new Runnable() {
          public void run() {
          myEntry.getIDLEditor().open();
          }
          });
          }  
          
          }

          private void removeSaveCookie() {                   
          // remove Save cookie from the data object
          if (myEntry.getCookie(SaveCookie.class) == this) {
          myEntry.getCookieSet().remove(this);
          }
          ((IDLDataObject)myEntry.getDataObject()).updateModificationStatus();
          }
          
          } // end of EntrySavingManager inner class
          
          
          static class NewLineInputStream extends InputStream {

          BufferedInputStream bufis;

          int nextToRead;
          
          int[] newLineTypes;
          
          public NewLineInputStream(InputStream is) throws IOException {
            bufis = new BufferedInputStream(is);
            nextToRead = bufis.read();
            newLineTypes = new int[] { 0, 0, 0 };
          }

          public int read() throws IOException {
            if (nextToRead == -1)
              return -1;
                    
            if (nextToRead == '\r') { 
              nextToRead = bufis.read();
              while (nextToRead == '\r')
                nextToRead = bufis.read();
              if (nextToRead == '\n') {     
                nextToRead = bufis.read();
                newLineTypes[NEW_LINE_RN]++;
                return '\n';
              }
              else {
                newLineTypes[NEW_LINE_R]++;
                return '\n';
              }
            }            
            if (nextToRead == '\n') {
              nextToRead = bufis.read();
              newLineTypes[NEW_LINE_N]++;
              return '\n';
            }
            int oldNextToRead = nextToRead;
            nextToRead = bufis.read();
            return oldNextToRead;
          }  
            
          public byte getNewLineType() {
            if (newLineTypes[0] > newLineTypes[1]) {
              return (newLineTypes[0] > newLineTypes[2]) ? (byte) 0 : 2;
            }
            else {
              return (newLineTypes[1] > newLineTypes[2]) ? (byte) 1 : 2;
            }
          }
    }


        static class NewLineOutputStream extends OutputStream {

          OutputStream stream;
          

          byte newLineType;
          

          public NewLineOutputStream(OutputStream stream, byte newLineType) {
            this.stream = stream;
            this.newLineType = newLineType;
          }


          public void write(int b) throws IOException {
            if (b == '\r')
              return;
            if (b == '\n') {
              switch (newLineType) {
              case NEW_LINE_R:
                stream.write('\r');
                break;
              case NEW_LINE_RN:
                stream.write('\r');
              case NEW_LINE_N:
                stream.write('\n');
                break;
              }
            }
            else {
              stream.write(b);
            }
          }

          public void close() throws IOException {
          stream.flush();
          stream.close();
          }
        */
    }
}

