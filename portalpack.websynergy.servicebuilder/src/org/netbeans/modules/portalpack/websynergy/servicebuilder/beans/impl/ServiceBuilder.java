/**
 *	This generated bean class ServiceBuilder
 *	matches the schema element 'service-builder'.
 *
 *
 *	This class matches the root element of the DTD,
 *	and is the root of the bean graph.
 *
 * 	serviceBuilder <service-builder> : ServiceBuilder
 * 		[attr: package-path CDATA #REQUIRED ]
 * 		author <author> : String[0,1]
 * 		namespace <namespace> : String
 * 		entity <entity> : Entity[1,n]
 * 			[attr: name CDATA #REQUIRED ]
 * 			[attr: table CDATA #IMPLIED ]
 * 			[attr: uuid CDATA #IMPLIED ]
 * 			[attr: local-service CDATA #IMPLIED ]
 * 			[attr: remote-service CDATA #IMPLIED ]
 * 			[attr: persistence-class CDATA #IMPLIED ]
 * 			[attr: data-source CDATA #IMPLIED ]
 * 			[attr: session-factory CDATA #IMPLIED ]
 * 			[attr: tx-manager CDATA #IMPLIED ]
 * 			[attr: cache-enabled CDATA #IMPLIED ]
 * 			column <column> : String[0,n]
 * 				[attr: name CDATA #REQUIRED ]
 * 				[attr: db-name CDATA #IMPLIED ]
 * 				[attr: type CDATA #REQUIRED ]
 * 				[attr: primary CDATA #IMPLIED ]
 * 				[attr: entity CDATA #IMPLIED ]
 * 				[attr: mapping-key CDATA #IMPLIED ]
 * 				[attr: mapping-table CDATA #IMPLIED ]
 * 				[attr: id-type CDATA #IMPLIED ]
 * 				[attr: id-param CDATA #IMPLIED ]
 * 				[attr: convert-null CDATA #IMPLIED ]
 * 			order <order> : Order[0,1]
 * 				[attr: by CDATA #IMPLIED ]
 * 				orderColumn <order-column> : String[1,n]
 * 					[attr: name CDATA #REQUIRED ]
 * 					[attr: case-sensitive CDATA #IMPLIED ]
 * 					[attr: order-by CDATA #IMPLIED ]
 * 			finder <finder> : Finder[0,n]
 * 				[attr: name CDATA #REQUIRED ]
 * 				[attr: return-type CDATA #REQUIRED ]
 * 				[attr: where CDATA #IMPLIED ]
 * 				[attr: db-index CDATA #IMPLIED ]
 * 				finderColumn <finder-column> : String[1,n]
 * 					[attr: name CDATA #REQUIRED ]
 * 					[attr: case-sensitive CDATA #IMPLIED ]
 * 					[attr: comparator CDATA #IMPLIED ]
 * 			reference <reference> : String[0,n]
 * 				[attr: package-path CDATA #IMPLIED ]
 * 				[attr: entity CDATA #IMPLIED ]
 * 			txRequired <tx-required> : String[0,n]
 * 		exceptions <exceptions> : Exceptions[0,1]
 * 			exception <exception> : String[0,n]
 *
 * @Generated
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl;

public class ServiceBuilder extends org.netbeans.modules.schema2beans.BaseBean
 implements  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.ServiceBuilder
            , org.netbeans.modules.schema2beans.Bean {
	public static final String COMMENTS = "Comments";	// NOI18N
	public static final String PACKAGEPATH = "PackagePath";	// NOI18N
	public static final String AUTHOR = "Author";	// NOI18N
	public static final String NAMESPACE = "Namespace";	// NOI18N
	public static final String ENTITY = "Entity";	// NOI18N
	public static final String EXCEPTIONS = "Exceptions";	// NOI18N

	private static final org.netbeans.modules.schema2beans.Version baseBeanRuntimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);
	private java.util.List _Comments = new java.util.ArrayList();	// List<java.lang.String>
	private java.lang.String _PackagePath;
	private String _Author;
	private String _Namespace;
	private java.util.List _Entity = new java.util.ArrayList();	// List<Entity>
	private Exceptions _Exceptions;
	private java.lang.String schemaLocation;
	private org.netbeans.modules.schema2beans.BaseBean parent;
	private java.beans.PropertyChangeSupport eventListeners;
	private org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder.DocType docType = null;
	private java.util.Map propByName = new java.util.HashMap(8, 1.0f);
	private java.util.List beanPropList = null;	// List<org.netbeans.modules.schema2beans.BeanProp>

	/**
	 * Normal starting point constructor.
	 */
	public ServiceBuilder() {
		this(null, baseBeanRuntimeVersion);
	}

	/**
	 * This constructor is here for BaseBean compatibility.
	 */
	public ServiceBuilder(java.util.Vector comps, org.netbeans.modules.schema2beans.Version baseBeanRuntimeVersion) {
		super(null, baseBeanRuntimeVersion);
		_PackagePath = "";
		_Namespace = "";
	}

	/**
	 * Required parameters constructor
	 */
	public ServiceBuilder(java.lang.String packagePath, String namespace, org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity[] entity) {
		super(null, baseBeanRuntimeVersion);
		_PackagePath = packagePath;
		_Namespace = namespace;
		if (entity!= null) {
			((java.util.ArrayList) _Entity).ensureCapacity(entity.length);
			for (int i = 0; i < entity.length; ++i) {
				if (entity[i] != null) {
					entity[i]._setParent(this);
				}
				_Entity.add(entity[i]);
			}
		}
	}

	/**
	 * Deep copy
	 */
	public ServiceBuilder(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder source) {
		this(source, false);
	}

	/**
	 * Deep copy
	 * @param justData just copy the XML relevant data
	 */
	public ServiceBuilder(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder source, boolean justData) {
		this(source, null, justData);
	}

	/**
	 * Deep copy
	 */
	public ServiceBuilder(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData) {
		super(null, baseBeanRuntimeVersion);
		this.parent = parent;
		for (java.util.Iterator it = source._Comments.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_Comments.add(srcElement);
		}
		_PackagePath = source._PackagePath;
		_Author = source._Author;
		_Namespace = source._Namespace;
		for (java.util.Iterator it = source._Entity.iterator(); 
			it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity srcElement = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity)it.next();
			_Entity.add((srcElement == null) ? null : (Entity) newEntity(( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
            ) srcElement, this, justData));
		}
		_Exceptions = (source._Exceptions == null) ? null : (Exceptions) newExceptions(( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions
            ) source._Exceptions, this, justData);
		if (source.docType != null) {
			docType = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder.DocType(source.docType);
		}
		schemaLocation = source.schemaLocation;
		if (!justData) {
			if (source.eventListeners != null) {
				eventListeners = new java.beans.PropertyChangeSupport(this);
				java.beans.PropertyChangeListener[] theListeners = source.eventListeners.getPropertyChangeListeners();
				for (int i = 0; i < theListeners.length; ++i) {
					eventListeners.addPropertyChangeListener(theListeners[i]);
				}
			}
		}
	}

	public ServiceBuilder(org.w3c.dom.Node doc, int currentlyUnusedOptions) {
		this();
		readFromDocument((org.w3c.dom.Document) doc);
	}

	// This attribute is an array, possibly empty
	public void setComments(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeComments()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getComments(i) == null : value[i].equals(getComments(i)))) {
					same = false;
					break;
				}
			}
			if (same) {
				// No change.
				return;
			}
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			// See if only 1 thing changed.
			int addIndex = -1;
			int removeIndex = -1;
			int oldSize = sizeComments();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getComments(oldIndex) == null : value[newIndex].equals(getComments(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getComments(oldIndex+1) == null : value[newIndex].equals(getComments(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getComments(oldIndex) == null : value[newIndex+1].equals(getComments(oldIndex)))) {
						addIndex = newIndex;
						++newIndex;
					} else {
						// More than 1 difference.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					}
				}
				if (checkAddOrRemoveOne && addIndex == -1 && removeIndex == -1) {
					if (oldSize + 1 == newSize) {
						// Added last one
						addIndex = oldSize;
					} else if (oldSize == newSize + 1) {
						// Removed last one
						removeIndex = newSize;
					}
				}
			}
			if (addIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Comments."+Integer.toHexString(addIndex), null, value[addIndex]);
				_Comments.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Comments."+Integer.toHexString(removeIndex), getComments(removeIndex), null);
				_Comments.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Comments.-1", getComments(), value);
			}
		}
		_Comments.clear();
		((java.util.ArrayList) _Comments).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_Comments.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setComments(int index, java.lang.String value) {
		if (value == null ? getComments(index) == null : value.equals(getComments(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Comments."+Integer.toHexString(index), getComments(index), value);
			eventListeners.firePropertyChange(event);
		}
		_Comments.set(index, value);
	}

	public java.lang.String[] getComments() {
		java.lang.String[] arr = new java.lang.String[_Comments.size()];
		return (java.lang.String[]) _Comments.toArray(arr);
	}

	public java.util.List fetchCommentsList() {
		return _Comments;
	}

	public java.lang.String getComments(int index) {
		return (java.lang.String)_Comments.get(index);
	}

	// Return the number of comments
	public int sizeComments() {
		return _Comments.size();
	}

	public int addComments(java.lang.String value) {
		_Comments.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Comments."+Integer.toHexString(_Comments.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _Comments.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeComments(java.lang.String value) {
		int pos = _Comments.indexOf(value);
		if (pos >= 0) {
			_Comments.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Comments."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is mandatory
	public void setPackagePath(java.lang.String value) {
		if (value == null ? _PackagePath == null : value.equals(_PackagePath)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/PackagePath", getPackagePath(), value);
		}
		_PackagePath = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getPackagePath() {
		return _PackagePath;
	}

	// This attribute is optional
	public void setAuthor(String value) {
		if (value == null ? _Author == null : value.equals(_Author)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Author", getAuthor(), value);
		}
		_Author = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public String getAuthor() {
		return _Author;
	}

	// This attribute is mandatory
	public void setNamespace(String value) {
		if (value == null ? _Namespace == null : value.equals(_Namespace)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Namespace", getNamespace(), value);
		}
		_Namespace = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public String getNamespace() {
		return _Namespace;
	}

	// This attribute is an array containing at least one element
	public void setEntity( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
            [] valueInterface) {
		Entity[] value = (Entity[]) valueInterface;
		if (value == null)
			value = new Entity[0];
		if (value.length == sizeEntity()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getEntity(i) == null : value[i].equals(getEntity(i)))) {
					same = false;
					break;
				}
			}
			if (same) {
				// No change.
				return;
			}
		}
		// Make the foreign beans take on our property change event listeners.
		// Maintain the parent reference.
		for (int i = 0; i < value.length; ++i) {
			if (value[i] != null) {
				value[i]._setPropertyChangeSupport(eventListeners);
				value[i]._setParent(this);
			}
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			// See if only 1 thing changed.
			int addIndex = -1;
			int removeIndex = -1;
			int oldSize = sizeEntity();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getEntity(oldIndex) == null : value[newIndex].equals(getEntity(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getEntity(oldIndex+1) == null : value[newIndex].equals(getEntity(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getEntity(oldIndex) == null : value[newIndex+1].equals(getEntity(oldIndex)))) {
						addIndex = newIndex;
						++newIndex;
					} else {
						// More than 1 difference.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					}
				}
				if (checkAddOrRemoveOne && addIndex == -1 && removeIndex == -1) {
					if (oldSize + 1 == newSize) {
						// Added last one
						addIndex = oldSize;
					} else if (oldSize == newSize + 1) {
						// Removed last one
						removeIndex = newSize;
					}
				}
			}
			if (addIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Entity."+Integer.toHexString(addIndex), null, value[addIndex]);
				_Entity.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Entity."+Integer.toHexString(removeIndex), getEntity(removeIndex), null);
				_Entity.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Entity.-1", getEntity(), value);
			}
		}
		_Entity.clear();
		((java.util.ArrayList) _Entity).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_Entity.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setEntity(int index,  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
             valueInterface) {
		Entity value = (Entity) valueInterface;
		if (value == null ? getEntity(index) == null : value.equals(getEntity(index))) {
			// No change.
			return;
		}
		if (value != null) {
			value._setParent(this);
		}
		if (value != null) {
			// Make the foreign beans take on our property change event listeners.
			value._setPropertyChangeSupport(eventListeners);
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Entity."+Integer.toHexString(index), getEntity(index), value);
			eventListeners.firePropertyChange(event);
		}
		_Entity.set(index, value);
	}

	public  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
            [] getEntity() {
		Entity[] arr = new Entity[_Entity.size()];
		return (Entity[]) _Entity.toArray(arr);
	}

	public java.util.List fetchEntityList() {
		return _Entity;
	}

	public  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
             getEntity(int index) {
		return (Entity)_Entity.get(index);
	}

	// Return the number of entity
	public int sizeEntity() {
		return _Entity.size();
	}

	public int addEntity( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
             valueInterface) {
		Entity value = (Entity) valueInterface;
		if (value != null) {
			value._setParent(this);
		}
		if (value != null) {
			// Make the foreign beans take on our property change event listeners.
			value._setPropertyChangeSupport(eventListeners);
		}
		_Entity.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Entity."+Integer.toHexString(_Entity.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _Entity.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeEntity( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
             valueInterface) {
		Entity value = (Entity) valueInterface;
		int pos = _Entity.indexOf(value);
		if (pos >= 0) {
			_Entity.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Entity."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is optional
	public void setExceptions( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions
             valueInterface) {
		Exceptions value = (Exceptions) valueInterface;
		if (value == null ? _Exceptions == null : value.equals(_Exceptions)) {
			// No change.
			return;
		}
		// Make the foreign beans take on our property change event listeners.
		// Maintain the parent reference.
		if (value != null) {
			value._setPropertyChangeSupport(eventListeners);
			value._setParent(this);
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Exceptions", getExceptions(), value);
		}
		_Exceptions = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions
             getExceptions() {
		return _Exceptions;
	}

	public void _setSchemaLocation(String location) {
		schemaLocation = location;
	}

	public String _getSchemaLocation() {
		return schemaLocation;
	}

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder.DocType fetchDocType() {
		return docType;
	}

	public void changeDocType(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder.DocType dt) {
		docType = dt;
	}

	public void changeDocType(String publicId, String systemId) {
		docType = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder.DocType(publicId, systemId);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity newEntity() {
		return new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity();
	}

	/**
	 * Create a new bean, copying from another one.
	 * This does not add it to any bean graph.
	 */
	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity newEntity(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData) {
		return new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity((Entity) source, parent, justData);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions newExceptions() {
		return new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Exceptions();
	}

	/**
	 * Create a new bean, copying from another one.
	 * This does not add it to any bean graph.
	 */
	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions newExceptions(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData) {
		return new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Exceptions((Exceptions) source, parent, justData);
	}

	public void _setParent(org.netbeans.modules.schema2beans.BaseBean parent) {
		this.parent = parent;
	}

	public String _getXPathExpr() {
		if (parent == null) {
			return "/service-builder";
		} else {
			String parentXPathExpr = parent._getXPathExpr();
			String myExpr = parent.nameChild(this, false, false, true);
			return parentXPathExpr + "/" + myExpr;
		}
	}

	public String _getXPathExpr(Object childObj) {
		String childName = nameChild(childObj, false, false, true);
		if (childName == null) {
			throw new IllegalArgumentException("childObj ("+childObj.toString()+") is not a child of this bean (ServiceBuilder).");
		}
		return _getXPathExpr() + "/" + childName;
	}

	public void write(org.openide.filesystems.FileObject fo) throws java.io.IOException {
		org.openide.filesystems.FileLock lock = fo.lock();
		try {
			java.io.OutputStream out = fo.getOutputStream(lock);
			write(out);
			out.close();
		} finally {
			lock.releaseLock();
		}
	}

	public void write(final org.openide.filesystems.FileObject dir, final String filename) throws java.io.IOException {
		org.openide.filesystems.FileSystem fs = dir.getFileSystem();
		fs.runAtomicAction(new org.openide.filesystems.FileSystem.AtomicAction()
		{
			public void run() throws java.io.IOException {
				org.openide.filesystems.FileObject file = dir.getFileObject(filename);
				if (file == null) {
					file = dir.createData(filename);
				}
				write(file);
			}
		}
		);
	}

	public void write(java.io.File f) throws java.io.IOException {
		java.io.OutputStream out = new java.io.FileOutputStream(f);
		try {
			write(out);
		} finally {
			out.close();
		}
	}

	public void write(java.io.OutputStream out) throws java.io.IOException {
		write(out, null);
	}

	public void write(java.io.OutputStream out, String encoding) throws java.io.IOException {
		java.io.Writer w;
		if (encoding == null) {
			encoding = "UTF-8";	// NOI18N
		}
		w = new java.io.BufferedWriter(new java.io.OutputStreamWriter(out, encoding));
		write(w, encoding);
		w.flush();
	}

	/**
	 * Print this Java Bean to @param out including an XML header.
	 * @param encoding is the encoding style that @param out was opened with.
	 */
	public void write(java.io.Writer out, String encoding) throws java.io.IOException {
		out.write("<?xml version='1.0'");	// NOI18N
		if (encoding != null)
			out.write(" encoding='"+encoding+"'");	// NOI18N
		out.write(" ?>\n");	// NOI18N
		if (docType != null) {
			out.write(docType.toString());
			out.write("\n");
		}
		writeNode(out, "service-builder", "");	// NOI18N
	}

	public void writeNode(java.io.Writer out) throws java.io.IOException {
		String myName;
		if (parent == null) {
			myName = "service-builder";
		} else {
			myName = parent.nameChild(this, false, true);
			if (myName == null) {
				myName = "service-builder";
			}
		}
		writeNode(out, myName, "");	// NOI18N
	}

	public void writeNode(java.io.Writer out, String nodeName, String indent) throws java.io.IOException {
		writeNode(out, nodeName, null, indent, new java.util.HashMap());
	}

	/**
	 * It's not recommended to call this method directly.
	 */
	public void writeNode(java.io.Writer out, String nodeName, String namespace, String indent, java.util.Map namespaceMap) throws java.io.IOException {
		out.write(indent);
		out.write("<");
		if (namespace != null) {
			out.write((String)namespaceMap.get(namespace));
			out.write(":");
		}
		out.write(nodeName);
		if (schemaLocation != null) {
			namespaceMap.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
			out.write(" xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='");
			out.write(schemaLocation);
			out.write("'");	// NOI18N
		}
		writeNodeAttributes(out, nodeName, namespace, indent, namespaceMap);
		out.write(">\n");
		writeNodeChildren(out, nodeName, namespace, indent, namespaceMap);
		out.write(indent);
		out.write("</");
		if (namespace != null) {
			out.write((String)namespaceMap.get(namespace));
			out.write(":");
		}
		out.write(nodeName);
		out.write(">\n");
	}

	protected void writeNodeAttributes(java.io.Writer out, String nodeName, String namespace, String indent, java.util.Map namespaceMap) throws java.io.IOException {
		// package-path is an attribute with namespace null
		if (_PackagePath != null) {
			out.write(" package-path='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _PackagePath, true);
			out.write("'");	// NOI18N
		}
	}

	protected void writeNodeChildren(java.io.Writer out, String nodeName, String namespace, String indent, java.util.Map namespaceMap) throws java.io.IOException {
		String nextIndent = indent + "	";
		for (java.util.Iterator it = _Comments.iterator(); it.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			if (element != null) {
				out.write(nextIndent);
				out.write("<!--");
				out.write(element);
				out.write("-->\n");
			}
		}
		if (_Author != null) {
			out.write(nextIndent);
			out.write("<author");	// NOI18N
			out.write(">");	// NOI18N
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _Author, false);
			out.write("</author>\n");	// NOI18N
		}
		if (_Namespace != null) {
			out.write(nextIndent);
			out.write("<namespace");	// NOI18N
			out.write(">");	// NOI18N
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _Namespace, false);
			out.write("</namespace>\n");	// NOI18N
		}
		for (java.util.Iterator it = _Entity.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity)it.next();
			if (element != null) {
				element.writeNode(out, "entity", null, nextIndent, namespaceMap);
			}
		}
		if (_Exceptions != null) {
			_Exceptions.writeNode(out, "exceptions", null, nextIndent, namespaceMap);
		}
	}

	public static ServiceBuilder read(org.openide.filesystems.FileObject fo) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException {
		java.io.InputStream in = fo.getInputStream();
		try {
			return read(in);
		} finally {
			in.close();
		}
	}

	public static ServiceBuilder read(java.io.File f) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException {
		java.io.InputStream in = new java.io.FileInputStream(f);
		try {
			return read(in);
		} finally {
			in.close();
		}
	}

	public static ServiceBuilder read(java.io.InputStream in) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException {
		return read(new org.xml.sax.InputSource(in), false, null, null);
	}

	/**
	 * Warning: in readNoEntityResolver character and entity references will
	 * not be read from any DTD in the XML source.
	 * However, this way is faster since no DTDs are looked up
	 * (possibly skipping network access) or parsed.
	 */
	public static ServiceBuilder readNoEntityResolver(java.io.InputStream in) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException {
		return read(new org.xml.sax.InputSource(in), false,
			new org.xml.sax.EntityResolver() {
			public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) {
				java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(new byte[0]);
				return new org.xml.sax.InputSource(bin);
			}
		}
			, null);
	}

	public static ServiceBuilder read(org.xml.sax.InputSource in, boolean validate, org.xml.sax.EntityResolver er, org.xml.sax.ErrorHandler eh) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException {
		javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setValidating(validate);
		dbf.setNamespaceAware(true);
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		if (er != null)	db.setEntityResolver(er);
		if (eh != null)	db.setErrorHandler(eh);
		org.w3c.dom.Document doc = db.parse(in);
		return read(doc);
	}

	public static ServiceBuilder read(org.w3c.dom.Document document) {
		ServiceBuilder aServiceBuilder = new ServiceBuilder();
		aServiceBuilder.readFromDocument(document);
		return aServiceBuilder;
	}

	protected void readFromDocument(org.w3c.dom.Document document) {
		org.w3c.dom.NodeList children = document.getChildNodes();
		int length = children.getLength();
		for (int i = 0; i < length; ++i) {
			if (children.item(i) instanceof org.w3c.dom.DocumentType) {
				docType = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder.DocType((org.w3c.dom.DocumentType)children.item(i));
				break;
			}
		}
		readNode(document.getDocumentElement());
	}

	protected static class ReadState {
		int lastElementType;
		int elementPosition;
	}

	public void readNode(org.w3c.dom.Node node) {
		readNode(node, new java.util.HashMap());
	}

	public void readNode(org.w3c.dom.Node node, java.util.Map namespacePrefixes) {
		if (node.hasAttributes()) {
			org.w3c.dom.NamedNodeMap attrs = node.getAttributes();
			org.w3c.dom.Attr attr;
			java.lang.String attrValue;
			boolean firstNamespaceDef = true;
			for (int attrNum = 0; attrNum < attrs.getLength(); ++attrNum) {
				attr = (org.w3c.dom.Attr) attrs.item(attrNum);
				String attrName = attr.getName();
				if (attrName.startsWith("xmlns:")) {
					if (firstNamespaceDef) {
						firstNamespaceDef = false;
						// Dup prefix map, so as to not write over previous values, and to make it easy to clear out our entries.
						namespacePrefixes = new java.util.HashMap(namespacePrefixes);
					}
					String attrNSPrefix = attrName.substring(6, attrName.length());
					namespacePrefixes.put(attrNSPrefix, attr.getValue());
				}
			}
			String xsiPrefix = "xsi";
			for (java.util.Iterator it = namespacePrefixes.keySet().iterator(); 
				it.hasNext(); ) {
				String prefix = (String) it.next();
				String ns = (String) namespacePrefixes.get(prefix);
				if ("http://www.w3.org/2001/XMLSchema-instance".equals(ns)) {
					xsiPrefix = prefix;
					break;
				}
			}
			attr = (org.w3c.dom.Attr) attrs.getNamedItem(""+xsiPrefix+":schemaLocation");
			if (attr != null) {
				attrValue = attr.getValue();
				schemaLocation = attrValue;
			}
			readNodeAttributes(node, namespacePrefixes, attrs);
		}
		readNodeChildren(node, namespacePrefixes);
	}

	protected void readNodeAttributes(org.w3c.dom.Node node, java.util.Map namespacePrefixes, org.w3c.dom.NamedNodeMap attrs) {
		org.w3c.dom.Attr attr;
		java.lang.String attrValue;
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("package-path");
		if (attr != null) {
			attrValue = attr.getValue();
			_PackagePath = attrValue;
		}
	}

	protected void readNodeChildren(org.w3c.dom.Node node, java.util.Map namespacePrefixes) {
		org.w3c.dom.NodeList children = node.getChildNodes();
		for (int i = 0, size = children.getLength(); i < size; ++i) {
			org.w3c.dom.Node childNode = children.item(i);
			String childNodeName = (childNode.getLocalName() == null ? childNode.getNodeName().intern() : childNode.getLocalName().intern());
			String childNodeValue = "";
			if (childNode.getFirstChild() != null) {
				childNodeValue = childNode.getFirstChild().getNodeValue();
			}
			boolean recognized = readNodeChild(childNode, childNodeName, childNodeValue, namespacePrefixes);
			if (!recognized) {
				// Found extra unrecognized childNode
			}
		}
	}

	protected boolean readNodeChild(org.w3c.dom.Node childNode, String childNodeName, String childNodeValue, java.util.Map namespacePrefixes) {
		// assert childNodeName == childNodeName.intern()
		if (childNode instanceof org.w3c.dom.Comment) {
			java.lang.String aComments;
			aComments = ((org.w3c.dom.CharacterData)childNode).getData();
			_Comments.add(aComments);
		}
		else if (childNodeName == "author") {
			_Author = childNodeValue;
		}
		else if (childNodeName == "namespace") {
			_Namespace = childNodeValue;
		}
		else if (childNodeName == "entity") {
			Entity aEntity = (Entity) newEntity();
			aEntity._setPropertyChangeSupport(eventListeners);
			aEntity._setParent(this);
			aEntity.readNode(childNode, namespacePrefixes);
			_Entity.add(aEntity);
		}
		else if (childNodeName == "exceptions") {
			_Exceptions = (Exceptions) newExceptions();
			_Exceptions._setPropertyChangeSupport(eventListeners);
			_Exceptions._setParent(this);
			_Exceptions.readNode(childNode, namespacePrefixes);
		}
		else {
			return false;
		}
		return true;
	}

	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
		boolean restrictionFailure = false;
		boolean restrictionPassed = false;
		// Validating property comments
		// Validating property packagePath
		if (getPackagePath() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getPackagePath() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "packagePath", this);	// NOI18N
		}
		// Validating property author
		// Validating property namespace
		if (getNamespace() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getNamespace() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "namespace", this);	// NOI18N
		}
		// Validating property entity
		if (sizeEntity() == 0) {
			throw new org.netbeans.modules.schema2beans.ValidateException("sizeEntity() == 0", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "entity", this);	// NOI18N
		}
		for (int _index = 0; _index < sizeEntity(); ++_index) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity) getEntity(_index);
			if (element != null) {
				((Entity)element).validate();
			}
		}
		// Validating property exceptions
		if (getExceptions() != null) {
			((Exceptions)getExceptions()).validate();
		}
	}

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		if (eventListeners == null) {
			eventListeners = new java.beans.PropertyChangeSupport(this);
		}
		eventListeners.addPropertyChangeListener(listener);
		for (java.util.Iterator it = _Entity.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity)it.next();
			if (element != null) {
				element.addPropertyChangeListener(listener);
			}
		}
		if (_Exceptions != null) {
			_Exceptions.addPropertyChangeListener(listener);
		}
	}

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		for (java.util.Iterator it = _Entity.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity)it.next();
			if (element != null) {
				element.removePropertyChangeListener(listener);
			}
		}
		if (_Exceptions != null) {
			_Exceptions.removePropertyChangeListener(listener);
		}
		if (eventListeners == null) {
			return;
		}
		eventListeners.removePropertyChangeListener(listener);
		if (!eventListeners.hasListeners(null)) {
			eventListeners = null;
		}
	}

	public void _setPropertyChangeSupport(java.beans.PropertyChangeSupport listeners) {
		eventListeners = listeners;
		for (java.util.Iterator it = _Entity.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity)it.next();
			if (element != null) {
				element._setPropertyChangeSupport(listeners);
			}
		}
		if (_Exceptions != null) {
			_Exceptions._setPropertyChangeSupport(listeners);
		}
	}

	public void changePropertyByName(String name, Object value) {
		if (name == null) return;
		name = name.intern();
		if (name == "comments")
			addComments((java.lang.String)value);
		else if (name == "comments[]")
			setComments((java.lang.String[]) value);
		else if (name == "packagePath")
			setPackagePath((java.lang.String)value);
		else if (name == "author")
			setAuthor((String)value);
		else if (name == "namespace")
			setNamespace((String)value);
		else if (name == "entity")
			addEntity((Entity)value);
		else if (name == "entity[]")
			setEntity((Entity[]) value);
		else if (name == "exceptions")
			setExceptions((Exceptions)value);
		else
			throw new IllegalArgumentException(name+" is not a valid property name for ServiceBuilder");
	}

	public Object fetchPropertyByName(String name) {
		if (name == "comments[]")
			return getComments();
		if (name == "packagePath")
			return getPackagePath();
		if (name == "author")
			return getAuthor();
		if (name == "namespace")
			return getNamespace();
		if (name == "entity[]")
			return getEntity();
		if (name == "exceptions")
			return getExceptions();
		throw new IllegalArgumentException(name+" is not a valid property name for ServiceBuilder");
	}

	public static class DocType {
		private org.w3c.dom.NamedNodeMap entities;
		private String internalSubset;
		private String name;
		private org.w3c.dom.NamedNodeMap notations;
		private String publicId;
		private String systemId;

		public DocType(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder.DocType docType) {
			entities = docType.getEntities();
			internalSubset = docType.getInternalSubset();
			name = docType.getName();
			notations = docType.getNotations();
			publicId = docType.getPublicId();
			systemId = docType.getSystemId();
		}

		public DocType(org.w3c.dom.DocumentType docType) {
			entities = docType.getEntities();
			internalSubset = docType.getInternalSubset();
			name = docType.getName();
			notations = docType.getNotations();
			publicId = docType.getPublicId();
			systemId = docType.getSystemId();
		}

		public DocType(String publicId, String systemId) {
			this("service-builder", publicId, systemId);
		}

		public DocType(String name, String publicId, String systemId) {
			this.name = name;
			this.publicId = publicId;
			this.systemId = systemId;
		}

		public org.w3c.dom.NamedNodeMap getEntities() {
			return entities;
		}

		public String getInternalSubset() {
			return internalSubset;
		}

		public String getName() {
			return name;
		}

		public org.w3c.dom.NamedNodeMap getNotations() {
			return notations;
		}

		public String getPublicId() {
			return publicId;
		}

		public String getSystemId() {
			return systemId;
		}

		public String toString() {
			String result = "<!DOCTYPE ";
			result += name;
			if (publicId != null) {
				result += " PUBLIC \"";
				result += publicId;
				result += "\"";
				if (systemId == null) {
					systemId = "SYSTEM";
				}
			}
			if (systemId != null) {
				result += " \"";
				result += systemId;
				result += "\"";
			}
			if (entities != null) {
				int length = entities.getLength();
				if (length > 0) {
					result += " [";
					for (int i = 0; i < length; ++i) {
						org.w3c.dom.Node node = entities.item(i);
						result += "<"+node.getNodeName()+">";
						result += node.getNodeValue();
						result += "</"+node.getNodeName()+">";
					}
					result += "]";
				}
			}
			result += ">";
			return result;
		}
	}

	public String nameSelf() {
		if (parent != null) {
			String parentName = parent.nameSelf();
			String myName = parent.nameChild(this, false, false);
			return parentName + "/" + myName;
		}
		return "/ServiceBuilder";
	}

	public String nameChild(Object childObj) {
		return nameChild(childObj, false, false);
	}

	/**
	 * @param childObj  The child object to search for
	 * @param returnSchemaName  Whether or not the schema name should be returned or the property name
	 * @return null if not found
	 */
	public String nameChild(Object childObj, boolean returnConstName, boolean returnSchemaName) {
		return nameChild(childObj, returnConstName, returnSchemaName, false);
	}

	/**
	 * @param childObj  The child object to search for
	 * @param returnSchemaName  Whether or not the schema name should be returned or the property name
	 * @return null if not found
	 */
	public String nameChild(Object childObj, boolean returnConstName, boolean returnSchemaName, boolean returnXPathName) {
		if (childObj instanceof Exceptions) {
			Exceptions child = (Exceptions) childObj;
			if (child == _Exceptions) {
				if (returnConstName) {
					return EXCEPTIONS;
				} else if (returnSchemaName) {
					return "exceptions";
				} else if (returnXPathName) {
					return "exceptions";
				} else {
					return "Exceptions";
				}
			}
		}
		if (childObj instanceof java.lang.String) {
			java.lang.String child = (java.lang.String) childObj;
			int index = 0;
			for (java.util.Iterator it = _Comments.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COMMENTS;
					} else {
						return "Comments."+Integer.toHexString(index);
					}
				}
				++index;
			}
			if (child == _PackagePath) {
				if (returnConstName) {
					return PACKAGEPATH;
				} else if (returnSchemaName) {
					return "package-path";
				} else if (returnXPathName) {
					return "@package-path";
				} else {
					return "PackagePath";
				}
			}
			if (child == _Author) {
				if (returnConstName) {
					return AUTHOR;
				} else if (returnSchemaName) {
					return "author";
				} else if (returnXPathName) {
					return "author";
				} else {
					return "Author";
				}
			}
			if (child == _Namespace) {
				if (returnConstName) {
					return NAMESPACE;
				} else if (returnSchemaName) {
					return "namespace";
				} else if (returnXPathName) {
					return "namespace";
				} else {
					return "Namespace";
				}
			}
		}
		if (childObj instanceof Entity) {
			Entity child = (Entity) childObj;
			int index = 0;
			for (java.util.Iterator it = _Entity.iterator(); it.hasNext(); 
				) {
				org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity)it.next();
				if (child == element) {
					if (returnConstName) {
						return ENTITY;
					} else if (returnSchemaName) {
						return "entity";
					} else if (returnXPathName) {
						return "entity[position()="+index+"]";
					} else {
						return "Entity."+Integer.toHexString(index);
					}
				}
				++index;
			}
		}
		return null;
	}

	/**
	 * Return an array of all of the properties that are beans and are set.
	 */
	public org.netbeans.modules.schema2beans.BaseBean[] childBeans(boolean recursive) {
		java.util.List children = new java.util.LinkedList();
		childBeans(recursive, children);
		org.netbeans.modules.schema2beans.BaseBean[] result = new org.netbeans.modules.schema2beans.BaseBean[children.size()];
		return (org.netbeans.modules.schema2beans.BaseBean[]) children.toArray(result);
	}

	/**
	 * Put all child beans into the beans list.
	 */
	public void childBeans(boolean recursive, java.util.List beans) {
		for (java.util.Iterator it = _Entity.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity)it.next();
			if (element != null) {
				if (recursive) {
					element.childBeans(true, beans);
				}
				beans.add(element);
			}
		}
		if (_Exceptions != null) {
			if (recursive) {
				_Exceptions.childBeans(true, beans);
			}
			beans.add(_Exceptions);
		}
	}

	public boolean equals(Object o) {
		return o instanceof org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder && equals((org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder) o);
	}

	public boolean equals(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder inst) {
		if (inst == this) {
			return true;
		}
		if (inst == null) {
			return false;
		}
		if (sizeComments() != inst.sizeComments())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _Comments.iterator(), it2 = inst._Comments.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (!(_PackagePath == null ? inst._PackagePath == null : _PackagePath.equals(inst._PackagePath))) {
			return false;
		}
		if (!(_Author == null ? inst._Author == null : _Author.equals(inst._Author))) {
			return false;
		}
		if (!(_Namespace == null ? inst._Namespace == null : _Namespace.equals(inst._Namespace))) {
			return false;
		}
		if (sizeEntity() != inst.sizeEntity())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _Entity.iterator(), it2 = inst._Entity.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity)it.next();
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity element2 = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (!(_Exceptions == null ? inst._Exceptions == null : _Exceptions.equals(inst._Exceptions))) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int result = 17;
		result = 37*result + (_Comments == null ? 0 : _Comments.hashCode());
		result = 37*result + (_PackagePath == null ? 0 : _PackagePath.hashCode());
		result = 37*result + (_Author == null ? 0 : _Author.hashCode());
		result = 37*result + (_Namespace == null ? 0 : _Namespace.hashCode());
		result = 37*result + (_Entity == null ? 0 : _Entity.hashCode());
		result = 37*result + (_Exceptions == null ? 0 : _Exceptions.hashCode());
		return result;
	}

	public void dump(StringBuffer str, String indent) {
		str.append(toString());
	}

	public org.netbeans.modules.schema2beans.BeanProp beanProp(String name) {
		if (name == null) return null;
		org.netbeans.modules.schema2beans.BeanProp prop = (org.netbeans.modules.schema2beans.BeanProp) propByName.get(name);
		if (prop == null) {
			name = name.intern();
			boolean indexed;
			int options;
			String constName;
			String schemaName;
			java.lang.reflect.Method writer = null;
			java.lang.reflect.Method arrayWriter = null;
			java.lang.reflect.Method reader = null;
			java.lang.reflect.Method arrayReader = null;
			java.lang.reflect.Method adder = null;
			java.lang.reflect.Method remover = null;
			try {
				if (name == COMMENTS) {
					indexed = true;
					constName = COMMENTS;
					schemaName = "comment";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_COMMENT;
					reader = getClass().getMethod("getComments", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getComments", new Class[] {});
					writer = getClass().getMethod("setComments", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setComments", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addComments", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeComments", new Class[] {java.lang.String.class});
				} else if (name == PACKAGEPATH) {
					indexed = false;
					constName = PACKAGEPATH;
					schemaName = "package-path";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setPackagePath", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getPackagePath", new Class[] {});
				} else if (name == AUTHOR) {
					indexed = false;
					constName = AUTHOR;
					schemaName = "author";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setAuthor", new Class[] {String.class});
					reader = getClass().getMethod("getAuthor", new Class[] {});
				} else if (name == NAMESPACE) {
					indexed = false;
					constName = NAMESPACE;
					schemaName = "namespace";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setNamespace", new Class[] {String.class});
					reader = getClass().getMethod("getNamespace", new Class[] {});
				} else if (name == ENTITY) {
					indexed = true;
					constName = ENTITY;
					schemaName = "entity";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_1_N|org.netbeans.modules.schema2beans.Common.TYPE_BEAN;
					reader = getClass().getMethod("getEntity", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getEntity", new Class[] {});
					writer = getClass().getMethod("setEntity", new Class[] {Integer.TYPE,  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
            .class});
					arrayWriter = getClass().getMethod("setEntity", new Class[] { 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
            [].class});
					adder = getClass().getMethod("addEntity", new Class[] { 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
            .class});
					remover = getClass().getMethod("removeEntity", new Class[] { 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
            .class});
				} else if (name == EXCEPTIONS) {
					indexed = false;
					constName = EXCEPTIONS;
					schemaName = "exceptions";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_BEAN;
					writer = getClass().getMethod("setExceptions", new Class[] { 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions
            .class});
					reader = getClass().getMethod("getExceptions", new Class[] {});
				} else {
					// Check if name is a schema name.
					if (name == "comment") {
						prop = beanProp(COMMENTS);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "package-path") {
						prop = beanProp(PACKAGEPATH);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "author") {
						prop = beanProp(AUTHOR);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "namespace") {
						prop = beanProp(NAMESPACE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "entity") {
						prop = beanProp(ENTITY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "exceptions") {
						prop = beanProp(EXCEPTIONS);
						propByName.put(name, prop);
						return prop;
					}
					throw new IllegalArgumentException(name+" is not a valid property name for ServiceBuilder");
				}
			} catch (java.lang.NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
			prop = new org.netbeans.modules.schema2beans.ReflectiveBeanProp(this, schemaName, constName, options, getClass(), true, writer, arrayWriter, reader, arrayReader, adder, remover);
			propByName.put(name, prop);
		}
		return prop;
	}

	public org.netbeans.modules.schema2beans.BeanProp beanProp() {
		if (parent == null) {
			org.netbeans.modules.schema2beans.BeanProp prop = (org.netbeans.modules.schema2beans.BeanProp) propByName.get("");
			if (prop == null) {
				prop = new org.netbeans.modules.schema2beans.ReflectiveBeanProp(this, "service-builder", "ServiceBuilder", org.netbeans.modules.schema2beans.Common.TYPE_1 | org.netbeans.modules.schema2beans.Common.TYPE_BEAN, ServiceBuilder.class, isRoot(), null, null, null, null, null, null);
				propByName.put("", prop);
			}
			return prop;
		}
		String myConstName = parent.nameChild(this, true, false);
		return parent.beanProp(myConstName);
	}

	public org.netbeans.modules.schema2beans.BeanProp beanProp(int order) {
		prepareBeanPropList();
		return (org.netbeans.modules.schema2beans.BeanProp) beanPropList.get(order);
	}

	public org.netbeans.modules.schema2beans.BaseBean parent() {
		return (org.netbeans.modules.schema2beans.BaseBean) parent;
	}

	public org.netbeans.modules.schema2beans.Bean _getParent() {
		return parent;
	}

	public org.netbeans.modules.schema2beans.BaseBean newInstance(String name) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String dtdName() {
		if (parent == null) {
			return "service-builder";
		}
		return parent.nameChild(this, false, true);
	}

	public org.w3c.dom.Comment[] comments() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public org.w3c.dom.Comment addComment(String comment) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void removeComment(org.w3c.dom.Comment comment) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void createProperty(String dtdName, String beanName, Class type) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void createProperty(String dtdName, String beanName, int option, Class type) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void createRoot(String dtdName, String beanName, int option, Class type) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public Object[] knownValues(String name) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void addKnownValue(String name, Object value) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void createAttribute(String dtdName, String name, int type, String[] values, String defValue) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void createAttribute(String propName, String dtdName, String name, int type, String[] values, String defValue) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void setAttributeValue(String propName, String name, String value) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void setAttributeValue(String name, String value) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String getAttributeValue(String name) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String getAttributeValue(String propName, String name) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void setAttributeValue(String propName, int index, String name, String value) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String getAttributeValue(String propName, int index, String name) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String[] getAttributeNames(String propName) {
		return new String[] {};
	}

	public String[] getAttributeNames() {
		return new String[] {};
	}

	public org.netbeans.modules.schema2beans.BaseAttribute[] listAttributes(String propName) {
		return new org.netbeans.modules.schema2beans.BaseAttribute[] {};
	}

	public org.netbeans.modules.schema2beans.BaseAttribute[] listAttributes() {
		return new org.netbeans.modules.schema2beans.BaseAttribute[] {};
	}

	public String[] findAttributeValue(String attrName, String value) {
		return new String[] {};
	}

	public String[] findPropertyValue(String propName, Object value) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String[] findValue(Object value) {
		throw new UnsupportedOperationException("Not implemented");
	}

	protected void buildPathName(StringBuffer str) {
		str.append(nameSelf());
	}

	public org.netbeans.modules.schema2beans.GraphManager graphManager() {
		if (graphManager == null) {
			if (parent == null) {
				graphManager = new org.netbeans.modules.schema2beans.GraphManager(this);
			} else {
				graphManager = parent.graphManager();
			}
		}
		return graphManager;
	}

	public Object clone() {
		return new ServiceBuilder(this, null, false);
	}

	public Object cloneData() {
		return new ServiceBuilder(this, null, true);
	}

	private void prepareBeanPropList() {
		if (beanPropList == null) {
			beanPropList = new java.util.ArrayList(6);
			beanPropList.add(beanProp(COMMENTS));
			beanPropList.add(beanProp(PACKAGEPATH));
			beanPropList.add(beanProp(AUTHOR));
			beanPropList.add(beanProp(NAMESPACE));
			beanPropList.add(beanProp(ENTITY));
			beanPropList.add(beanProp(EXCEPTIONS));
		}
	}

	protected java.util.Iterator beanPropsIterator() {
		prepareBeanPropList();
		return beanPropList.iterator();
	}

	public org.netbeans.modules.schema2beans.BeanProp[] beanProps() {
		prepareBeanPropList();
		org.netbeans.modules.schema2beans.BeanProp[] ret = new org.netbeans.modules.schema2beans.BeanProp[6];
		ret = (org.netbeans.modules.schema2beans.BeanProp[]) beanPropList.toArray(ret);
		return ret;
	}

	public void setValue(String name, Object value) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			setComments((java.lang.String[]) value);
		} else if (name == PACKAGEPATH || name == "package-path") {
			setPackagePath((java.lang.String)value);
		} else if (name == AUTHOR || name == "author") {
			setAuthor((String)value);
		} else if (name == NAMESPACE || name == "namespace") {
			setNamespace((String)value);
		} else if (name == ENTITY || name == "entity") {
			setEntity((Entity[]) value);
		} else if (name == EXCEPTIONS || name == "exceptions") {
			setExceptions((Exceptions)value);
		} else throw new IllegalArgumentException(name+" is not a valid property name for ServiceBuilder");
	}

	public void setValue(String name, int index, Object value) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			setComments(index, (java.lang.String)value);
		} else if (name == PACKAGEPATH || name == "package-path") {
			throw new IllegalArgumentException(name+" is not an indexed property for ServiceBuilder");
		} else if (name == AUTHOR || name == "author") {
			throw new IllegalArgumentException(name+" is not an indexed property for ServiceBuilder");
		} else if (name == NAMESPACE || name == "namespace") {
			throw new IllegalArgumentException(name+" is not an indexed property for ServiceBuilder");
		} else if (name == ENTITY || name == "entity") {
			setEntity(index, (Entity)value);
		} else if (name == EXCEPTIONS || name == "exceptions") {
			throw new IllegalArgumentException(name+" is not an indexed property for ServiceBuilder");
		} else throw new IllegalArgumentException(name+" is not a valid property name for ServiceBuilder");
	}

	public Object getValue(String name) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			return getComments();
		} else if (name == PACKAGEPATH || name == "package-path") {
			return getPackagePath();
		} else if (name == AUTHOR || name == "author") {
			return getAuthor();
		} else if (name == NAMESPACE || name == "namespace") {
			return getNamespace();
		} else if (name == ENTITY || name == "entity") {
			return getEntity();
		} else if (name == EXCEPTIONS || name == "exceptions") {
			return getExceptions();
		} else throw new IllegalArgumentException(name+" is not a valid property name for ServiceBuilder");
	}

	public Object getValue(String name, int index) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			return getComments(index);
		} else if (name == ENTITY || name == "entity") {
			return getEntity(index);
		} else if (name == PACKAGEPATH || name == "package-path") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getPackagePath();
		} else if (name == AUTHOR || name == "author") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getAuthor();
		} else if (name == NAMESPACE || name == "namespace") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getNamespace();
		} else if (name == EXCEPTIONS || name == "exceptions") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getExceptions();
		} else throw new IllegalArgumentException(name+" is not a valid property name for ServiceBuilder");
	}

	public void mergeUpdate(org.netbeans.modules.schema2beans.BaseBean sourceBean) {
		org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder source = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.ServiceBuilder) sourceBean;
		{
			java.lang.String[] srcProperty = source.getComments();
			setComments(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getPackagePath();
			setPackagePath(srcProperty);
		}
		{
			String srcProperty = source.getAuthor();
			setAuthor(srcProperty);
		}
		{
			String srcProperty = source.getNamespace();
			setNamespace(srcProperty);
		}
		{
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity[] srcProperty = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity[]) source.getEntity();
			int destSize = sizeEntity();
			if (destSize == srcProperty.length) {
				for (int i = 0; i < srcProperty.length; ++i) {
					org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity dest;
					if (srcProperty[i] == null) {
						dest = null;
					} else {
						if (i < destSize) {
							dest = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity) getEntity(i);
						} else {
							dest = null;
						}
						if (dest == null) {
							// Use a temp variable, and store it after we've merged everything into it, so as to make it only 1 change event.
							dest = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity(srcProperty[i], this, false);
						} else {
							dest.mergeUpdate(srcProperty[i]);
						}
					}
					// Merge events were generated by the above dest.mergeUpdate, so just set it directly now.
					_Entity.set(i, dest);
				}
			} else {
				org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity[] destArray = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity[srcProperty.length];
				for (int i = 0; i < srcProperty.length; ++i) {
					org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity dest;
					if (srcProperty[i] == null) {
						dest = null;
					} else {
						if (i < destSize) {
							dest = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity) getEntity(i);
							if (!srcProperty[i].equals(dest)) {
								// It's different, so have it just dup the source one.
								dest = null;
							}
						} else {
							dest = null;
						}
						if (dest == null) {
							// Use a temp variable, and store it after we've merged everything into it, so as to make it only 1 change event.
							dest = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity(srcProperty[i], this, false);
						}
						destArray[i] = dest;
					}
				}
				setEntity(destArray);
			}
		}
		{
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Exceptions srcProperty = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Exceptions) source.getExceptions();
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Exceptions dest;
			boolean needToSet = false;
			if (srcProperty == null) {
				dest = null;
				needToSet = true;
			} else {
				dest = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Exceptions) getExceptions();
				if (dest == null) {
					// Use a temp variable, and store it after we've merged everything into it, so as to make it only 1 change event.
					dest = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Exceptions(srcProperty, this, false);
					needToSet = true;
				} else {
					dest.mergeUpdate(srcProperty);
				}
			}
			if (needToSet) {
				setExceptions(dest);
			}
		}
	}

	public boolean isRoot() {
		return true;
	}

	public static ServiceBuilder createGraph(java.io.InputStream in, boolean validate) throws java.io.IOException, javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException {
		return read(new org.xml.sax.InputSource(in), validate, null, null);
	}

	public static ServiceBuilder createGraph(java.io.InputStream in) throws java.io.IOException, javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException {
		return createGraph(in, false);
	}

	public static ServiceBuilder createGraph() {
		return new ServiceBuilder();
	}

	public static ServiceBuilder createGraph(org.w3c.dom.Document document) {
		return read(document);
	}


	    
        
}


