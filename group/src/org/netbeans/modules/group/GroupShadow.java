/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.group;


import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.event.ChangeListener;

import org.openide.cookies.CompilerCookie;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.nodes.*;
import org.openide.TopManager;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;
import org.openide.WizardDescriptor;


/** Group shadow.
 *  <code>DataObject</code> representing a group of DataDbjects on a filesystem.
 *  It also defines rules for templating of group members:
 *    <p>property templateALL if true then the group is result
 *                            otherwise bunch of group members
 *    <p>property templatePattern defines MessageFormat where
 *    <p> {0} member name
 *    <p> {1} name entered by user (during template instantiation)
 *    <p> {2} posfix got from {0} by using part following last "__"
 *         e.g. for "hello__World" is postfix "World"
 *              for "helloWorld" is postfix ""
 *    <p> {3} backward substitution result (i.e. __somethingBetween__ => {1} )
 *
 * @author Martin Ryzl
 * @see org.openide.loaders.DataObject
 */
public class GroupShadow extends DataObject {

    /** Constants. */
    public static final String GS_EXTENSION = GroupShadowLoader.GS_EXTENSION;

    /** Children for Group Shadow. */
    private GroupChildren children;

    /** If true, GroupShadow will show targets for all links. */
    private static boolean showLinks = true;

    /** Name of the Show Links Property. */
    public static final String PROP_SHOW_LINKS = "showlinks"; // NOI18N

    /** Name of the Template All property. */
    public static final String PROP_TEMPLATE_ALL = "templateall"; // NOI18N

    /** Name of the Template Pattern property. */
    public static final String PROP_TEMPLATE_PATTERN = "templatepattern"; // NOI18N

    /** Name of the use template pattern property. */
    public static final String PROP_USE_PATTERN = "usepattern"; // NOI18N
    
    /** Icon resource string for GroupShadow node. */
    static final String GS_ICON_BASE = "org/netbeans/modules/group/resources/groupShadow"; // NOI18N

    /** Format for display name. */
    private static MessageFormat groupformat;

    /** Formats for target names (valid, invalid, invalid). */
    private static MessageFormat vformat, vformat2, iformat, iformat2;

    /** Anti-loop detection. */
    private GroupShadow gsprocessed = null;

    /** Generated serial version UID. */
    static final long serialVersionUID =-5086491126656157958L;

    // http://www.netbeans.org/issues/show_bug.cgi?id=23350
    private static TemplateWizard.Iterator groupTemplateIterator = null;
    private static synchronized TemplateWizard.Iterator getGroupTemplateIterator() {
        if (groupTemplateIterator == null) {
            groupTemplateIterator = new GroupTemplateIterator();
        }
        return groupTemplateIterator;
    }

    
    /** Constructs group shadow data object. */
    public GroupShadow(final FileObject fo, DataLoader dl)
    throws DataObjectExistsException, IllegalArgumentException, IOException {
        super(fo, dl);
    }

    
    /** Creates a node for <code>GroupShadow</code> and registers it for listening.
     * @return node */
    protected Node createNodeDelegate() {
        children = new GroupChildren();
        GroupShadowNode node = new GroupShadowNode(this, children);
        addPropertyChangeListener(node);
        return node;
    }

    /** Getter for delete action.
     * @return true if the object can be deleted */
    public boolean isDeleteAllowed () {
        return !getPrimaryFile ().isReadOnly ();
    }

    /** Getter for copy action.
     * @return true if the object can be copied */
    public boolean isCopyAllowed ()  {
        return true;
    }

    /** Getter for move action.
     * @return true if the object can be moved */
    public boolean isMoveAllowed ()  {
        return !getPrimaryFile ().isReadOnly ();
    }

    /** Getter for rename action.
     * @return true if the object can be renamed */
    public boolean isRenameAllowed () {
        return !getPrimaryFile ().isReadOnly ();
    }

    /** Handles copy of the data object.
     * @param f target folder
     * @return the new data object
     * @exception IOException if an error occures */
    protected DataObject handleCopy (DataFolder f) throws IOException {
        return handleCopy(f, getName());
    }

    protected DataObject handleCopy (DataFolder f, String name) throws IOException {
        String newname = FileUtil.findFreeFileName (f.getPrimaryFile (), name, GS_EXTENSION);
        FileObject fo = FileUtil.copyFile (getPrimaryFile (), f.getPrimaryFile (), newname);
        return new GroupShadow(fo, getLoader());
    }

    /** Deals with deleting of the object. Must be overriden in children.
     * @exception IOException if an error occures */
    protected void handleDelete () throws IOException {

        FileLock lock = getPrimaryFile ().lock ();
        try {
            getPrimaryFile ().delete (lock);
        } finally {
            lock.releaseLock ();
        }
    }