/*
		The following schema file has been used for generation:

<!--
This is the DTD for the Service Builder parameters for Liferay Portal.

<!DOCTYPE service-builder PUBLIC
	"-//Liferay//DTD Service Builder 5.1.0//EN"
	"http://www.liferay.com/dtd/liferay-service-builder_5_1_0.dtd">
-->

<!--
The service-builder element is the root of the deployment descriptor for
a Service Builder descriptor that is used to generate services available to
portlets. The Service Builder saves the developer time by generating Spring
utilities, SOAP utilities, and Hibernate persistence classes to ease the
development of services.
-->
<!ELEMENT service-builder (author?, namespace, entity+, exceptions?)>

<!--
The package-path value specifies the package of the generated code.
-->
<!ATTLIST service-builder
	package-path CDATA #REQUIRED
>

<!--
The author element is the name of the user associated with the generated code.
-->
<!ELEMENT author (#PCDATA)>

<!--
The namespace element must be a unique namespace for this component. Table names
will be prepended with this namespace. Generated JSON JavaScript will be scoped
to this namespace as well (i.e., Liferay.Service.Test.* if the namespace is
Test).
-->
<!ELEMENT namespace (#PCDATA)>

<!--
An entity usually represents a business facade and a table in the database. If
an entity does not have any columns, then it only represents a business facade.
The Service Builder will always generate an empty business facade POJO if it
does not exist. Upon subsequent generations, the Service Builder will check to
see if the business facade already exists. If it exists and has additional
methods, then the Service Builder will also update the SOAP wrappers.

If an entity does have columns, then the value object, the POJO class that
is mapped to the database, and other persistence utilities are also generated
based on the order and finder elements.
-->
<!ELEMENT entity (column*, order?, finder*, reference*, tx-required*)>

<!--
The name value specifies the name of the entity.

The table value specifies the name of the table that this entity maps to in the
database. If this value is not set, then the name of the table is the same as
the name of the entity name.

If the uuid value is true, then the service will generate a UUID column for the
service. This column will automatically be populated with a UUID. Developers
will also be able to find and remove based on that UUID. The default value is
false.

If the local-service value is true, then the service will generate the local
interfaces for the service. The default value is false.

If the remote-service value is true, then the service will generate remote
interfaces for the service. The default value is true.

The persistence-class value specifies the name of your custom persistence class.
This class must implmeent the generated persistence interface or extend the
generated persistence class. This allows you to override default behavior
without modifying the generated persistence class.

You can generate classes to use a custom data source and session factory.
Point "spring.configs" in portal.properties to load your custom Spring XML with
the defintions of your custom data source and session factory. Then set the
data-source and session-factory values to your custom values.

The data-source value specifies the data source target that is set to the
persistence class. The default value is the Liferay data source. This is used in
conjunction with session-factory. See data-source-spring.xml.

The session-factory value specifies the session factory that is set to the
persistence class. The default value is the Liferay session factory. This is
used in conjunction with data-source. See data-source-spring.xml.

The tx-manager value specifies the transaction manager that Spring uses. The
default value is the Spring Hibernate transaction manager that wraps the Liferay
data source and session factory. See data-source-spring.xml. Set this attribute
to "none" to disable transaction management.

The cache-enabled value specifies whether or not to cache this queries for this
entity. Set this to false if data in the table will be updated by other
programs. The default value is true.
-->
<!ATTLIST entity
	name CDATA #REQUIRED
	table CDATA #IMPLIED
	uuid CDATA #IMPLIED
	local-service CDATA #IMPLIED
	remote-service CDATA #IMPLIED
	persistence-class CDATA #IMPLIED
	data-source CDATA #IMPLIED
	session-factory CDATA #IMPLIED
	tx-manager CDATA #IMPLIED
	cache-enabled CDATA #IMPLIED
>

<!--
The column element represents a column in the database.
-->
<!ELEMENT column (#PCDATA)>

<!--
The name value specifies the getter and setter name in the entity.

The type value specifies whether the column is a String, Boolean, or int, etc.

For example:

<column name="companyId" db-name="companyId" type="String" />

The above column specifies that there will be a getter called
pojo.getCompanyId() that will return a String.

Set db-name to map the field to a physical database column that is different
from the column name.

If the primary value is set to true, then this column is part of the primary key
of the entity. If multiple columns have the primary value set to true, then a
compound key will be created.

See com.liferay.portal.service.persistence.LayoutPK for an example of a compound
primary key.

If the entity and mapping-key attributes are specified and mapping-table is not,
then the Service Builder will assume you are specifying a one to many
relationship.

For example:

<column
	name="shoppingItemPrices"
	type="Collection"
	entity="ShoppingItemPrice"
	mapping-key="itemId"
/>

The above column specifies that there will be a getter called
pojo.getShoppingItemPrices() that will return a collection. It will map to a
column called itemId in the table that maps to the entity ShoppingItemPrice.

If the entity and mapping-table attributes are specified and mapping-key is not,
then the Service Builder will assume you are specifying a many to many
relationship.

For example:

<column
	name="roles"
	type="Collection"
	entity="Role"
	mapping-table="Groups_Roles"
/>

The above column specifies that there will be a getter called
pojo.getRoles() that will return a collection. It will use a mapping table
called Groups_Roles to give a many to many relationship between groups and
roles.

The id-type and id-param values are used in order to create an auto-generated, 
auto-incrementing primary key when inserting records into a table. This can be 
implemented in 4 different ways, depending on the type of database being used. 
In all cases, the primary key of the model object should be assigned a value of 
null, and hibernate will know to replace the null value with an auto-generated, 
auto-incremented value. If no id-type value is used, it is assumed that the 
primary key will be assigned and not auto-generated.

The first implementation uses a class to generate a primary key. 

For example:

<column 
	name="id" 
	type="Integer" 
	primary="true" 
	id-type="class" 
	id-param="com.liferay.counter.service.persistence.IDGenerator" 
/>

In this implementation, the class specified in the id-param value will be called
to retrieve a unique identifier (in the example above, an Integer) that will be 
used as the primary key for the new record. This implementation works for all 
supported databases.

The second implementation generates identifiers that are unique only when no 
other process is inserting data into the same table. This implementation should
NOT be used in a clustered environment, but it does work for all supported 
databases.

For example:

<column 
	name="id" 
	type="Integer" 
	primary="true" 
	id-type="increment" 
/>

The third implementation uses an identity column to generate a primary key.

For example:

<column 
	name="id" 
	type="Integer" 
	primary="true" 
	id-type="identity" 
/>

In this implementation, the create table SQL generated for this entity will 
create an identity column that natively auto-generates a primary key whenever
an insert occurs. This implementation is only supported by DB2, MySQL, and
MS SQL Server.

The fourth implementation uses a sequence to generate a primary key.

For example:

<column 
	name="id" 
	type="Integer" 
	primary="true" 
	id-type="sequence" 
	id-param="id_sequence" 
/>

In this implementation, a create sequence SQL statement is created based on
the id-param value (stored in /sql/sequences.sql). This sequence is then 
accessed to generate a unique identifier whenever an insert occurs. This
implementation is only supported by DB2, Oracle, PostgreSQL, and SAP DB.

The convert-null value specifies whether or not the column value is
automatically converted to a non null value if it is null. This only applies if
the type value is String. This is particularly useful if your entity is
referencing a read only table or a database view so that Hibernate does not try
to issue unnecessary updates. The default value is true.
-->
<!ATTLIST column
	name CDATA #REQUIRED
	db-name CDATA #IMPLIED
	type CDATA #REQUIRED
	primary CDATA #IMPLIED
	entity CDATA #IMPLIED
	mapping-key CDATA #IMPLIED
	mapping-table CDATA #IMPLIED
	id-type CDATA #IMPLIED
	id-param CDATA #IMPLIED
	convert-null CDATA #IMPLIED
>

<!--
The order element specifies a default ordering and sorting of the entities when
they are retrieved from the database.
-->
<!ELEMENT order (order-column+)>

<!--
Set the by attribute to "asc" or "desc" to order by ascending or descending.
-->
<!ATTLIST order
	by CDATA #IMPLIED
>

<!--
The order-column element allows you to order the entities by specific columns.
-->
<!ELEMENT order-column (#PCDATA)>

<!--
The attributes of the order-column element allows you to fine tune the ordering
of the entity.

For example:

<order by="asc">
	<order-column name="parentLayoutId" />
	<order-column name="priority" />
</order>

The above settings will order by parentLayoutId and then by priority in an
ascending manner.

For example:

<order by="asc">
	<order-column name="name" case-sensitive="false" />
</order>

The above settings will order by name and will not be case sensitive.

For example:

<order>
	<order-column name="articleId" order-by="asc" />
	<order-column name="version" order-by="desc" />
</order>

The above settings will order by articleId in an ascending manner and then by
version in a descending manner.
-->
<!ATTLIST order-column
	name CDATA #REQUIRED
	case-sensitive CDATA #IMPLIED
	order-by CDATA #IMPLIED
>

<!--
The finder element represents a generated finder method.
-->
<!ELEMENT finder (finder-column+)>

<!--
-->
<!ATTLIST finder
	name CDATA #REQUIRED
	return-type CDATA #REQUIRED
	where CDATA #IMPLIED
	db-index CDATA #IMPLIED
>

<!--
The finder-column element specifies the columns to find by.
-->
<!ELEMENT finder-column (#PCDATA)>

<!--
The name value specifies the name of the finder method.

For example:

<finder name="CompanyId" return-type="Collection">
	<finder-column name="companyId" />
</finder>

The above settings will create a finder with the name findByCompanyId that will
return a Collection and require a given companyId. It will also generate
several more findByCompanyId methods that take in pagination fields (int begin,
int end) and more sorting options. The easiest way to understand this is to
look at a generated PersistenceImpl class. The Service Builder will also
generate removeByCompanyId and countByCompanyId.

See com.liferay.portal.service.persistence.LayoutPersistenceImpl for a good
example.

The attribute comparator takes in the values =, !=, <, <=, >, >=, or LIKE and is
used to compare this column.

The attribute case-sensitive is a boolean value and is only used if the column
is a String value.
-->
<!ATTLIST finder-column
	name CDATA #REQUIRED
	case-sensitive CDATA #IMPLIED
	comparator CDATA #IMPLIED
>

<!--
The reference element allows you to inject services from another service.xml
within the same class loader. For example, if you inject the Resource entity,
then you'll be able to reference the Resource services from your service
implementation via the methods getResourceLocalService and getResourceService.
You'll also be able to reference the Resource services via the variables
resourceLocalService and resourceService.
-->
<!ELEMENT reference (#PCDATA)>

<!--
See the comments in reference element.
-->
<!ATTLIST reference
	package-path CDATA #IMPLIED
	entity CDATA #IMPLIED
>

<!--
The tx-required element has a text value that will be used to match method names
that require transactions. By default, the methods: add*, check*, clear*,
delete*, set*, and update* require propagation of transactions. All other
methods support transactions but are assumed to be read only. If you want
additional methods to fall under transactions, add the method name to this
element.
-->
<!ELEMENT tx-required (#PCDATA)>

<!--
The exceptions element contain a list of generated exceptions. This doesn't save
a lot of typing, but can still be helpful.
-->
<!ELEMENT exceptions (exception*)>

<!--
See the comments in exceptions element.
-->
<!ELEMENT exception (#PCDATA)>
*/