    /* Handles renaming of the object.
     * Must be overriden in children.
     *
     * @param name name to rename the object to
     * @return new primary file of the object
     * @exception IOException if an error occures
     */
    protected FileObject handleRename (String name) throws IOException {
        FileLock lock = getPrimaryFile ().lock ();
        try {
            getPrimaryFile ().rename (lock, name, GS_EXTENSION);
        } finally {
            lock.releaseLock ();
        }
        return getPrimaryFile ();
    }

    /** Handles move of the object. Must be overriden in children.
     * @param f target data folder
     * @return new primary file of the object
     * @exception IOException if an error occures */
    protected FileObject handleMove (DataFolder f) throws IOException {
        String name = FileUtil.findFreeFileName (f.getPrimaryFile (), getName (), GS_EXTENSION);
        return FileUtil.moveFile (getPrimaryFile (), f.getPrimaryFile (), name);
    }


    /** Gets help context for this object.
     * @return help context */
    public org.openide.util.HelpCtx getHelpCtx () {
        return new HelpCtx (GroupShadow.class);
    }

    /** Adds a {@link CompilerCookie compilation cookie}. */
    public Node.Cookie getCookie (Class cookie) {
        if (cookie.isAssignableFrom(GroupShadowCompiler.class)) {
            return new GroupShadowCompiler (this, cookie);
        } else if (cookie == TemplateWizard.Iterator.class) {
            return getGroupTemplateIterator();
        } else {
            return super.getCookie (cookie);
        }
    }

    /** Reads whole file to the List.
     * @param fo file object to be read
     * @return List of String */
    public static List readLinks(FileObject fo) throws IOException {
        String line;
        List list = new ArrayList();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(fo.getInputStream()));

            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) br.close();
        }
        
        return list;
    }

    /** Reads whole file to the List.
     * @return List of String's */
    public List readLinks() throws IOException {
        return readLinks(getPrimaryFile());
    }

    /** Writes List as new content of file.
     * @param list List of String's */
    public static void writeLinks(List list, FileObject fo) throws IOException {
        String line;
        Iterator iterator = list.iterator();
        BufferedWriter bw = null;
        FileLock lock = null;
        try {
            lock = fo.lock();
            bw = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream(lock)));
            while (iterator.hasNext()) {
                line = (String) iterator.next();
                bw.write(line); 
                bw.newLine();
            }
        } catch (IOException ex) {
            throw ex;
        }
        finally {
            if (lock != null) lock.releaseLock();
            if (bw != null) {
                bw.close();
            }
        }
    }

    /** Writes List as new content of file.
     * @param list List of String */
    protected void writeLinks(List list) throws IOException {
        writeLinks(list, getPrimaryFile());
    }

    /** Get link name.
     * @param fo file object
     * @return file name */
    public static String getLinkName(FileObject fo) {
        return fo.getPackageNameExt('/', '.');
    }

    /** Get FileObject for given filename.
     * @param filename filename
     * @return FileObject */
    private static FileObject findFileObject(String filename) {

        return TopManager.getDefault().getRepository().findResource(filename);
    }

    /** Get DataObject for given filename.
     * @param filename filename
     * @return DataObject
     */
    private static DataObject getDataObjectByName(String filename) throws DataObjectNotFoundException {

        FileObject tempfo = findFileObject(filename);
        return (tempfo != null) ? DataObject.find(tempfo): null;
    }

    /** Reads filenames from shadow and creates an array of DataObjects for them.
     * @return array that can contain DataObjects or Strings with names of invalid
     * links */
    public Object[] getLinks() {
        FileObject pf = getPrimaryFile(); //, parent = pf.getParent(), tempfo;
        DataObject obj;
        String line;
        HashSet set = new HashSet();
        List linearray;

        try {
            linearray = readLinks(pf);
            Iterator it = linearray.iterator();
            while (it.hasNext()) {
                line = (String)it.next();
                try {
                    if ((obj = getDataObjectByName(line)) != null) {
                        set.add(obj);
                    }
                    else set.add(new String(line));
                } catch (DataObjectNotFoundException ex) {
                    // can be thrown when the link is not recognized by any data loader
                    // in this case I can't help so ignore it
                }
            }
        } catch (IOException ex) {
            // it can be ignored
        }

        // the array can contain DataObjects and Strings !
        return set.toArray();
    }

    /** Replace the oldprefix by a newprefix in name.
     * @param name name
     * @param oldprefix old prefix
     * @param newprefix new prefix
     * @return new name
     */
    public static String createName(String name, String oldprefix, String newprefix) {
        if (name.startsWith(oldprefix)) {
            return newprefix + name.substring(oldprefix.length());
        }
        return name;
    }

    /** Replaces all occurences of __.*__ by a given text
     * @param name name with __.*__ substrings
     * @param pattern replacement
     * @return a string with __.*__ replaced
     */
    private String replaceName0(String name, String pattern) {
        StringBuffer sb = new StringBuffer(256);
        int i = 0, j, k;

        while ((j = name.indexOf("__", i)) != -1) { // NOI18N

            // first occurence found
            k = name.indexOf("__", j + 2); // NOI18N
            if (k != -1) {
                // second occurence found, copy start part and pattern
                sb.append(name.substring(i, j));
                sb.append(pattern);
                i = k + 2;
            } else {
                break;
            }
        }
        // copy the rest
        sb.append(name.substring(i, name.length ()));
        return sb.toString();
    }

    /** Replaces name according to namming pattern defined by templatePattern
     * property or fails to replaceName0() if the property is <code>null</code>. */
    private String replaceName(String name, String pattern) {
        String fmt = getTemplatePattern();
        
        if(!isUsePattern() || fmt==null){
            return replaceName0(name,pattern);
        }

        // filter out all characters before "__" // NOI18N
        String postfix = ""; // NOI18N
        try{
            int i = name.lastIndexOf("__"); // NOI18N
            if(i>0){
                postfix = name.substring(i+2);
            }
        }catch(IndexOutOfBoundsException ex){
            //use default value
        }

        String subst = string3(name,pattern);
        return MessageFormat.format(fmt,new String[]{name,pattern,postfix,subst});
    }

    /** Backward substitution in name by x.
     * SE: it calls recursively itself until whole substituion done
     * x must not contain substitution pattern !!! */
    private String substitute(String name, String x){
        StringBuffer sb = new StringBuffer(name);
        int j = name.length();
        int i = name.lastIndexOf("__",j); // NOI18N
        j = i-1;
        if(i>=0){
            i = name.lastIndexOf("__",j); // NOI18N
            if(i>=0){
                sb.delete(i,j+3);
                sb.insert(i,x);
                return substitute(sb.toString(),x);
            }
        }
        return name;
    }

    /** Substitution wrapper for special cases.
     * @returns String representing new name after substitution */
    private String string3(String name, String pattern) {

        String patch;
        if(name.startsWith("__")){ // NOI18N
            patch = name;
        } else {
            patch = "__" + name; // NOI18N
        }

        String s3 = substitute(patch,pattern);
        if (s3.startsWith("__")){ // NOI18N
            s3 = s3.substring(2);
        }
        return s3;
    }

    /** Implementation of handleCreateFromTemplate for GroupShadow.
     * All members of this group are called to create new objects. */
    protected DataObject handleCreateFromTemplate(DataFolder df, String name) throws IOException {
        List createdObjs = createGroupFromTemplate(df, name, true);
        return createdObjs != null && createdObjs.size() > 0 ? 
                   (DataObject)createdObjs.get(0) : this;
    }

    /** Creates new objects from all members of this group.
     * @returns List of created objects. */
    private List createGroupFromTemplate(DataFolder df,
                                                   String name,
                                                   boolean root) throws IOException {
        if (gsprocessed == null) gsprocessed = this;
        else return null;

        if (name == null) // name is not specified
            name = FileUtil.findFreeFileName(df.getPrimaryFile(),
                                          getPrimaryFile().getName(), GS_EXTENSION);

        Object[] objs = getLinks();
        ArrayList createdObjs = new ArrayList(objs.length+1);
        ArrayList linksList = new ArrayList(objs.length);

        try {
            for (int i=0; i < objs.length; i++) {
                if (objs[i] instanceof DataObject) {
                    DataObject original = (DataObject)objs[i];

                    if (original instanceof GroupShadow) {
                        GroupShadow gs = (GroupShadow)original;
                        List items = gs.createGroupFromTemplate(df, name, false);
                        if (items != null) {
                            for (int j=0, n=items.size(); j < n; j++) {
                                DataObject obj = (DataObject)items.get(j);
                                createdObjs.add(obj);
                                linksList.add(getLinkName(obj.getPrimaryFile()));
                                if (j == 0 && obj instanceof GroupShadow
                                      && gs.getTemplateAll())
                                    break;
                            }
//                            createdObjs.addAll(items);
                        }
                    }
                    else {
                        String repName = replaceName(original.getName(), name);
                        DataObject newObj = original.createFromTemplate(df, repName);
                        createdObjs.add(newObj);
                        linksList.add(getLinkName(newObj.getPrimaryFile()));
                    }
                }
            }

            if (objs.length == 0 || getTemplateAll()) { // create also the group
                String repName = root ? name : replaceName(getName(), name);
                FileObject fo = df.getPrimaryFile().createData(repName, GS_EXTENSION);
                writeLinks(linksList, fo);
                GroupShadow gs = (GroupShadow)DataObject.find(fo);
                if (gs == null)
                    gs = new GroupShadow(fo, getLoader());
                createdObjs.add(0, gs);
            }
        }
        catch (IOException ex) {
            throw ex;
        }
        catch (Error er) {
            er.printStackTrace();
            throw er;
        }
        finally {
            gsprocessed = null;
        }

        return createdObjs;
    }

    /** Setter for showLinks property.
     * @param show if true also show real packages and names of targets */
    public void setShowLinks(boolean show) {
        showLinks = show;
    }

    /** Getter for showLinks property. */
    public boolean getShowLinks() {
        return showLinks;
    }

    /** Setter for template pattern.
     * @exception IOException if error occured */
    public void setTemplatePattern(String templatePattern) throws IOException{
        final FileObject fo = getPrimaryFile();
        String old = getTemplatePattern();

        fo.setAttribute(PROP_TEMPLATE_PATTERN, templatePattern);

        if (old != templatePattern) {
            firePropertyChange(PROP_TEMPLATE_PATTERN, old, templatePattern);
        }

    }

    /** Getter for template pattern. */
    public String getTemplatePattern(){
        Object value = getPrimaryFile().getAttribute(GroupShadow.PROP_TEMPLATE_PATTERN);
        if(value != null && value instanceof String) 
            return (String)value;
        else 
            // Default pattern.
            return "{1}";
    }

    
    /** Getter for use pattern property. */
    public boolean isUsePattern() {
        Object value = getPrimaryFile().getAttribute(GroupShadow.PROP_USE_PATTERN);
        if(value != null && value instanceof Boolean)
            return ((Boolean)value).booleanValue();
        
        return false;
    }

    
    /** Setter for use pattern property. */
    public void setUsePattern(boolean usePattern) throws IOException {
        FileObject fileObject = getPrimaryFile();
        boolean oldValue = isUsePattern();
        
        if(usePattern == oldValue)
            return;
        
        fileObject.setAttribute(PROP_USE_PATTERN, usePattern ? Boolean.TRUE : Boolean.FALSE);
        
        firePropertyChange(PROP_USE_PATTERN, oldValue ? Boolean.TRUE : Boolean.FALSE, usePattern ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /** Getter for template all. */
    public boolean getTemplateAll() {
        Object o = getPrimaryFile().getAttribute(GroupShadow.PROP_TEMPLATE_ALL);
        if (o instanceof Boolean) return ((Boolean) o).booleanValue();
        else return false;
    }


    /** Setter for template all. */
    public void setTemplateAll(boolean templateAll) throws IOException {
        final FileObject fo = getPrimaryFile();
        boolean oldtempl = getTemplateAll();

        fo.setAttribute(PROP_TEMPLATE_ALL, (templateAll ? Boolean.TRUE : null));

        if (oldtempl != templateAll) {
            firePropertyChange(PROP_TEMPLATE_ALL, oldtempl ? Boolean.TRUE : Boolean.FALSE, templateAll ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    /** Getter for resources */
    static String getLocalizedString (String s) {
        return NbBundle.getBundle (GroupShadow.class).getString (s);
    }

    /* ======================  inner class(es) ======================== */

    /** Node for group shadow data object. */
    public class GroupShadowNode extends DataNode implements PropertyChangeListener {

        /** Creates a folder node with some children.
         * @param ch children to use for the node */
        public GroupShadowNode (final DataObject dob, Children children) {
            super (dob, children);
            setIconBase(GS_ICON_BASE);
        }

        
        /** Gets display name.
         * @return display name */
        public String getDisplayName() {
            if (groupformat == null) {
                groupformat = new MessageFormat(GroupShadow.getLocalizedString("FMT_groupShadowName")); // NOI18N
            }

            String name = getName();
            DataObject dataObject = getDataObject();
            String displayName = super.getDisplayName();
            
            String argument0 = name == null ? "" : name; // NOI18N
            String argument2 = dataObject == null ? "" : dataObject.getPrimaryFile().toString(); // NOI18N
            String argument4 = displayName == null ? "" : displayName; // NOI18N
            
            String display = groupformat.format(
                new Object[] { argument0,
                               "", // NOI18N
                               argument2,
                               "", // NOI18N
                               argument4
                });
            try {
                display = getDataObject ().getPrimaryFile ().getFileSystem ()
                    .getStatus().annotateName (display, getDataObject ().files ());
                
            } catch (FileStateInvalidException e) {
                // no fs, do nothing
            }
            return display;
            //      return super.getDisplayName() + GroupShadow.getLocalizedString("PROP_group"); // " (group)"; // NOI18N
        }


        /** Initializes sheet of properties.
         * @return sheet */
        protected Sheet createSheet() {
            Sheet s = super.createSheet();
            updateSheet(s);
            return s;
        }

        /** Listens to GroupShadow dataobject and updates Expert properties
         * list visibility (on "template" property change). */
        public void propertyChange(PropertyChangeEvent evt) {
            if (DataObject.PROP_TEMPLATE.equals(evt.getPropertyName())) {
                if (evt.getNewValue() != null
                      && !evt.getNewValue().equals(evt.getOldValue()))
                    updateSheet(getSheet());
            } else if(PROP_USE_PATTERN.equals(evt.getPropertyName())) {
                updateSheet(getSheet());
            }
        }

        /** Conditionally fills the set */
        private void fillExpertSet(Sheet.Set set){
            final DataObject obj = getDataObject();
            Node.Property p;

            // put properties to set
            try {
                /*X
                  p = new PropertySupport.Reflection (obj, Boolean.TYPE, "getShowLinks", "setShowLinks");
                  p.setName(GroupShadow.PROP_SHOW_LINKS);
                  p.setDisplayName(GroupShadow.getLocalizedString("PROP_showlinks"));
                  p.setShortDescription(GroupShadow.getLocalizedString("HINT_showlinks"));
                  ss.put(p);
                */
                if (obj.isTemplate()){
                    p = new PropertySupport.Reflection (obj, Boolean.TYPE, 
                                                        "getTemplateAll", 
                          obj.getPrimaryFile().isReadOnly() ? null : "setTemplateAll"); // NOI18N
                    p.setName(GroupShadow.PROP_TEMPLATE_ALL);
                    p.setDisplayName(GroupShadow.getLocalizedString("PROP_templateall")); // NOI18N
                    p.setShortDescription(GroupShadow.getLocalizedString("HINT_templateall")); // NOI18N
                    set.put(p);

                    class TemplatePatternProperty extends PropertySupport.Reflection {
                        public TemplatePatternProperty (DataObject obj) throws NoSuchMethodException {
                            super (obj, String.class,
                                   "getTemplatePattern", // NOI18N
                                   obj.getPrimaryFile().isReadOnly() ? null : "setTemplatePattern"); // NOI18N
                        }
                        public boolean canWrite() {
                            return isUsePattern();
                        }
                    }
                    p = new TemplatePatternProperty (obj);
                                                        
                    p.setName(GroupShadow.PROP_TEMPLATE_PATTERN);
                    p.setDisplayName(GroupShadow.getLocalizedString("PROP_templatePattern")); // NOI18N
                    p.setShortDescription(GroupShadow.getLocalizedString("HINT_templatePattern")); // NOI18N
                    set.put(p);
                    
                    
                    p = new PropertySupport.Reflection(
                        obj,
                        Boolean.TYPE,
                        "isUsePattern", // NOI18N
                        "setUsePattern"); // NOI18N
                        
                    p.setName(PROP_USE_PATTERN);
                    p.setDisplayName(GroupShadow.getLocalizedString("PROP_UsePattern")); // NOI18N
                    p.setShortDescription(GroupShadow.getLocalizedString("HINT_UsePattern")); // NOI18N
                    
                    set.put(p);
                }
            } catch (Exception ex) {
                throw new InternalError();
            }
        }

        /** Updates expert sheet on property isTemplate change. */
        private void updateSheet(Sheet sheet){
            if (getDataObject().isTemplate()) {
                Sheet.Set set = sheet.get(Sheet.EXPERT);
                if (set == null) {
                    set = Sheet.createExpertSet();
                    fillExpertSet(set);
                    sheet.put(set);
                } 
                else fillExpertSet(set);
            }
            else sheet.remove(Sheet.EXPERT);
        }

        /** Augments the default behaviour to test for {@link NodeTransfer#nodeCutFlavor} and
         * {@link NodeTransfer#nodeCopyFlavor}
         * with the {@link DataObject}. If there is such a flavor then adds
         * the cut and copy flavors. Also, if there is a copy flavor and the
         * data object is a template, adds an instantiate flavor.
         *
         * @param t transferable to use
         * @param s list of {@link PasteType}s
         */
        protected void createPasteTypes (Transferable t, List s) {
            super.createPasteTypes (t, s);
            if (getDataObject().getPrimaryFile().isReadOnly()) return;

            DataObject obj = null;

            // try copy flavor
            obj = (DataObject)NodeTransfer.cookie (
                      t, NodeTransfer.CLIPBOARD_COPY | NodeTransfer.CLIPBOARD_CUT, DataObject.class
                  );

            if (obj != null) {
                if (obj.isCopyAllowed ()) {

                    // copy and cut
                    s.add (new Paste ("PT_copy", obj, false)); // NOI18N
                }
            }
        }
    } // End of GroupShadowNode class.

    
    /** Paste types for data objects. */
    private class Paste extends PasteType {
        
        /** Resource name for the name.*/
        private String resName;
        /** Data object to work with. */
        private DataObject obj;
        /** Indicates whether to clear clipboard. */
        private boolean clearClipboard;

        
        /** Creates paste type.
         * @param resName resource name for the name
         * @param obj object to work with
         * @param clear true if we should clear clipboard
         */
        public Paste (String resName, DataObject obj, boolean clear) {
            this.resName = resName;
            this.obj = obj;
            this.clearClipboard = clear;
        }

        
        /** Gets name. It's obtained from the bundle.
         * @return the name */
        public String getName () {
            return getLocalizedString (resName);
        }

        /** Perform paste operation. */
        public final Transferable paste () throws IOException {
            handle (obj);
            // clear clipboard or preserve content
            return clearClipboard ? ExTransferable.EMPTY : null;
        }

        /** Actually handles the paste action.
         * @param obj the data object to operate on */
        public void handle (DataObject obj2) throws IOException {
            List list = readLinks();
            String name = getLinkName(obj2.getPrimaryFile());
            if (list.indexOf(name) == -1) list.add(name);
            writeLinks(list);
        }
    } // End of Paste class.

    
    /** Children for group shadow. */
    private class GroupChildren extends Children.Keys {

        /** Creates children. */
        public GroupChildren() {
            getPrimaryFile().addFileChangeListener(new FileChangeAdapter() {
                public void fileChanged(FileEvent fe) { // group file changed
                    update();
                }
            });
        }

        
        /** Overrides superclass method. */
        protected void addNotify() {
            setKeys(Collections.EMPTY_SET);
            RequestProcessor.postRequest(new Runnable() {
                                             public void run() {
                                                 update();
                                             }
                                         });
        }

        /** Overrrides superclass method. */
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
        }

        /** Updates keys. */
        void update() {
            setKeys(getLinks());
        }

        /** Creates nodes. */
        protected Node[] createNodes(Object key) {
            Node nodes[] = new Node[1];

            if (key instanceof DataObject) {
                nodes[0] = (Node) new GroupFilterNode(((DataObject)key).getNodeDelegate());
            } else {
                nodes[0] = (Node) new ErrorNode((String)key);
            }
            return nodes;
        }
    } // End of GroupChidren class.

    
    /** FilterNode representing one link. */
    private class GroupFilterNode extends FilterNode implements PropertyChangeListener {

        /** Original file name. */
        String originalFileName = null;
        /** Original data object. */
        DataObject originalDatObj = null;
        
        
        /** Constrictor. */
        public GroupFilterNode(Node original) {
            super(original);
            
            originalDatObj = (DataObject)original.getCookie(DataObject.class);
            if (originalDatObj == null) return; // should not happen

            FileObject originalFO = originalDatObj.getPrimaryFile();
            if (originalFO == null) return; // should not happen
            originalFileName = getLinkName(originalFO);

            originalDatObj.addPropertyChangeListener(this);
        }

        
        /** GroupFilterNode can be always destroyed, when Group is also allow delete.
         * @return <CODE>true</CODE> if Group allow delete.
         */
        public boolean canDestroy () {
            return GroupShadow.this.isDeleteAllowed();
        }

        /** Destroys node. */
        public void destroy() throws IOException {
            originalDatObj.removePropertyChangeListener(this);
            
            List list = readLinks();
            for (int i=0; i < list.size(); i++) {
                String fileName = (String)list.get(i);
                if (fileName.equals(originalFileName)) {
                    list.remove(i);
                    i--;
                }
            }
            writeLinks(list);
        }

        /**
         * @return true if the node can be renamed
         */
        public boolean canRename () {
            return super.canRename() && GroupShadow.this.isRenameAllowed();
        }

        /**
         * @returns true if this object allows cutting.
         */
        public boolean canCut () {
            return super.canCut() && GroupShadow.this.isMoveAllowed();
        }

        
        /** Gets display name. */
        public String getDisplayName() {
//            DataObject obj = (DataObject) this.getCookie(DataObject.class);
            FileObject primary = originalDatObj.getPrimaryFile();
            String fullname = primary.getPackageNameExt ('/', '.');
            int index = fullname.lastIndexOf('/');
            String foldername;
            if (index > -1)
                foldername = fullname.substring(0, index + 1);
            else
                foldername = ""; // NOI18N
            Object[] objs = new Object[] { originalDatObj.getName(), fullname, foldername,
                                           originalDatObj.getNodeDelegate().getDisplayName() };

            if (showLinks) {
                if (vformat == null) {
                    vformat = new MessageFormat(GroupShadow.getLocalizedString("FMT_validTargetName")); // NOI18N
                }
                return vformat.format(objs);
            } else {
                if (vformat2 == null) {
                    vformat2 = new MessageFormat(GroupShadow.getLocalizedString("FMT_validTargetName2")); // NOI18N
                }
                return vformat2.format(objs);
            }
        }

        /** Implements <code>PropertyChangeListener</code>. */
        public void propertyChange(PropertyChangeEvent evt) {
            if (DataObject.PROP_PRIMARY_FILE.equals(evt.getPropertyName())) {
                // primary file has been changed
                try {
                    List list = readLinks();
                    String newName = getLinkName(originalDatObj.getPrimaryFile());

                    for (int i=0; i < list.size(); i++) {
                        String fileName = (String)list.get(i);
                        if (fileName.equals(originalFileName)) {
                            list.set(i,newName);
                        }
                    }
                    originalFileName = newName;
                    writeLinks(list);
                    fireDisplayNameChange(null,null);
                } catch (IOException ex) { } // ignore it here
            }
            
            else if (DataObject.PROP_VALID.equals(evt.getPropertyName())) {
                // data object might be deleted
                if (!originalDatObj.isValid()) { // link becomes invalid
                    originalDatObj.removePropertyChangeListener(this);
                    children.update();
                }
            }
        }
    } // End of GroupFilterNode class.

    
    /** Node representing an invalid link. */
    private class ErrorNode extends AbstractNode implements FileChangeListener {

        /** Name. */
        String name;
        /** Parent folder. */
        FileObject parentFolder;
        /** Parent folders. */
        FileObject[] parentFolders;

        
        /** Constructor. */
        public ErrorNode(String name) {
            super(Children.LEAF);

            systemActions = new SystemAction[] {
                                SystemAction.get(org.openide.actions.DeleteAction.class),
                                null,
                                SystemAction.get(org.openide.actions.ToolsAction.class),
                                SystemAction.get(org.openide.actions.PropertiesAction.class)
                            };

            this.name = name;
            parentFolders = null;

            if (name != null) {
                if (iformat == null) {
                    iformat = new MessageFormat(GroupShadow.getLocalizedString("FMT_invalidTargetName")); // NOI18N
                }
                setDisplayName(iformat.format(new Object[] { "", name })); // NOI18N

                setFolderListening();
            } else {
                if (iformat2 == null) {
                    iformat2 = new MessageFormat(GroupShadow.getLocalizedString("FMT_invalidTargetName2")); // NOI18N
                }
                setDisplayName(iformat2.format(new Object[] { "", name })); // NOI18N
            }
        }

        /** Constructor. */
        public ErrorNode() {
            this(null);
        }

        
        /** Destroys node. */
        public void destroy() throws IOException {

            String name;
            boolean modified = false;

            List list = readLinks();
            Iterator it =  list.iterator();

            while (it.hasNext()) {
                name = (String)it.next();
                if (name.equals(this.name)) {
                    it.remove(); modified = true;
                }
                if (modified) writeLinks(list);
            }

            cancelFolderListening();
        }

        /** Indicates whether node can be destroyed. */
        public boolean canDestroy() {
            return true;
        }

        /** Sets listeners on parent folders. */
        private void setFolderListening() {
            // This link is invalid but its parent could be a valid folder.
            // So we could watch it creating a new file...
            parentFolder = null;
            int ii = name.lastIndexOf('/');

            if (ii > 0) {
                String folder = new String(name.toCharArray(), 0, ii);
                try {
                    DataObject dobj = getDataObjectByName(folder);
                    if (dobj != null) {
                        FileObject fobj = dobj.getPrimaryFile();
                        if (fobj != null) {
                            parentFolders = new FileObject[1];
                            parentFolders[0] = fobj;
                        }
                    }
                } catch (DataObjectNotFoundException ex) {
                }
            } else { // use filesystems' folders
                org.openide.filesystems.FileSystem[] fs =
                    TopManager.getDefault().getRepository().toArray();
                parentFolders = new FileObject[fs.length];

                for (int i=0; i < fs.length; i++)
                    parentFolders[i] = fs[i].getRoot();
            }

            if (parentFolders == null) return;

            for (int i=0; i < parentFolders.length; i++)
                parentFolders[i].addFileChangeListener(this);
        }

        /** Unsets listeners on parent folders. */
        private void cancelFolderListening() {
            if (parentFolders != null)
                for (int i=0; i < parentFolders.length; i++) {
                    parentFolders[i].removeFileChangeListener(this);
                }
        }

        /** Implements <code>FileChangeListener</code> interface method. */
        public void fileDataCreated(FileEvent fe) {
            if (getLinkName(fe.getFile()).equals(name)) {
                cancelFolderListening();
                children.update();
            }
        }

        /** Dummy implementation of <code>FileChangeListener</code> interface method. */
        public void fileFolderCreated(FileEvent fe) {}
        /** Dummy implementation of <code>FileChangeListener</code> interface method. */
        public void fileChanged(FileEvent fe) {}
        /** Dummy implementation of <code>FileChangeListener</code> interface method. */
        public void fileDeleted(FileEvent fe) {}
        /** Dummy implementation of <code>FileChangeListener</code> interface method. */
        public void fileRenamed(FileRenameEvent fe) {}
        /** Dummy implementation of <code>FileChangeListener</code> interface method. */
        public void fileAttributeChanged(FileAttributeEvent fe) {}
        
    } // End of ErrorNode class.

    
    /** Template wizard iterator for handling creating group members from template.
     * This iterator is attached to each <code>GroupShadow</code> data object to
     * control group template instantiating. */
    private static class GroupTemplateIterator implements TemplateWizard.Iterator {
        
        /** Target panel. */
        private WizardDescriptor.Panel targetPanel = null;

        /** Constructor. */
        GroupTemplateIterator() {
        }

        /** Instantiates the template using informations provided by the wizard.
         * @param wiz the wizard
         * @return set of data objects that has been created (should contain at least one) 
         * @exception IOException if the instantiation fails */
        public Set instantiate(TemplateWizard wiz) throws IOException {
            String nam = wiz.getTargetName();
            DataFolder folder = wiz.getTargetFolder();
            DataObject template = wiz.getTemplate();

            // new objects from all members of template group will be created
            // (even from nested groups)
            if (template instanceof GroupShadow) {
                GroupShadow group = (GroupShadow) template;
                List createdObjs = group.createGroupFromTemplate(folder,nam,true);
                HashSet templObjs = new HashSet(createdObjs.size());

                if (createdObjs != null) {
                    Iterator it = createdObjs.iterator();
                    while (it.hasNext()) {
                        DataObject obj = (DataObject) it.next();
                        if (!(obj instanceof DataFolder) && !(obj instanceof GroupShadow)) {
                            templObjs.add(obj);
                            Node node = obj.getNodeDelegate();
                            SystemAction sa = node.getDefaultAction();
                            if (sa != null) {
                                TopManager.getDefault().getActionManager().invokeAction(sa, new ActionEvent(node, ActionEvent.ACTION_PERFORMED, "")); // NOI18N
                            }
                        }
                    }
                }

                return templObjs;
            }
            else {
                DataObject obj = nam == null ?
                             template.createFromTemplate(folder) :
                             template.createFromTemplate(folder, nam);
  
                return Collections.singleton(obj);
            }
        }

        /** Initializes this instance. */
        public void initialize(TemplateWizard wiz) {
            targetPanel = wiz.targetChooser();
        }
        
        /** No-op implementation. */
        public void uninitialize(TemplateWizard wiz) {
            targetPanel = null;
        }

        /** Get the current panel.
         * @return the panel */
        public WizardDescriptor.Panel current() {
            return targetPanel;
        }
        
        /** Current name of the panel. */
        public String name () {
            return "";
        }

        /** Test whether there is a next panel.
         * @return <code>false</code> - only one panel is used */
        public boolean hasNext() {
            return false;
        }

        /** Test whether there is a previous panel.
        * @return <code>false</code> - only one panel is used
        */
        public boolean hasPrevious() {
            return false;
        }

        /** Move to the next panel.
         * I.e. increment its index, need not actually change any GUI itself.
         * @exception NoSuchElementException if the panel does not exist */
        public void nextPanel() {
            throw new NoSuchElementException();
        }

        /** Move to the previous panel.
         * I.e. decrement its index, need not actually change any GUI itself.
         * @exception NoSuchElementException if the panel does not exist */
        public void previousPanel() {
            throw new NoSuchElementException();
        }

        /** Dummy implementation of method <code>TemplateWizard.Iterator</code> interface method. */
        public void addChangeListener(ChangeListener l) {}

        /** Dummy implementation of method <code>TemplateWizard.Iterator</code> interface method. */
        public void removeChangeListener(ChangeListener l) {}
        
    } // End of GroupTemplateIterator class.
    
}
