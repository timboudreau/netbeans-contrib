/**
 *	This generated bean class Entity
 *	matches the schema element 'entity'.
 *  The root bean class is ServiceBuilder
 *
 * @Generated
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl;

public class Entity extends org.netbeans.modules.schema2beans.BaseBean
 implements  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity
            , org.netbeans.modules.schema2beans.Bean {
	public static final String COMMENTS = "Comments";	// NOI18N
	public static final String NAME = "Name";	// NOI18N
	public static final String TABLE = "Table";	// NOI18N
	public static final String UUID = "Uuid";	// NOI18N
	public static final String LOCALSERVICE = "LocalService";	// NOI18N
	public static final String REMOTESERVICE = "RemoteService";	// NOI18N
	public static final String PERSISTENCECLASS = "PersistenceClass";	// NOI18N
	public static final String DATASOURCE = "DataSource";	// NOI18N
	public static final String SESSIONFACTORY = "SessionFactory";	// NOI18N
	public static final String TXMANAGER = "TxManager";	// NOI18N
	public static final String CACHEENABLED = "CacheEnabled";	// NOI18N
	public static final String COLUMN = "Column";	// NOI18N
	public static final String COLUMNNAME = "ColumnName";	// NOI18N
	public static final String COLUMNDBNAME = "ColumnDbName";	// NOI18N
	public static final String COLUMNTYPE = "ColumnType";	// NOI18N
	public static final String COLUMNPRIMARY = "ColumnPrimary";	// NOI18N
	public static final String COLUMNENTITY = "ColumnEntity";	// NOI18N
	public static final String COLUMNMAPPINGKEY = "ColumnMappingKey";	// NOI18N
	public static final String COLUMNMAPPINGTABLE = "ColumnMappingTable";	// NOI18N
	public static final String COLUMNIDTYPE = "ColumnIdType";	// NOI18N
	public static final String COLUMNIDPARAM = "ColumnIdParam";	// NOI18N
	public static final String COLUMNCONVERTNULL = "ColumnConvertNull";	// NOI18N
	public static final String ORDER = "Order";	// NOI18N
	public static final String FINDER = "Finder";	// NOI18N
	public static final String REFERENCE = "Reference";	// NOI18N
	public static final String REFERENCEPACKAGEPATH = "ReferencePackagePath";	// NOI18N
	public static final String REFERENCEENTITY = "ReferenceEntity";	// NOI18N
	public static final String TX_REQUIRED = "TxRequired";	// NOI18N

	private static final org.netbeans.modules.schema2beans.Version baseBeanRuntimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);
	private java.util.List _Comments = new java.util.ArrayList();	// List<java.lang.String>
	private java.lang.String _Name;
	private java.lang.String _Table;
	private java.lang.String _Uuid;
	private java.lang.String _LocalService;
	private java.lang.String _RemoteService;
	private java.lang.String _PersistenceClass;
	private java.lang.String _DataSource;
	private java.lang.String _SessionFactory;
	private java.lang.String _TxManager;
	private java.lang.String _CacheEnabled;
	private java.util.List _Column = new java.util.ArrayList();	// List<String>
	private java.util.List _ColumnName = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ColumnDbName = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ColumnType = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ColumnPrimary = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ColumnEntity = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ColumnMappingKey = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ColumnMappingTable = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ColumnIdType = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ColumnIdParam = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ColumnConvertNull = new java.util.ArrayList();	// List<java.lang.String>
	private Order _Order;
	private java.util.List _Finder = new java.util.ArrayList();	// List<Finder>
	private java.util.List _Reference = new java.util.ArrayList();	// List<String>
	private java.util.List _ReferencePackagePath = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _ReferenceEntity = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _TxRequired = new java.util.ArrayList();	// List<String>
	private org.netbeans.modules.schema2beans.BaseBean parent;
	private java.beans.PropertyChangeSupport eventListeners;
	private java.util.Map propByName = new java.util.HashMap(30, 1.0f);
	private java.util.List beanPropList = null;	// List<org.netbeans.modules.schema2beans.BeanProp>

	/**
	 * Normal starting point constructor.
	 */
	public Entity() {
		this(null, baseBeanRuntimeVersion);
	}

	/**
	 * This constructor is here for BaseBean compatibility.
	 */
	public Entity(java.util.Vector comps, org.netbeans.modules.schema2beans.Version baseBeanRuntimeVersion) {
		super(null, baseBeanRuntimeVersion);
		_Name = "";
	}

	/**
	 * Required parameters constructor
	 */
	public Entity(java.lang.String name) {
		super(null, baseBeanRuntimeVersion);
		_Name = name;
	}

	/**
	 * Deep copy
	 */
	public Entity(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity source) {
		this(source, false);
	}

	/**
	 * Deep copy
	 * @param justData just copy the XML relevant data
	 */
	public Entity(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity source, boolean justData) {
		this(source, null, justData);
	}

	/**
	 * Deep copy
	 */
	public Entity(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData) {
		super(null, baseBeanRuntimeVersion);
		this.parent = parent;
		for (java.util.Iterator it = source._Comments.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_Comments.add(srcElement);
		}
		_Name = source._Name;
		_Table = source._Table;
		_Uuid = source._Uuid;
		_LocalService = source._LocalService;
		_RemoteService = source._RemoteService;
		_PersistenceClass = source._PersistenceClass;
		_DataSource = source._DataSource;
		_SessionFactory = source._SessionFactory;
		_TxManager = source._TxManager;
		_CacheEnabled = source._CacheEnabled;
		for (java.util.Iterator it = source._Column.iterator(); 
			it.hasNext(); ) {
			String srcElement = (String)it.next();
			_Column.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnName.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnName.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnDbName.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnDbName.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnType.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnType.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnPrimary.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnPrimary.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnEntity.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnEntity.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnMappingKey.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnMappingKey.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnMappingTable.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnMappingTable.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnIdType.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnIdType.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnIdParam.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnIdParam.add(srcElement);
		}
		for (java.util.Iterator it = source._ColumnConvertNull.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ColumnConvertNull.add(srcElement);
		}
		_Order = (source._Order == null) ? null : (Order) newOrder(( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order
            ) source._Order, this, justData);
		for (java.util.Iterator it = source._Finder.iterator(); 
			it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder srcElement = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder)it.next();
			_Finder.add((srcElement == null) ? null : (Finder) newFinder(( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
            ) srcElement, this, justData));
		}
		for (java.util.Iterator it = source._Reference.iterator(); 
			it.hasNext(); ) {
			String srcElement = (String)it.next();
			_Reference.add(srcElement);
		}
		for (java.util.Iterator it = source._ReferencePackagePath.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ReferencePackagePath.add(srcElement);
		}
		for (java.util.Iterator it = source._ReferenceEntity.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_ReferenceEntity.add(srcElement);
		}
		for (java.util.Iterator it = source._TxRequired.iterator(); 
			it.hasNext(); ) {
			String srcElement = (String)it.next();
			_TxRequired.add(srcElement);
		}
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
	public void setName(java.lang.String value) {
		if (value == null ? _Name == null : value.equals(_Name)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Name", getName(), value);
		}
		_Name = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getName() {
		return _Name;
	}

	// This attribute is optional
	public void setTable(java.lang.String value) {
		if (value == null ? _Table == null : value.equals(_Table)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Table", getTable(), value);
		}
		_Table = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getTable() {
		return _Table;
	}

	// This attribute is optional
	public void setUuid(java.lang.String value) {
		if (value == null ? _Uuid == null : value.equals(_Uuid)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Uuid", getUuid(), value);
		}
		_Uuid = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getUuid() {
		return _Uuid;
	}

	// This attribute is optional
	public void setLocalService(java.lang.String value) {
		if (value == null ? _LocalService == null : value.equals(_LocalService)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/LocalService", getLocalService(), value);
		}
		_LocalService = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getLocalService() {
		return _LocalService;
	}

	// This attribute is optional
	public void setRemoteService(java.lang.String value) {
		if (value == null ? _RemoteService == null : value.equals(_RemoteService)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/RemoteService", getRemoteService(), value);
		}
		_RemoteService = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getRemoteService() {
		return _RemoteService;
	}

	// This attribute is optional
	public void setPersistenceClass(java.lang.String value) {
		if (value == null ? _PersistenceClass == null : value.equals(_PersistenceClass)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/PersistenceClass", getPersistenceClass(), value);
		}
		_PersistenceClass = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getPersistenceClass() {
		return _PersistenceClass;
	}

	// This attribute is optional
	public void setDataSource(java.lang.String value) {
		if (value == null ? _DataSource == null : value.equals(_DataSource)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/DataSource", getDataSource(), value);
		}
		_DataSource = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getDataSource() {
		return _DataSource;
	}

	// This attribute is optional
	public void setSessionFactory(java.lang.String value) {
		if (value == null ? _SessionFactory == null : value.equals(_SessionFactory)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/SessionFactory", getSessionFactory(), value);
		}
		_SessionFactory = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getSessionFactory() {
		return _SessionFactory;
	}

	// This attribute is optional
	public void setTxManager(java.lang.String value) {
		if (value == null ? _TxManager == null : value.equals(_TxManager)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/TxManager", getTxManager(), value);
		}
		_TxManager = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getTxManager() {
		return _TxManager;
	}

	// This attribute is optional
	public void setCacheEnabled(java.lang.String value) {
		if (value == null ? _CacheEnabled == null : value.equals(_CacheEnabled)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/CacheEnabled", getCacheEnabled(), value);
		}
		_CacheEnabled = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getCacheEnabled() {
		return _CacheEnabled;
	}

	// This attribute is an array, possibly empty
	public void setColumn(String[] value) {
		if (value == null)
			value = new String[0];
		if (value.length == sizeColumn()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumn(i) == null : value[i].equals(getColumn(i)))) {
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
			int oldSize = sizeColumn();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumn(oldIndex) == null : value[newIndex].equals(getColumn(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumn(oldIndex+1) == null : value[newIndex].equals(getColumn(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumn(oldIndex) == null : value[newIndex+1].equals(getColumn(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Column."+Integer.toHexString(addIndex), null, value[addIndex]);
				_Column.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Column."+Integer.toHexString(removeIndex), getColumn(removeIndex), null);
				_Column.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Column.-1", getColumn(), value);
			}
		}
		_Column.clear();
		((java.util.ArrayList) _Column).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_Column.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumn(int index, String value) {
		if (value == null ? getColumn(index) == null : value.equals(getColumn(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Column."+Integer.toHexString(index), getColumn(index), value);
			eventListeners.firePropertyChange(event);
		}
		_Column.set(index, value);
	}

	public String[] getColumn() {
		String[] arr = new String[_Column.size()];
		return (String[]) _Column.toArray(arr);
	}

	public java.util.List fetchColumnList() {
		return _Column;
	}

	public String getColumn(int index) {
		return (String)_Column.get(index);
	}

	// Return the number of column
	public int sizeColumn() {
		return _Column.size();
	}

	public int addColumn(String value) {
		_Column.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Column."+Integer.toHexString(_Column.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _Column.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumn(String value) {
		int pos = _Column.indexOf(value);
		if (pos >= 0) {
			_Column.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Column."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnName(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnName()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnName(i) == null : value[i].equals(getColumnName(i)))) {
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
			int oldSize = sizeColumnName();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnName(oldIndex) == null : value[newIndex].equals(getColumnName(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnName(oldIndex+1) == null : value[newIndex].equals(getColumnName(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnName(oldIndex) == null : value[newIndex+1].equals(getColumnName(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnName."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnName.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnName."+Integer.toHexString(removeIndex), getColumnName(removeIndex), null);
				_ColumnName.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnName.-1", getColumnName(), value);
			}
		}
		_ColumnName.clear();
		((java.util.ArrayList) _ColumnName).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnName.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnName(int index, java.lang.String value) {
		if (value == null ? getColumnName(index) == null : value.equals(getColumnName(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnName."+Integer.toHexString(index), getColumnName(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnName.size(); index >= size; ++size) {
			_ColumnName.add(null);
		}
		_ColumnName.set(index, value);
	}

	public java.lang.String[] getColumnName() {
		java.lang.String[] arr = new java.lang.String[_ColumnName.size()];
		return (java.lang.String[]) _ColumnName.toArray(arr);
	}

	public java.util.List fetchColumnNameList() {
		return _ColumnName;
	}

	public java.lang.String getColumnName(int index) {
		return (java.lang.String)_ColumnName.get(index);
	}

	// Return the number of columnName
	public int sizeColumnName() {
		return _ColumnName.size();
	}

	public int addColumnName(java.lang.String value) {
		_ColumnName.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnName."+Integer.toHexString(_ColumnName.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnName.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnName(java.lang.String value) {
		int pos = _ColumnName.indexOf(value);
		if (pos >= 0) {
			_ColumnName.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnName."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnDbName(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnDbName()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnDbName(i) == null : value[i].equals(getColumnDbName(i)))) {
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
			int oldSize = sizeColumnDbName();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnDbName(oldIndex) == null : value[newIndex].equals(getColumnDbName(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnDbName(oldIndex+1) == null : value[newIndex].equals(getColumnDbName(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnDbName(oldIndex) == null : value[newIndex+1].equals(getColumnDbName(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnDbName."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnDbName.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnDbName."+Integer.toHexString(removeIndex), getColumnDbName(removeIndex), null);
				_ColumnDbName.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnDbName.-1", getColumnDbName(), value);
			}
		}
		_ColumnDbName.clear();
		((java.util.ArrayList) _ColumnDbName).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnDbName.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnDbName(int index, java.lang.String value) {
		if (value == null ? getColumnDbName(index) == null : value.equals(getColumnDbName(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnDbName."+Integer.toHexString(index), getColumnDbName(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnDbName.size(); index >= size; ++size) {
			_ColumnDbName.add(null);
		}
		_ColumnDbName.set(index, value);
	}

	public java.lang.String[] getColumnDbName() {
		java.lang.String[] arr = new java.lang.String[_ColumnDbName.size()];
		return (java.lang.String[]) _ColumnDbName.toArray(arr);
	}

	public java.util.List fetchColumnDbNameList() {
		return _ColumnDbName;
	}

	public java.lang.String getColumnDbName(int index) {
		return (java.lang.String)_ColumnDbName.get(index);
	}

	// Return the number of columnDbName
	public int sizeColumnDbName() {
		return _ColumnDbName.size();
	}

	public int addColumnDbName(java.lang.String value) {
		_ColumnDbName.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnDbName."+Integer.toHexString(_ColumnDbName.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnDbName.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnDbName(java.lang.String value) {
		int pos = _ColumnDbName.indexOf(value);
		if (pos >= 0) {
			_ColumnDbName.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnDbName."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnType(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnType()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnType(i) == null : value[i].equals(getColumnType(i)))) {
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
			int oldSize = sizeColumnType();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnType(oldIndex) == null : value[newIndex].equals(getColumnType(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnType(oldIndex+1) == null : value[newIndex].equals(getColumnType(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnType(oldIndex) == null : value[newIndex+1].equals(getColumnType(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnType."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnType.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnType."+Integer.toHexString(removeIndex), getColumnType(removeIndex), null);
				_ColumnType.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnType.-1", getColumnType(), value);
			}
		}
		_ColumnType.clear();
		((java.util.ArrayList) _ColumnType).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnType.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnType(int index, java.lang.String value) {
		if (value == null ? getColumnType(index) == null : value.equals(getColumnType(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnType."+Integer.toHexString(index), getColumnType(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnType.size(); index >= size; ++size) {
			_ColumnType.add(null);
		}
		_ColumnType.set(index, value);
	}

	public java.lang.String[] getColumnType() {
		java.lang.String[] arr = new java.lang.String[_ColumnType.size()];
		return (java.lang.String[]) _ColumnType.toArray(arr);
	}

	public java.util.List fetchColumnTypeList() {
		return _ColumnType;
	}

	public java.lang.String getColumnType(int index) {
		return (java.lang.String)_ColumnType.get(index);
	}

	// Return the number of columnType
	public int sizeColumnType() {
		return _ColumnType.size();
	}

	public int addColumnType(java.lang.String value) {
		_ColumnType.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnType."+Integer.toHexString(_ColumnType.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnType.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnType(java.lang.String value) {
		int pos = _ColumnType.indexOf(value);
		if (pos >= 0) {
			_ColumnType.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnType."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnPrimary(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnPrimary()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnPrimary(i) == null : value[i].equals(getColumnPrimary(i)))) {
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
			int oldSize = sizeColumnPrimary();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnPrimary(oldIndex) == null : value[newIndex].equals(getColumnPrimary(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnPrimary(oldIndex+1) == null : value[newIndex].equals(getColumnPrimary(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnPrimary(oldIndex) == null : value[newIndex+1].equals(getColumnPrimary(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnPrimary."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnPrimary.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnPrimary."+Integer.toHexString(removeIndex), getColumnPrimary(removeIndex), null);
				_ColumnPrimary.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnPrimary.-1", getColumnPrimary(), value);
			}
		}
		_ColumnPrimary.clear();
		((java.util.ArrayList) _ColumnPrimary).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnPrimary.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnPrimary(int index, java.lang.String value) {
		if (value == null ? getColumnPrimary(index) == null : value.equals(getColumnPrimary(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnPrimary."+Integer.toHexString(index), getColumnPrimary(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnPrimary.size(); index >= size; ++size) {
			_ColumnPrimary.add(null);
		}
		_ColumnPrimary.set(index, value);
	}

	public java.lang.String[] getColumnPrimary() {
		java.lang.String[] arr = new java.lang.String[_ColumnPrimary.size()];
		return (java.lang.String[]) _ColumnPrimary.toArray(arr);
	}

	public java.util.List fetchColumnPrimaryList() {
		return _ColumnPrimary;
	}

	public java.lang.String getColumnPrimary(int index) {
		return (java.lang.String)_ColumnPrimary.get(index);
	}

	// Return the number of columnPrimary
	public int sizeColumnPrimary() {
		return _ColumnPrimary.size();
	}

	public int addColumnPrimary(java.lang.String value) {
		_ColumnPrimary.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnPrimary."+Integer.toHexString(_ColumnPrimary.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnPrimary.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnPrimary(java.lang.String value) {
		int pos = _ColumnPrimary.indexOf(value);
		if (pos >= 0) {
			_ColumnPrimary.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnPrimary."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnEntity(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnEntity()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnEntity(i) == null : value[i].equals(getColumnEntity(i)))) {
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
			int oldSize = sizeColumnEntity();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnEntity(oldIndex) == null : value[newIndex].equals(getColumnEntity(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnEntity(oldIndex+1) == null : value[newIndex].equals(getColumnEntity(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnEntity(oldIndex) == null : value[newIndex+1].equals(getColumnEntity(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnEntity."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnEntity.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnEntity."+Integer.toHexString(removeIndex), getColumnEntity(removeIndex), null);
				_ColumnEntity.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnEntity.-1", getColumnEntity(), value);
			}
		}
		_ColumnEntity.clear();
		((java.util.ArrayList) _ColumnEntity).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnEntity.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnEntity(int index, java.lang.String value) {
		if (value == null ? getColumnEntity(index) == null : value.equals(getColumnEntity(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnEntity."+Integer.toHexString(index), getColumnEntity(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnEntity.size(); index >= size; ++size) {
			_ColumnEntity.add(null);
		}
		_ColumnEntity.set(index, value);
	}

	public java.lang.String[] getColumnEntity() {
		java.lang.String[] arr = new java.lang.String[_ColumnEntity.size()];
		return (java.lang.String[]) _ColumnEntity.toArray(arr);
	}

	public java.util.List fetchColumnEntityList() {
		return _ColumnEntity;
	}

	public java.lang.String getColumnEntity(int index) {
		return (java.lang.String)_ColumnEntity.get(index);
	}

	// Return the number of columnEntity
	public int sizeColumnEntity() {
		return _ColumnEntity.size();
	}

	public int addColumnEntity(java.lang.String value) {
		_ColumnEntity.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnEntity."+Integer.toHexString(_ColumnEntity.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnEntity.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnEntity(java.lang.String value) {
		int pos = _ColumnEntity.indexOf(value);
		if (pos >= 0) {
			_ColumnEntity.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnEntity."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnMappingKey(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnMappingKey()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnMappingKey(i) == null : value[i].equals(getColumnMappingKey(i)))) {
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
			int oldSize = sizeColumnMappingKey();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnMappingKey(oldIndex) == null : value[newIndex].equals(getColumnMappingKey(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnMappingKey(oldIndex+1) == null : value[newIndex].equals(getColumnMappingKey(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnMappingKey(oldIndex) == null : value[newIndex+1].equals(getColumnMappingKey(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingKey."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnMappingKey.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingKey."+Integer.toHexString(removeIndex), getColumnMappingKey(removeIndex), null);
				_ColumnMappingKey.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingKey.-1", getColumnMappingKey(), value);
			}
		}
		_ColumnMappingKey.clear();
		((java.util.ArrayList) _ColumnMappingKey).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnMappingKey.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnMappingKey(int index, java.lang.String value) {
		if (value == null ? getColumnMappingKey(index) == null : value.equals(getColumnMappingKey(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingKey."+Integer.toHexString(index), getColumnMappingKey(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnMappingKey.size(); index >= size; ++size) {
			_ColumnMappingKey.add(null);
		}
		_ColumnMappingKey.set(index, value);
	}

	public java.lang.String[] getColumnMappingKey() {
		java.lang.String[] arr = new java.lang.String[_ColumnMappingKey.size()];
		return (java.lang.String[]) _ColumnMappingKey.toArray(arr);
	}

	public java.util.List fetchColumnMappingKeyList() {
		return _ColumnMappingKey;
	}

	public java.lang.String getColumnMappingKey(int index) {
		return (java.lang.String)_ColumnMappingKey.get(index);
	}

	// Return the number of columnMappingKey
	public int sizeColumnMappingKey() {
		return _ColumnMappingKey.size();
	}

	public int addColumnMappingKey(java.lang.String value) {
		_ColumnMappingKey.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingKey."+Integer.toHexString(_ColumnMappingKey.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnMappingKey.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnMappingKey(java.lang.String value) {
		int pos = _ColumnMappingKey.indexOf(value);
		if (pos >= 0) {
			_ColumnMappingKey.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingKey."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnMappingTable(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnMappingTable()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnMappingTable(i) == null : value[i].equals(getColumnMappingTable(i)))) {
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
			int oldSize = sizeColumnMappingTable();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnMappingTable(oldIndex) == null : value[newIndex].equals(getColumnMappingTable(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnMappingTable(oldIndex+1) == null : value[newIndex].equals(getColumnMappingTable(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnMappingTable(oldIndex) == null : value[newIndex+1].equals(getColumnMappingTable(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingTable."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnMappingTable.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingTable."+Integer.toHexString(removeIndex), getColumnMappingTable(removeIndex), null);
				_ColumnMappingTable.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingTable.-1", getColumnMappingTable(), value);
			}
		}
		_ColumnMappingTable.clear();
		((java.util.ArrayList) _ColumnMappingTable).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnMappingTable.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnMappingTable(int index, java.lang.String value) {
		if (value == null ? getColumnMappingTable(index) == null : value.equals(getColumnMappingTable(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingTable."+Integer.toHexString(index), getColumnMappingTable(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnMappingTable.size(); index >= size; ++size) {
			_ColumnMappingTable.add(null);
		}
		_ColumnMappingTable.set(index, value);
	}

	public java.lang.String[] getColumnMappingTable() {
		java.lang.String[] arr = new java.lang.String[_ColumnMappingTable.size()];
		return (java.lang.String[]) _ColumnMappingTable.toArray(arr);
	}

	public java.util.List fetchColumnMappingTableList() {
		return _ColumnMappingTable;
	}

	public java.lang.String getColumnMappingTable(int index) {
		return (java.lang.String)_ColumnMappingTable.get(index);
	}

	// Return the number of columnMappingTable
	public int sizeColumnMappingTable() {
		return _ColumnMappingTable.size();
	}

	public int addColumnMappingTable(java.lang.String value) {
		_ColumnMappingTable.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingTable."+Integer.toHexString(_ColumnMappingTable.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnMappingTable.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnMappingTable(java.lang.String value) {
		int pos = _ColumnMappingTable.indexOf(value);
		if (pos >= 0) {
			_ColumnMappingTable.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnMappingTable."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnIdType(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnIdType()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnIdType(i) == null : value[i].equals(getColumnIdType(i)))) {
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
			int oldSize = sizeColumnIdType();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnIdType(oldIndex) == null : value[newIndex].equals(getColumnIdType(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnIdType(oldIndex+1) == null : value[newIndex].equals(getColumnIdType(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnIdType(oldIndex) == null : value[newIndex+1].equals(getColumnIdType(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdType."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnIdType.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdType."+Integer.toHexString(removeIndex), getColumnIdType(removeIndex), null);
				_ColumnIdType.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdType.-1", getColumnIdType(), value);
			}
		}
		_ColumnIdType.clear();
		((java.util.ArrayList) _ColumnIdType).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnIdType.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnIdType(int index, java.lang.String value) {
		if (value == null ? getColumnIdType(index) == null : value.equals(getColumnIdType(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdType."+Integer.toHexString(index), getColumnIdType(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnIdType.size(); index >= size; ++size) {
			_ColumnIdType.add(null);
		}
		_ColumnIdType.set(index, value);
	}

	public java.lang.String[] getColumnIdType() {
		java.lang.String[] arr = new java.lang.String[_ColumnIdType.size()];
		return (java.lang.String[]) _ColumnIdType.toArray(arr);
	}

	public java.util.List fetchColumnIdTypeList() {
		return _ColumnIdType;
	}

	public java.lang.String getColumnIdType(int index) {
		return (java.lang.String)_ColumnIdType.get(index);
	}

	// Return the number of columnIdType
	public int sizeColumnIdType() {
		return _ColumnIdType.size();
	}

	public int addColumnIdType(java.lang.String value) {
		_ColumnIdType.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdType."+Integer.toHexString(_ColumnIdType.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnIdType.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnIdType(java.lang.String value) {
		int pos = _ColumnIdType.indexOf(value);
		if (pos >= 0) {
			_ColumnIdType.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdType."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnIdParam(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnIdParam()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnIdParam(i) == null : value[i].equals(getColumnIdParam(i)))) {
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
			int oldSize = sizeColumnIdParam();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnIdParam(oldIndex) == null : value[newIndex].equals(getColumnIdParam(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnIdParam(oldIndex+1) == null : value[newIndex].equals(getColumnIdParam(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnIdParam(oldIndex) == null : value[newIndex+1].equals(getColumnIdParam(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdParam."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnIdParam.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdParam."+Integer.toHexString(removeIndex), getColumnIdParam(removeIndex), null);
				_ColumnIdParam.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdParam.-1", getColumnIdParam(), value);
			}
		}
		_ColumnIdParam.clear();
		((java.util.ArrayList) _ColumnIdParam).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnIdParam.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnIdParam(int index, java.lang.String value) {
		if (value == null ? getColumnIdParam(index) == null : value.equals(getColumnIdParam(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdParam."+Integer.toHexString(index), getColumnIdParam(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnIdParam.size(); index >= size; ++size) {
			_ColumnIdParam.add(null);
		}
		_ColumnIdParam.set(index, value);
	}

	public java.lang.String[] getColumnIdParam() {
		java.lang.String[] arr = new java.lang.String[_ColumnIdParam.size()];
		return (java.lang.String[]) _ColumnIdParam.toArray(arr);
	}

	public java.util.List fetchColumnIdParamList() {
		return _ColumnIdParam;
	}

	public java.lang.String getColumnIdParam(int index) {
		return (java.lang.String)_ColumnIdParam.get(index);
	}

	// Return the number of columnIdParam
	public int sizeColumnIdParam() {
		return _ColumnIdParam.size();
	}

	public int addColumnIdParam(java.lang.String value) {
		_ColumnIdParam.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdParam."+Integer.toHexString(_ColumnIdParam.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnIdParam.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnIdParam(java.lang.String value) {
		int pos = _ColumnIdParam.indexOf(value);
		if (pos >= 0) {
			_ColumnIdParam.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnIdParam."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setColumnConvertNull(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeColumnConvertNull()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getColumnConvertNull(i) == null : value[i].equals(getColumnConvertNull(i)))) {
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
			int oldSize = sizeColumnConvertNull();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getColumnConvertNull(oldIndex) == null : value[newIndex].equals(getColumnConvertNull(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getColumnConvertNull(oldIndex+1) == null : value[newIndex].equals(getColumnConvertNull(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getColumnConvertNull(oldIndex) == null : value[newIndex+1].equals(getColumnConvertNull(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnConvertNull."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ColumnConvertNull.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnConvertNull."+Integer.toHexString(removeIndex), getColumnConvertNull(removeIndex), null);
				_ColumnConvertNull.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnConvertNull.-1", getColumnConvertNull(), value);
			}
		}
		_ColumnConvertNull.clear();
		((java.util.ArrayList) _ColumnConvertNull).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ColumnConvertNull.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setColumnConvertNull(int index, java.lang.String value) {
		if (value == null ? getColumnConvertNull(index) == null : value.equals(getColumnConvertNull(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnConvertNull."+Integer.toHexString(index), getColumnConvertNull(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ColumnConvertNull.size(); index >= size; ++size) {
			_ColumnConvertNull.add(null);
		}
		_ColumnConvertNull.set(index, value);
	}

	public java.lang.String[] getColumnConvertNull() {
		java.lang.String[] arr = new java.lang.String[_ColumnConvertNull.size()];
		return (java.lang.String[]) _ColumnConvertNull.toArray(arr);
	}

	public java.util.List fetchColumnConvertNullList() {
		return _ColumnConvertNull;
	}

	public java.lang.String getColumnConvertNull(int index) {
		return (java.lang.String)_ColumnConvertNull.get(index);
	}

	// Return the number of columnConvertNull
	public int sizeColumnConvertNull() {
		return _ColumnConvertNull.size();
	}

	public int addColumnConvertNull(java.lang.String value) {
		_ColumnConvertNull.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnConvertNull."+Integer.toHexString(_ColumnConvertNull.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ColumnConvertNull.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeColumnConvertNull(java.lang.String value) {
		int pos = _ColumnConvertNull.indexOf(value);
		if (pos >= 0) {
			_ColumnConvertNull.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ColumnConvertNull."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is optional
	public void setOrder( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order
             valueInterface) {
		Order value = (Order) valueInterface;
		if (value == null ? _Order == null : value.equals(_Order)) {
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
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Order", getOrder(), value);
		}
		_Order = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order
             getOrder() {
		return _Order;
	}

	// This attribute is an array, possibly empty
	public void setFinder( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
            [] valueInterface) {
		Finder[] value = (Finder[]) valueInterface;
		if (value == null)
			value = new Finder[0];
		if (value.length == sizeFinder()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getFinder(i) == null : value[i].equals(getFinder(i)))) {
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
			int oldSize = sizeFinder();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getFinder(oldIndex) == null : value[newIndex].equals(getFinder(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getFinder(oldIndex+1) == null : value[newIndex].equals(getFinder(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getFinder(oldIndex) == null : value[newIndex+1].equals(getFinder(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Finder."+Integer.toHexString(addIndex), null, value[addIndex]);
				_Finder.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Finder."+Integer.toHexString(removeIndex), getFinder(removeIndex), null);
				_Finder.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Finder.-1", getFinder(), value);
			}
		}
		_Finder.clear();
		((java.util.ArrayList) _Finder).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_Finder.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setFinder(int index,  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
             valueInterface) {
		Finder value = (Finder) valueInterface;
		if (value == null ? getFinder(index) == null : value.equals(getFinder(index))) {
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
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Finder."+Integer.toHexString(index), getFinder(index), value);
			eventListeners.firePropertyChange(event);
		}
		_Finder.set(index, value);
	}

	public  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
            [] getFinder() {
		Finder[] arr = new Finder[_Finder.size()];
		return (Finder[]) _Finder.toArray(arr);
	}

	public java.util.List fetchFinderList() {
		return _Finder;
	}

	public  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
             getFinder(int index) {
		return (Finder)_Finder.get(index);
	}

	// Return the number of finder
	public int sizeFinder() {
		return _Finder.size();
	}

	public int addFinder( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
             valueInterface) {
		Finder value = (Finder) valueInterface;
		if (value != null) {
			value._setParent(this);
		}
		if (value != null) {
			// Make the foreign beans take on our property change event listeners.
			value._setPropertyChangeSupport(eventListeners);
		}
		_Finder.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Finder."+Integer.toHexString(_Finder.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _Finder.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeFinder( 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
             valueInterface) {
		Finder value = (Finder) valueInterface;
		int pos = _Finder.indexOf(value);
		if (pos >= 0) {
			_Finder.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Finder."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setReference(String[] value) {
		if (value == null)
			value = new String[0];
		if (value.length == sizeReference()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getReference(i) == null : value[i].equals(getReference(i)))) {
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
			int oldSize = sizeReference();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getReference(oldIndex) == null : value[newIndex].equals(getReference(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getReference(oldIndex+1) == null : value[newIndex].equals(getReference(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getReference(oldIndex) == null : value[newIndex+1].equals(getReference(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Reference."+Integer.toHexString(addIndex), null, value[addIndex]);
				_Reference.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Reference."+Integer.toHexString(removeIndex), getReference(removeIndex), null);
				_Reference.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Reference.-1", getReference(), value);
			}
		}
		_Reference.clear();
		((java.util.ArrayList) _Reference).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_Reference.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setReference(int index, String value) {
		if (value == null ? getReference(index) == null : value.equals(getReference(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Reference."+Integer.toHexString(index), getReference(index), value);
			eventListeners.firePropertyChange(event);
		}
		_Reference.set(index, value);
	}

	public String[] getReference() {
		String[] arr = new String[_Reference.size()];
		return (String[]) _Reference.toArray(arr);
	}

	public java.util.List fetchReferenceList() {
		return _Reference;
	}

	public String getReference(int index) {
		return (String)_Reference.get(index);
	}

	// Return the number of reference
	public int sizeReference() {
		return _Reference.size();
	}

	public int addReference(String value) {
		_Reference.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Reference."+Integer.toHexString(_Reference.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _Reference.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeReference(String value) {
		int pos = _Reference.indexOf(value);
		if (pos >= 0) {
			_Reference.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Reference."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setReferencePackagePath(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeReferencePackagePath()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getReferencePackagePath(i) == null : value[i].equals(getReferencePackagePath(i)))) {
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
			int oldSize = sizeReferencePackagePath();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getReferencePackagePath(oldIndex) == null : value[newIndex].equals(getReferencePackagePath(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getReferencePackagePath(oldIndex+1) == null : value[newIndex].equals(getReferencePackagePath(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getReferencePackagePath(oldIndex) == null : value[newIndex+1].equals(getReferencePackagePath(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferencePackagePath."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ReferencePackagePath.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferencePackagePath."+Integer.toHexString(removeIndex), getReferencePackagePath(removeIndex), null);
				_ReferencePackagePath.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferencePackagePath.-1", getReferencePackagePath(), value);
			}
		}
		_ReferencePackagePath.clear();
		((java.util.ArrayList) _ReferencePackagePath).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ReferencePackagePath.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setReferencePackagePath(int index, java.lang.String value) {
		if (value == null ? getReferencePackagePath(index) == null : value.equals(getReferencePackagePath(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferencePackagePath."+Integer.toHexString(index), getReferencePackagePath(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ReferencePackagePath.size(); index >= size; 
			++size) {
			_ReferencePackagePath.add(null);
		}
		_ReferencePackagePath.set(index, value);
	}

	public java.lang.String[] getReferencePackagePath() {
		java.lang.String[] arr = new java.lang.String[_ReferencePackagePath.size()];
		return (java.lang.String[]) _ReferencePackagePath.toArray(arr);
	}

	public java.util.List fetchReferencePackagePathList() {
		return _ReferencePackagePath;
	}

	public java.lang.String getReferencePackagePath(int index) {
		return (java.lang.String)_ReferencePackagePath.get(index);
	}

	// Return the number of referencePackagePath
	public int sizeReferencePackagePath() {
		return _ReferencePackagePath.size();
	}

	public int addReferencePackagePath(java.lang.String value) {
		_ReferencePackagePath.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferencePackagePath."+Integer.toHexString(_ReferencePackagePath.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ReferencePackagePath.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeReferencePackagePath(java.lang.String value) {
		int pos = _ReferencePackagePath.indexOf(value);
		if (pos >= 0) {
			_ReferencePackagePath.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferencePackagePath."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setReferenceEntity(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeReferenceEntity()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getReferenceEntity(i) == null : value[i].equals(getReferenceEntity(i)))) {
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
			int oldSize = sizeReferenceEntity();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getReferenceEntity(oldIndex) == null : value[newIndex].equals(getReferenceEntity(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getReferenceEntity(oldIndex+1) == null : value[newIndex].equals(getReferenceEntity(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getReferenceEntity(oldIndex) == null : value[newIndex+1].equals(getReferenceEntity(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferenceEntity."+Integer.toHexString(addIndex), null, value[addIndex]);
				_ReferenceEntity.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferenceEntity."+Integer.toHexString(removeIndex), getReferenceEntity(removeIndex), null);
				_ReferenceEntity.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferenceEntity.-1", getReferenceEntity(), value);
			}
		}
		_ReferenceEntity.clear();
		((java.util.ArrayList) _ReferenceEntity).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_ReferenceEntity.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setReferenceEntity(int index, java.lang.String value) {
		if (value == null ? getReferenceEntity(index) == null : value.equals(getReferenceEntity(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferenceEntity."+Integer.toHexString(index), getReferenceEntity(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _ReferenceEntity.size(); index >= size; ++size) {
			_ReferenceEntity.add(null);
		}
		_ReferenceEntity.set(index, value);
	}

	public java.lang.String[] getReferenceEntity() {
		java.lang.String[] arr = new java.lang.String[_ReferenceEntity.size()];
		return (java.lang.String[]) _ReferenceEntity.toArray(arr);
	}

	public java.util.List fetchReferenceEntityList() {
		return _ReferenceEntity;
	}

	public java.lang.String getReferenceEntity(int index) {
		return (java.lang.String)_ReferenceEntity.get(index);
	}

	// Return the number of referenceEntity
	public int sizeReferenceEntity() {
		return _ReferenceEntity.size();
	}

	public int addReferenceEntity(java.lang.String value) {
		_ReferenceEntity.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferenceEntity."+Integer.toHexString(_ReferenceEntity.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _ReferenceEntity.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeReferenceEntity(java.lang.String value) {
		int pos = _ReferenceEntity.indexOf(value);
		if (pos >= 0) {
			_ReferenceEntity.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ReferenceEntity."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setTxRequired(String[] value) {
		if (value == null)
			value = new String[0];
		if (value.length == sizeTxRequired()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getTxRequired(i) == null : value[i].equals(getTxRequired(i)))) {
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
			int oldSize = sizeTxRequired();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getTxRequired(oldIndex) == null : value[newIndex].equals(getTxRequired(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getTxRequired(oldIndex+1) == null : value[newIndex].equals(getTxRequired(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getTxRequired(oldIndex) == null : value[newIndex+1].equals(getTxRequired(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/TxRequired."+Integer.toHexString(addIndex), null, value[addIndex]);
				_TxRequired.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/TxRequired."+Integer.toHexString(removeIndex), getTxRequired(removeIndex), null);
				_TxRequired.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/TxRequired.-1", getTxRequired(), value);
			}
		}
		_TxRequired.clear();
		((java.util.ArrayList) _TxRequired).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_TxRequired.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setTxRequired(int index, String value) {
		if (value == null ? getTxRequired(index) == null : value.equals(getTxRequired(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/TxRequired."+Integer.toHexString(index), getTxRequired(index), value);
			eventListeners.firePropertyChange(event);
		}
		_TxRequired.set(index, value);
	}

	public String[] getTxRequired() {
		String[] arr = new String[_TxRequired.size()];
		return (String[]) _TxRequired.toArray(arr);
	}

	public java.util.List fetchTxRequiredList() {
		return _TxRequired;
	}

	public String getTxRequired(int index) {
		return (String)_TxRequired.get(index);
	}

	// Return the number of txRequired
	public int sizeTxRequired() {
		return _TxRequired.size();
	}

	public int addTxRequired(String value) {
		_TxRequired.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/TxRequired."+Integer.toHexString(_TxRequired.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _TxRequired.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeTxRequired(String value) {
		int pos = _TxRequired.indexOf(value);
		if (pos >= 0) {
			_TxRequired.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/TxRequired."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order newOrder() {
		return new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order();
	}

	/**
	 * Create a new bean, copying from another one.
	 * This does not add it to any bean graph.
	 */
	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order newOrder(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData) {
		return new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order((Order) source, parent, justData);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder newFinder() {
		return new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder();
	}

	/**
	 * Create a new bean, copying from another one.
	 * This does not add it to any bean graph.
	 */
	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder newFinder(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData) {
		return new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder((Finder) source, parent, justData);
	}

	public void _setParent(org.netbeans.modules.schema2beans.BaseBean parent) {
		this.parent = parent;
	}

	public String _getXPathExpr() {
		if (parent == null) {
			return "/entity";
		} else {
			String parentXPathExpr = parent._getXPathExpr();
			String myExpr = parent.nameChild(this, false, false, true);
			return parentXPathExpr + "/" + myExpr;
		}
	}

	public String _getXPathExpr(Object childObj) {
		String childName = nameChild(childObj, false, false, true);
		if (childName == null) {
			throw new IllegalArgumentException("childObj ("+childObj.toString()+") is not a child of this bean (Entity).");
		}
		return _getXPathExpr() + "/" + childName;
	}

	public void writeNode(java.io.Writer out) throws java.io.IOException {
		String myName;
		if (parent == null) {
			myName = "entity";
		} else {
			myName = parent.nameChild(this, false, true);
			if (myName == null) {
				myName = "entity";
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
		// name is an attribute with namespace null
		if (_Name != null) {
			out.write(" name='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _Name, true);
			out.write("'");	// NOI18N
		}
		// table is an attribute with namespace null
		if (_Table != null) {
			out.write(" table='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _Table, true);
			out.write("'");	// NOI18N
		}
		// uuid is an attribute with namespace null
		if (_Uuid != null) {
			out.write(" uuid='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _Uuid, true);
			out.write("'");	// NOI18N
		}
		// local-service is an attribute with namespace null
		if (_LocalService != null) {
			out.write(" local-service='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _LocalService, true);
			out.write("'");	// NOI18N
		}
		// remote-service is an attribute with namespace null
		if (_RemoteService != null) {
			out.write(" remote-service='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _RemoteService, true);
			out.write("'");	// NOI18N
		}
		// persistence-class is an attribute with namespace null
		if (_PersistenceClass != null) {
			out.write(" persistence-class='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _PersistenceClass, true);
			out.write("'");	// NOI18N
		}
		// data-source is an attribute with namespace null
		if (_DataSource != null) {
			out.write(" data-source='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _DataSource, true);
			out.write("'");	// NOI18N
		}
		// session-factory is an attribute with namespace null
		if (_SessionFactory != null) {
			out.write(" session-factory='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _SessionFactory, true);
			out.write("'");	// NOI18N
		}
		// tx-manager is an attribute with namespace null
		if (_TxManager != null) {
			out.write(" tx-manager='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _TxManager, true);
			out.write("'");	// NOI18N
		}
		// cache-enabled is an attribute with namespace null
		if (_CacheEnabled != null) {
			out.write(" cache-enabled='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _CacheEnabled, true);
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
		int index = 0;
		for (java.util.Iterator it = _Column.iterator(); it.hasNext(); ) {
			String element = (String)it.next();
			if (element != null) {
				out.write(nextIndent);
				out.write("<column");	// NOI18N
				if (index < sizeColumnName()) {
					// name is an attribute with namespace null
					if (getColumnName(index) != null) {
						out.write(" name='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnName(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeColumnDbName()) {
					// db-name is an attribute with namespace null
					if (getColumnDbName(index) != null) {
						out.write(" db-name='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnDbName(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeColumnType()) {
					// type is an attribute with namespace null
					if (getColumnType(index) != null) {
						out.write(" type='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnType(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeColumnPrimary()) {
					// primary is an attribute with namespace null
					if (getColumnPrimary(index) != null) {
						out.write(" primary='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnPrimary(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeColumnEntity()) {
					// entity is an attribute with namespace null
					if (getColumnEntity(index) != null) {
						out.write(" entity='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnEntity(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeColumnMappingKey()) {
					// mapping-key is an attribute with namespace null
					if (getColumnMappingKey(index) != null) {
						out.write(" mapping-key='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnMappingKey(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeColumnMappingTable()) {
					// mapping-table is an attribute with namespace null
					if (getColumnMappingTable(index) != null) {
						out.write(" mapping-table='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnMappingTable(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeColumnIdType()) {
					// id-type is an attribute with namespace null
					if (getColumnIdType(index) != null) {
						out.write(" id-type='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnIdType(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeColumnIdParam()) {
					// id-param is an attribute with namespace null
					if (getColumnIdParam(index) != null) {
						out.write(" id-param='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnIdParam(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeColumnConvertNull()) {
					// convert-null is an attribute with namespace null
					if (getColumnConvertNull(index) != null) {
						out.write(" convert-null='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getColumnConvertNull(index), true);
						out.write("'");	// NOI18N
					}
				}
				out.write(">");	// NOI18N
				org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, element, false);
				out.write("</column>\n");	// NOI18N
			}
			++index;
		}
		if (_Order != null) {
			_Order.writeNode(out, "order", null, nextIndent, namespaceMap);
		}
		for (java.util.Iterator it = _Finder.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder)it.next();
			if (element != null) {
				element.writeNode(out, "finder", null, nextIndent, namespaceMap);
			}
		}
		index = 0;
		for (java.util.Iterator it = _Reference.iterator(); it.hasNext(); 
			) {
			String element = (String)it.next();
			if (element != null) {
				out.write(nextIndent);
				out.write("<reference");	// NOI18N
				if (index < sizeReferencePackagePath()) {
					// package-path is an attribute with namespace null
					if (getReferencePackagePath(index) != null) {
						out.write(" package-path='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getReferencePackagePath(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeReferenceEntity()) {
					// entity is an attribute with namespace null
					if (getReferenceEntity(index) != null) {
						out.write(" entity='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getReferenceEntity(index), true);
						out.write("'");	// NOI18N
					}
				}
				out.write(">");	// NOI18N
				org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, element, false);
				out.write("</reference>\n");	// NOI18N
			}
			++index;
		}
		for (java.util.Iterator it = _TxRequired.iterator(); it.hasNext(); 
			) {
			String element = (String)it.next();
			if (element != null) {
				out.write(nextIndent);
				out.write("<tx-required");	// NOI18N
				out.write(">");	// NOI18N
				org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, element, false);
				out.write("</tx-required>\n");	// NOI18N
			}
		}
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
			readNodeAttributes(node, namespacePrefixes, attrs);
		}
		readNodeChildren(node, namespacePrefixes);
	}

	protected void readNodeAttributes(org.w3c.dom.Node node, java.util.Map namespacePrefixes, org.w3c.dom.NamedNodeMap attrs) {
		org.w3c.dom.Attr attr;
		java.lang.String attrValue;
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("name");
		if (attr != null) {
			attrValue = attr.getValue();
			_Name = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("table");
		if (attr != null) {
			attrValue = attr.getValue();
			_Table = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("uuid");
		if (attr != null) {
			attrValue = attr.getValue();
			_Uuid = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("local-service");
		if (attr != null) {
			attrValue = attr.getValue();
			_LocalService = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("remote-service");
		if (attr != null) {
			attrValue = attr.getValue();
			_RemoteService = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("persistence-class");
		if (attr != null) {
			attrValue = attr.getValue();
			_PersistenceClass = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("data-source");
		if (attr != null) {
			attrValue = attr.getValue();
			_DataSource = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("session-factory");
		if (attr != null) {
			attrValue = attr.getValue();
			_SessionFactory = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("tx-manager");
		if (attr != null) {
			attrValue = attr.getValue();
			_TxManager = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("cache-enabled");
		if (attr != null) {
			attrValue = attr.getValue();
			_CacheEnabled = attrValue;
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
		org.w3c.dom.NamedNodeMap attrs = childNode.getAttributes();
		org.w3c.dom.Attr attr;
		java.lang.String attrValue;
		if (childNode instanceof org.w3c.dom.Comment) {
			java.lang.String aComments;
			aComments = ((org.w3c.dom.CharacterData)childNode).getData();
			_Comments.add(aComments);
		}
		else if (childNodeName == "column") {
			String aColumn;
			aColumn = childNodeValue;
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("name");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnName;
			processedValueFor_ColumnName = attrValue;
			addColumnName(processedValueFor_ColumnName);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("db-name");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnDbName;
			processedValueFor_ColumnDbName = attrValue;
			addColumnDbName(processedValueFor_ColumnDbName);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("type");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnType;
			processedValueFor_ColumnType = attrValue;
			addColumnType(processedValueFor_ColumnType);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("primary");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnPrimary;
			processedValueFor_ColumnPrimary = attrValue;
			addColumnPrimary(processedValueFor_ColumnPrimary);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("entity");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnEntity;
			processedValueFor_ColumnEntity = attrValue;
			addColumnEntity(processedValueFor_ColumnEntity);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("mapping-key");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnMappingKey;
			processedValueFor_ColumnMappingKey = attrValue;
			addColumnMappingKey(processedValueFor_ColumnMappingKey);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("mapping-table");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnMappingTable;
			processedValueFor_ColumnMappingTable = attrValue;
			addColumnMappingTable(processedValueFor_ColumnMappingTable);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("id-type");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnIdType;
			processedValueFor_ColumnIdType = attrValue;
			addColumnIdType(processedValueFor_ColumnIdType);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("id-param");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnIdParam;
			processedValueFor_ColumnIdParam = attrValue;
			addColumnIdParam(processedValueFor_ColumnIdParam);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("convert-null");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ColumnConvertNull;
			processedValueFor_ColumnConvertNull = attrValue;
			addColumnConvertNull(processedValueFor_ColumnConvertNull);
			_Column.add(aColumn);
		}
		else if (childNodeName == "order") {
			_Order = (Order) newOrder();
			_Order._setPropertyChangeSupport(eventListeners);
			_Order._setParent(this);
			_Order.readNode(childNode, namespacePrefixes);
		}
		else if (childNodeName == "finder") {
			Finder aFinder = (Finder) newFinder();
			aFinder._setPropertyChangeSupport(eventListeners);
			aFinder._setParent(this);
			aFinder.readNode(childNode, namespacePrefixes);
			_Finder.add(aFinder);
		}
		else if (childNodeName == "reference") {
			String aReference;
			aReference = childNodeValue;
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("package-path");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ReferencePackagePath;
			processedValueFor_ReferencePackagePath = attrValue;
			addReferencePackagePath(processedValueFor_ReferencePackagePath);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("entity");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_ReferenceEntity;
			processedValueFor_ReferenceEntity = attrValue;
			addReferenceEntity(processedValueFor_ReferenceEntity);
			_Reference.add(aReference);
		}
		else if (childNodeName == "tx-required") {
			String aTxRequired;
			aTxRequired = childNodeValue;
			_TxRequired.add(aTxRequired);
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
		// Validating property name
		if (getName() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getName() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "name", this);	// NOI18N
		}
		// Validating property table
		// Validating property uuid
		// Validating property localService
		// Validating property remoteService
		// Validating property persistenceClass
		// Validating property dataSource
		// Validating property sessionFactory
		// Validating property txManager
		// Validating property cacheEnabled
		// Validating property column
		// Validating property columnName
		// Validating property columnDbName
		// Validating property columnType
		// Validating property columnPrimary
		// Validating property columnEntity
		// Validating property columnMappingKey
		// Validating property columnMappingTable
		// Validating property columnIdType
		// Validating property columnIdParam
		// Validating property columnConvertNull
		// Validating property order
		if (getOrder() != null) {
			((Order)getOrder()).validate();
		}
		// Validating property finder
		for (int _index = 0; _index < sizeFinder(); ++_index) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder) getFinder(_index);
			if (element != null) {
				((Finder)element).validate();
			}
		}
		// Validating property reference
		// Validating property referencePackagePath
		// Validating property referenceEntity
		// Validating property txRequired
	}

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		if (eventListeners == null) {
			eventListeners = new java.beans.PropertyChangeSupport(this);
		}
		eventListeners.addPropertyChangeListener(listener);
		if (_Order != null) {
			_Order.addPropertyChangeListener(listener);
		}
		for (java.util.Iterator it = _Finder.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder)it.next();
			if (element != null) {
				element.addPropertyChangeListener(listener);
			}
		}
	}

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		if (_Order != null) {
			_Order.removePropertyChangeListener(listener);
		}
		for (java.util.Iterator it = _Finder.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder)it.next();
			if (element != null) {
				element.removePropertyChangeListener(listener);
			}
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
		if (_Order != null) {
			_Order._setPropertyChangeSupport(listeners);
		}
		for (java.util.Iterator it = _Finder.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder)it.next();
			if (element != null) {
				element._setPropertyChangeSupport(listeners);
			}
		}
	}

	public void changePropertyByName(String name, Object value) {
		if (name == null) return;
		name = name.intern();
		if (name == "comments")
			addComments((java.lang.String)value);
		else if (name == "comments[]")
			setComments((java.lang.String[]) value);
		else if (name == "name")
			setName((java.lang.String)value);
		else if (name == "table")
			setTable((java.lang.String)value);
		else if (name == "uuid")
			setUuid((java.lang.String)value);
		else if (name == "localService")
			setLocalService((java.lang.String)value);
		else if (name == "remoteService")
			setRemoteService((java.lang.String)value);
		else if (name == "persistenceClass")
			setPersistenceClass((java.lang.String)value);
		else if (name == "dataSource")
			setDataSource((java.lang.String)value);
		else if (name == "sessionFactory")
			setSessionFactory((java.lang.String)value);
		else if (name == "txManager")
			setTxManager((java.lang.String)value);
		else if (name == "cacheEnabled")
			setCacheEnabled((java.lang.String)value);
		else if (name == "column")
			addColumn((String)value);
		else if (name == "column[]")
			setColumn((String[]) value);
		else if (name == "columnName")
			addColumnName((java.lang.String)value);
		else if (name == "columnName[]")
			setColumnName((java.lang.String[]) value);
		else if (name == "columnDbName")
			addColumnDbName((java.lang.String)value);
		else if (name == "columnDbName[]")
			setColumnDbName((java.lang.String[]) value);
		else if (name == "columnType")
			addColumnType((java.lang.String)value);
		else if (name == "columnType[]")
			setColumnType((java.lang.String[]) value);
		else if (name == "columnPrimary")
			addColumnPrimary((java.lang.String)value);
		else if (name == "columnPrimary[]")
			setColumnPrimary((java.lang.String[]) value);
		else if (name == "columnEntity")
			addColumnEntity((java.lang.String)value);
		else if (name == "columnEntity[]")
			setColumnEntity((java.lang.String[]) value);
		else if (name == "columnMappingKey")
			addColumnMappingKey((java.lang.String)value);
		else if (name == "columnMappingKey[]")
			setColumnMappingKey((java.lang.String[]) value);
		else if (name == "columnMappingTable")
			addColumnMappingTable((java.lang.String)value);
		else if (name == "columnMappingTable[]")
			setColumnMappingTable((java.lang.String[]) value);
		else if (name == "columnIdType")
			addColumnIdType((java.lang.String)value);
		else if (name == "columnIdType[]")
			setColumnIdType((java.lang.String[]) value);
		else if (name == "columnIdParam")
			addColumnIdParam((java.lang.String)value);
		else if (name == "columnIdParam[]")
			setColumnIdParam((java.lang.String[]) value);
		else if (name == "columnConvertNull")
			addColumnConvertNull((java.lang.String)value);
		else if (name == "columnConvertNull[]")
			setColumnConvertNull((java.lang.String[]) value);
		else if (name == "order")
			setOrder((Order)value);
		else if (name == "finder")
			addFinder((Finder)value);
		else if (name == "finder[]")
			setFinder((Finder[]) value);
		else if (name == "reference")
			addReference((String)value);
		else if (name == "reference[]")
			setReference((String[]) value);
		else if (name == "referencePackagePath")
			addReferencePackagePath((java.lang.String)value);
		else if (name == "referencePackagePath[]")
			setReferencePackagePath((java.lang.String[]) value);
		else if (name == "referenceEntity")
			addReferenceEntity((java.lang.String)value);
		else if (name == "referenceEntity[]")
			setReferenceEntity((java.lang.String[]) value);
		else if (name == "txRequired")
			addTxRequired((String)value);
		else if (name == "txRequired[]")
			setTxRequired((String[]) value);
		else
			throw new IllegalArgumentException(name+" is not a valid property name for Entity");
	}

	public Object fetchPropertyByName(String name) {
		if (name == "comments[]")
			return getComments();
		if (name == "name")
			return getName();
		if (name == "table")
			return getTable();
		if (name == "uuid")
			return getUuid();
		if (name == "localService")
			return getLocalService();
		if (name == "remoteService")
			return getRemoteService();
		if (name == "persistenceClass")
			return getPersistenceClass();
		if (name == "dataSource")
			return getDataSource();
		if (name == "sessionFactory")
			return getSessionFactory();
		if (name == "txManager")
			return getTxManager();
		if (name == "cacheEnabled")
			return getCacheEnabled();
		if (name == "column[]")
			return getColumn();
		if (name == "columnName[]")
			return getColumnName();
		if (name == "columnDbName[]")
			return getColumnDbName();
		if (name == "columnType[]")
			return getColumnType();
		if (name == "columnPrimary[]")
			return getColumnPrimary();
		if (name == "columnEntity[]")
			return getColumnEntity();
		if (name == "columnMappingKey[]")
			return getColumnMappingKey();
		if (name == "columnMappingTable[]")
			return getColumnMappingTable();
		if (name == "columnIdType[]")
			return getColumnIdType();
		if (name == "columnIdParam[]")
			return getColumnIdParam();
		if (name == "columnConvertNull[]")
			return getColumnConvertNull();
		if (name == "order")
			return getOrder();
		if (name == "finder[]")
			return getFinder();
		if (name == "reference[]")
			return getReference();
		if (name == "referencePackagePath[]")
			return getReferencePackagePath();
		if (name == "referenceEntity[]")
			return getReferenceEntity();
		if (name == "txRequired[]")
			return getTxRequired();
		throw new IllegalArgumentException(name+" is not a valid property name for Entity");
	}

	public String nameSelf() {
		if (parent != null) {
			String parentName = parent.nameSelf();
			String myName = parent.nameChild(this, false, false);
			return parentName + "/" + myName;
		}
		return "Entity";
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
		if (childObj instanceof Order) {
			Order child = (Order) childObj;
			if (child == _Order) {
				if (returnConstName) {
					return ORDER;
				} else if (returnSchemaName) {
					return "order";
				} else if (returnXPathName) {
					return "order";
				} else {
					return "Order";
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
			if (child == _Name) {
				if (returnConstName) {
					return NAME;
				} else if (returnSchemaName) {
					return "name";
				} else if (returnXPathName) {
					return "@name";
				} else {
					return "Name";
				}
			}
			if (child == _Table) {
				if (returnConstName) {
					return TABLE;
				} else if (returnSchemaName) {
					return "table";
				} else if (returnXPathName) {
					return "@table";
				} else {
					return "Table";
				}
			}
			if (child == _Uuid) {
				if (returnConstName) {
					return UUID;
				} else if (returnSchemaName) {
					return "uuid";
				} else if (returnXPathName) {
					return "@uuid";
				} else {
					return "Uuid";
				}
			}
			if (child == _LocalService) {
				if (returnConstName) {
					return LOCALSERVICE;
				} else if (returnSchemaName) {
					return "local-service";
				} else if (returnXPathName) {
					return "@local-service";
				} else {
					return "LocalService";
				}
			}
			if (child == _RemoteService) {
				if (returnConstName) {
					return REMOTESERVICE;
				} else if (returnSchemaName) {
					return "remote-service";
				} else if (returnXPathName) {
					return "@remote-service";
				} else {
					return "RemoteService";
				}
			}
			if (child == _PersistenceClass) {
				if (returnConstName) {
					return PERSISTENCECLASS;
				} else if (returnSchemaName) {
					return "persistence-class";
				} else if (returnXPathName) {
					return "@persistence-class";
				} else {
					return "PersistenceClass";
				}
			}
			if (child == _DataSource) {
				if (returnConstName) {
					return DATASOURCE;
				} else if (returnSchemaName) {
					return "data-source";
				} else if (returnXPathName) {
					return "@data-source";
				} else {
					return "DataSource";
				}
			}
			if (child == _SessionFactory) {
				if (returnConstName) {
					return SESSIONFACTORY;
				} else if (returnSchemaName) {
					return "session-factory";
				} else if (returnXPathName) {
					return "@session-factory";
				} else {
					return "SessionFactory";
				}
			}
			if (child == _TxManager) {
				if (returnConstName) {
					return TXMANAGER;
				} else if (returnSchemaName) {
					return "tx-manager";
				} else if (returnXPathName) {
					return "@tx-manager";
				} else {
					return "TxManager";
				}
			}
			if (child == _CacheEnabled) {
				if (returnConstName) {
					return CACHEENABLED;
				} else if (returnSchemaName) {
					return "cache-enabled";
				} else if (returnXPathName) {
					return "@cache-enabled";
				} else {
					return "CacheEnabled";
				}
			}
			index = 0;
			for (java.util.Iterator it = _Column.iterator(); it.hasNext(); 
				) {
				String element = (String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMN;
					} else if (returnSchemaName) {
						return "column";
					} else if (returnXPathName) {
						return "column[position()="+index+"]";
					} else {
						return "Column."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnName.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNNAME;
					} else if (returnSchemaName) {
						return "name";
					} else if (returnXPathName) {
						return "@name[position()="+index+"]";
					} else {
						return "ColumnName."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnDbName.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNDBNAME;
					} else if (returnSchemaName) {
						return "db-name";
					} else if (returnXPathName) {
						return "@db-name[position()="+index+"]";
					} else {
						return "ColumnDbName."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnType.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNTYPE;
					} else if (returnSchemaName) {
						return "type";
					} else if (returnXPathName) {
						return "@type[position()="+index+"]";
					} else {
						return "ColumnType."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnPrimary.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNPRIMARY;
					} else if (returnSchemaName) {
						return "primary";
					} else if (returnXPathName) {
						return "@primary[position()="+index+"]";
					} else {
						return "ColumnPrimary."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnEntity.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNENTITY;
					} else if (returnSchemaName) {
						return "entity";
					} else if (returnXPathName) {
						return "@entity[position()="+index+"]";
					} else {
						return "ColumnEntity."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnMappingKey.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNMAPPINGKEY;
					} else if (returnSchemaName) {
						return "mapping-key";
					} else if (returnXPathName) {
						return "@mapping-key[position()="+index+"]";
					} else {
						return "ColumnMappingKey."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnMappingTable.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNMAPPINGTABLE;
					} else if (returnSchemaName) {
						return "mapping-table";
					} else if (returnXPathName) {
						return "@mapping-table[position()="+index+"]";
					} else {
						return "ColumnMappingTable."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnIdType.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNIDTYPE;
					} else if (returnSchemaName) {
						return "id-type";
					} else if (returnXPathName) {
						return "@id-type[position()="+index+"]";
					} else {
						return "ColumnIdType."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnIdParam.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNIDPARAM;
					} else if (returnSchemaName) {
						return "id-param";
					} else if (returnXPathName) {
						return "@id-param[position()="+index+"]";
					} else {
						return "ColumnIdParam."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ColumnConvertNull.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return COLUMNCONVERTNULL;
					} else if (returnSchemaName) {
						return "convert-null";
					} else if (returnXPathName) {
						return "@convert-null[position()="+index+"]";
					} else {
						return "ColumnConvertNull."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _Reference.iterator(); 
				it.hasNext(); ) {
				String element = (String)it.next();
				if (child == element) {
					if (returnConstName) {
						return REFERENCE;
					} else if (returnSchemaName) {
						return "reference";
					} else if (returnXPathName) {
						return "reference[position()="+index+"]";
					} else {
						return "Reference."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ReferencePackagePath.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return REFERENCEPACKAGEPATH;
					} else if (returnSchemaName) {
						return "package-path";
					} else if (returnXPathName) {
						return "@package-path[position()="+index+"]";
					} else {
						return "ReferencePackagePath."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _ReferenceEntity.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return REFERENCEENTITY;
					} else if (returnSchemaName) {
						return "entity";
					} else if (returnXPathName) {
						return "@entity[position()="+index+"]";
					} else {
						return "ReferenceEntity."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _TxRequired.iterator(); 
				it.hasNext(); ) {
				String element = (String)it.next();
				if (child == element) {
					if (returnConstName) {
						return TX_REQUIRED;
					} else if (returnSchemaName) {
						return "tx-required";
					} else if (returnXPathName) {
						return "tx-required[position()="+index+"]";
					} else {
						return "TxRequired."+Integer.toHexString(index);
					}
				}
				++index;
			}
		}
		if (childObj instanceof Finder) {
			Finder child = (Finder) childObj;
			int index = 0;
			for (java.util.Iterator it = _Finder.iterator(); it.hasNext(); 
				) {
				org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder)it.next();
				if (child == element) {
					if (returnConstName) {
						return FINDER;
					} else if (returnSchemaName) {
						return "finder";
					} else if (returnXPathName) {
						return "finder[position()="+index+"]";
					} else {
						return "Finder."+Integer.toHexString(index);
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
		if (_Order != null) {
			if (recursive) {
				_Order.childBeans(true, beans);
			}
			beans.add(_Order);
		}
		for (java.util.Iterator it = _Finder.iterator(); it.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder)it.next();
			if (element != null) {
				if (recursive) {
					element.childBeans(true, beans);
				}
				beans.add(element);
			}
		}
	}

	public boolean equals(Object o) {
		return o instanceof org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity && equals((org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity) o);
	}

	public boolean equals(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity inst) {
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
		if (!(_Name == null ? inst._Name == null : _Name.equals(inst._Name))) {
			return false;
		}
		if (!(_Table == null ? inst._Table == null : _Table.equals(inst._Table))) {
			return false;
		}
		if (!(_Uuid == null ? inst._Uuid == null : _Uuid.equals(inst._Uuid))) {
			return false;
		}
		if (!(_LocalService == null ? inst._LocalService == null : _LocalService.equals(inst._LocalService))) {
			return false;
		}
		if (!(_RemoteService == null ? inst._RemoteService == null : _RemoteService.equals(inst._RemoteService))) {
			return false;
		}
		if (!(_PersistenceClass == null ? inst._PersistenceClass == null : _PersistenceClass.equals(inst._PersistenceClass))) {
			return false;
		}
		if (!(_DataSource == null ? inst._DataSource == null : _DataSource.equals(inst._DataSource))) {
			return false;
		}
		if (!(_SessionFactory == null ? inst._SessionFactory == null : _SessionFactory.equals(inst._SessionFactory))) {
			return false;
		}
		if (!(_TxManager == null ? inst._TxManager == null : _TxManager.equals(inst._TxManager))) {
			return false;
		}
		if (!(_CacheEnabled == null ? inst._CacheEnabled == null : _CacheEnabled.equals(inst._CacheEnabled))) {
			return false;
		}
		if (sizeColumn() != inst.sizeColumn())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _Column.iterator(), it2 = inst._Column.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			String element = (String)it.next();
			String element2 = (String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnName() != inst.sizeColumnName())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnName.iterator(), it2 = inst._ColumnName.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnDbName() != inst.sizeColumnDbName())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnDbName.iterator(), it2 = inst._ColumnDbName.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnType() != inst.sizeColumnType())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnType.iterator(), it2 = inst._ColumnType.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnPrimary() != inst.sizeColumnPrimary())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnPrimary.iterator(), it2 = inst._ColumnPrimary.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnEntity() != inst.sizeColumnEntity())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnEntity.iterator(), it2 = inst._ColumnEntity.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnMappingKey() != inst.sizeColumnMappingKey())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnMappingKey.iterator(), it2 = inst._ColumnMappingKey.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnMappingTable() != inst.sizeColumnMappingTable())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnMappingTable.iterator(), it2 = inst._ColumnMappingTable.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnIdType() != inst.sizeColumnIdType())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnIdType.iterator(), it2 = inst._ColumnIdType.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnIdParam() != inst.sizeColumnIdParam())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnIdParam.iterator(), it2 = inst._ColumnIdParam.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeColumnConvertNull() != inst.sizeColumnConvertNull())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ColumnConvertNull.iterator(), it2 = inst._ColumnConvertNull.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (!(_Order == null ? inst._Order == null : _Order.equals(inst._Order))) {
			return false;
		}
		if (sizeFinder() != inst.sizeFinder())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _Finder.iterator(), it2 = inst._Finder.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder element = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder)it.next();
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder element2 = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeReference() != inst.sizeReference())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _Reference.iterator(), it2 = inst._Reference.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			String element = (String)it.next();
			String element2 = (String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeReferencePackagePath() != inst.sizeReferencePackagePath())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ReferencePackagePath.iterator(), it2 = inst._ReferencePackagePath.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeReferenceEntity() != inst.sizeReferenceEntity())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _ReferenceEntity.iterator(), it2 = inst._ReferenceEntity.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeTxRequired() != inst.sizeTxRequired())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _TxRequired.iterator(), it2 = inst._TxRequired.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			String element = (String)it.next();
			String element2 = (String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		return true;
	}

	public int hashCode1() {
		int result = 17;
		result = 37*result + (_Comments == null ? 0 : _Comments.hashCode());
		result = 37*result + (_Name == null ? 0 : _Name.hashCode());
		result = 37*result + (_Table == null ? 0 : _Table.hashCode());
		result = 37*result + (_Uuid == null ? 0 : _Uuid.hashCode());
		result = 37*result + (_LocalService == null ? 0 : _LocalService.hashCode());
		result = 37*result + (_RemoteService == null ? 0 : _RemoteService.hashCode());
		result = 37*result + (_PersistenceClass == null ? 0 : _PersistenceClass.hashCode());
		result = 37*result + (_DataSource == null ? 0 : _DataSource.hashCode());
		result = 37*result + (_SessionFactory == null ? 0 : _SessionFactory.hashCode());
		result = 37*result + (_TxManager == null ? 0 : _TxManager.hashCode());
		result = 37*result + (_CacheEnabled == null ? 0 : _CacheEnabled.hashCode());
		result = 37*result + (_Column == null ? 0 : _Column.hashCode());
		result = 37*result + (_ColumnName == null ? 0 : _ColumnName.hashCode());
		result = 37*result + (_ColumnDbName == null ? 0 : _ColumnDbName.hashCode());
		result = 37*result + (_ColumnType == null ? 0 : _ColumnType.hashCode());
		result = 37*result + (_ColumnPrimary == null ? 0 : _ColumnPrimary.hashCode());
		result = 37*result + (_ColumnEntity == null ? 0 : _ColumnEntity.hashCode());
		result = 37*result + (_ColumnMappingKey == null ? 0 : _ColumnMappingKey.hashCode());
		result = 37*result + (_ColumnMappingTable == null ? 0 : _ColumnMappingTable.hashCode());
		result = 37*result + (_ColumnIdType == null ? 0 : _ColumnIdType.hashCode());
		result = 37*result + (_ColumnIdParam == null ? 0 : _ColumnIdParam.hashCode());
		result = 37*result + (_ColumnConvertNull == null ? 0 : _ColumnConvertNull.hashCode());
		result = 37*result + (_Order == null ? 0 : _Order.hashCode());
		result = 37*result + (_Finder == null ? 0 : _Finder.hashCode());
		result = 37*result + (_Reference == null ? 0 : _Reference.hashCode());
		result = 37*result + (_ReferencePackagePath == null ? 0 : _ReferencePackagePath.hashCode());
		result = 37*result + (_ReferenceEntity == null ? 0 : _ReferenceEntity.hashCode());
		result = 37*result + (_TxRequired == null ? 0 : _TxRequired.hashCode());
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
				} else if (name == NAME) {
					indexed = false;
					constName = NAME;
					schemaName = "name";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setName", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getName", new Class[] {});
				} else if (name == TABLE) {
					indexed = false;
					constName = TABLE;
					schemaName = "table";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setTable", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getTable", new Class[] {});
				} else if (name == UUID) {
					indexed = false;
					constName = UUID;
					schemaName = "uuid";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setUuid", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getUuid", new Class[] {});
				} else if (name == LOCALSERVICE) {
					indexed = false;
					constName = LOCALSERVICE;
					schemaName = "local-service";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setLocalService", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getLocalService", new Class[] {});
				} else if (name == REMOTESERVICE) {
					indexed = false;
					constName = REMOTESERVICE;
					schemaName = "remote-service";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setRemoteService", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getRemoteService", new Class[] {});
				} else if (name == PERSISTENCECLASS) {
					indexed = false;
					constName = PERSISTENCECLASS;
					schemaName = "persistence-class";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setPersistenceClass", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getPersistenceClass", new Class[] {});
				} else if (name == DATASOURCE) {
					indexed = false;
					constName = DATASOURCE;
					schemaName = "data-source";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setDataSource", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getDataSource", new Class[] {});
				} else if (name == SESSIONFACTORY) {
					indexed = false;
					constName = SESSIONFACTORY;
					schemaName = "session-factory";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setSessionFactory", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getSessionFactory", new Class[] {});
				} else if (name == TXMANAGER) {
					indexed = false;
					constName = TXMANAGER;
					schemaName = "tx-manager";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setTxManager", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getTxManager", new Class[] {});
				} else if (name == CACHEENABLED) {
					indexed = false;
					constName = CACHEENABLED;
					schemaName = "cache-enabled";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setCacheEnabled", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getCacheEnabled", new Class[] {});
				} else if (name == COLUMN) {
					indexed = true;
					constName = COLUMN;
					schemaName = "column";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumn", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumn", new Class[] {});
					writer = getClass().getMethod("setColumn", new Class[] {Integer.TYPE, String.class});
					arrayWriter = getClass().getMethod("setColumn", new Class[] {String[].class});
					adder = getClass().getMethod("addColumn", new Class[] {String.class});
					remover = getClass().getMethod("removeColumn", new Class[] {String.class});
				} else if (name == COLUMNNAME) {
					indexed = true;
					constName = COLUMNNAME;
					schemaName = "name";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnName", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnName", new Class[] {});
					writer = getClass().getMethod("setColumnName", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnName", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnName", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnName", new Class[] {java.lang.String.class});
				} else if (name == COLUMNDBNAME) {
					indexed = true;
					constName = COLUMNDBNAME;
					schemaName = "db-name";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnDbName", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnDbName", new Class[] {});
					writer = getClass().getMethod("setColumnDbName", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnDbName", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnDbName", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnDbName", new Class[] {java.lang.String.class});
				} else if (name == COLUMNTYPE) {
					indexed = true;
					constName = COLUMNTYPE;
					schemaName = "type";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnType", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnType", new Class[] {});
					writer = getClass().getMethod("setColumnType", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnType", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnType", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnType", new Class[] {java.lang.String.class});
				} else if (name == COLUMNPRIMARY) {
					indexed = true;
					constName = COLUMNPRIMARY;
					schemaName = "primary";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnPrimary", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnPrimary", new Class[] {});
					writer = getClass().getMethod("setColumnPrimary", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnPrimary", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnPrimary", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnPrimary", new Class[] {java.lang.String.class});
				} else if (name == COLUMNENTITY) {
					indexed = true;
					constName = COLUMNENTITY;
					schemaName = "entity";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnEntity", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnEntity", new Class[] {});
					writer = getClass().getMethod("setColumnEntity", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnEntity", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnEntity", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnEntity", new Class[] {java.lang.String.class});
				} else if (name == COLUMNMAPPINGKEY) {
					indexed = true;
					constName = COLUMNMAPPINGKEY;
					schemaName = "mapping-key";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnMappingKey", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnMappingKey", new Class[] {});
					writer = getClass().getMethod("setColumnMappingKey", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnMappingKey", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnMappingKey", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnMappingKey", new Class[] {java.lang.String.class});
				} else if (name == COLUMNMAPPINGTABLE) {
					indexed = true;
					constName = COLUMNMAPPINGTABLE;
					schemaName = "mapping-table";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnMappingTable", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnMappingTable", new Class[] {});
					writer = getClass().getMethod("setColumnMappingTable", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnMappingTable", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnMappingTable", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnMappingTable", new Class[] {java.lang.String.class});
				} else if (name == COLUMNIDTYPE) {
					indexed = true;
					constName = COLUMNIDTYPE;
					schemaName = "id-type";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnIdType", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnIdType", new Class[] {});
					writer = getClass().getMethod("setColumnIdType", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnIdType", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnIdType", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnIdType", new Class[] {java.lang.String.class});
				} else if (name == COLUMNIDPARAM) {
					indexed = true;
					constName = COLUMNIDPARAM;
					schemaName = "id-param";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnIdParam", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnIdParam", new Class[] {});
					writer = getClass().getMethod("setColumnIdParam", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnIdParam", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnIdParam", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnIdParam", new Class[] {java.lang.String.class});
				} else if (name == COLUMNCONVERTNULL) {
					indexed = true;
					constName = COLUMNCONVERTNULL;
					schemaName = "convert-null";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getColumnConvertNull", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getColumnConvertNull", new Class[] {});
					writer = getClass().getMethod("setColumnConvertNull", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setColumnConvertNull", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addColumnConvertNull", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeColumnConvertNull", new Class[] {java.lang.String.class});
				} else if (name == ORDER) {
					indexed = false;
					constName = ORDER;
					schemaName = "order";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_BEAN;
					writer = getClass().getMethod("setOrder", new Class[] { 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order
            .class});
					reader = getClass().getMethod("getOrder", new Class[] {});
				} else if (name == FINDER) {
					indexed = true;
					constName = FINDER;
					schemaName = "finder";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_BEAN;
					reader = getClass().getMethod("getFinder", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getFinder", new Class[] {});
					writer = getClass().getMethod("setFinder", new Class[] {Integer.TYPE,  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
            .class});
					arrayWriter = getClass().getMethod("setFinder", new Class[] { 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
            [].class});
					adder = getClass().getMethod("addFinder", new Class[] { 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
            .class});
					remover = getClass().getMethod("removeFinder", new Class[] { 
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder
            .class});
				} else if (name == REFERENCE) {
					indexed = true;
					constName = REFERENCE;
					schemaName = "reference";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getReference", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getReference", new Class[] {});
					writer = getClass().getMethod("setReference", new Class[] {Integer.TYPE, String.class});
					arrayWriter = getClass().getMethod("setReference", new Class[] {String[].class});
					adder = getClass().getMethod("addReference", new Class[] {String.class});
					remover = getClass().getMethod("removeReference", new Class[] {String.class});
				} else if (name == REFERENCEPACKAGEPATH) {
					indexed = true;
					constName = REFERENCEPACKAGEPATH;
					schemaName = "package-path";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getReferencePackagePath", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getReferencePackagePath", new Class[] {});
					writer = getClass().getMethod("setReferencePackagePath", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setReferencePackagePath", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addReferencePackagePath", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeReferencePackagePath", new Class[] {java.lang.String.class});
				} else if (name == REFERENCEENTITY) {
					indexed = true;
					constName = REFERENCEENTITY;
					schemaName = "entity";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getReferenceEntity", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getReferenceEntity", new Class[] {});
					writer = getClass().getMethod("setReferenceEntity", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setReferenceEntity", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addReferenceEntity", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeReferenceEntity", new Class[] {java.lang.String.class});
				} else if (name == TX_REQUIRED) {
					indexed = true;
					constName = TX_REQUIRED;
					schemaName = "tx-required";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getTxRequired", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getTxRequired", new Class[] {});
					writer = getClass().getMethod("setTxRequired", new Class[] {Integer.TYPE, String.class});
					arrayWriter = getClass().getMethod("setTxRequired", new Class[] {String[].class});
					adder = getClass().getMethod("addTxRequired", new Class[] {String.class});
					remover = getClass().getMethod("removeTxRequired", new Class[] {String.class});
				} else {
					// Check if name is a schema name.
					if (name == "comment") {
						prop = beanProp(COMMENTS);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "name") {
						prop = beanProp(NAME);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "table") {
						prop = beanProp(TABLE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "uuid") {
						prop = beanProp(UUID);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "local-service") {
						prop = beanProp(LOCALSERVICE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "remote-service") {
						prop = beanProp(REMOTESERVICE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "persistence-class") {
						prop = beanProp(PERSISTENCECLASS);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "data-source") {
						prop = beanProp(DATASOURCE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "session-factory") {
						prop = beanProp(SESSIONFACTORY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "tx-manager") {
						prop = beanProp(TXMANAGER);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "cache-enabled") {
						prop = beanProp(CACHEENABLED);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "column") {
						prop = beanProp(COLUMN);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "name") {
						prop = beanProp(COLUMNNAME);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "db-name") {
						prop = beanProp(COLUMNDBNAME);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "type") {
						prop = beanProp(COLUMNTYPE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "primary") {
						prop = beanProp(COLUMNPRIMARY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "entity") {
						prop = beanProp(COLUMNENTITY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "mapping-key") {
						prop = beanProp(COLUMNMAPPINGKEY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "mapping-table") {
						prop = beanProp(COLUMNMAPPINGTABLE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "id-type") {
						prop = beanProp(COLUMNIDTYPE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "id-param") {
						prop = beanProp(COLUMNIDPARAM);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "convert-null") {
						prop = beanProp(COLUMNCONVERTNULL);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "order") {
						prop = beanProp(ORDER);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "finder") {
						prop = beanProp(FINDER);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "reference") {
						prop = beanProp(REFERENCE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "package-path") {
						prop = beanProp(REFERENCEPACKAGEPATH);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "entity") {
						prop = beanProp(REFERENCEENTITY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "tx-required") {
						prop = beanProp(TX_REQUIRED);
						propByName.put(name, prop);
						return prop;
					}
					throw new IllegalArgumentException(name+" is not a valid property name for Entity");
				}
			} catch (java.lang.NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
			prop = new org.netbeans.modules.schema2beans.ReflectiveBeanProp(this, schemaName, constName, options, getClass(), false, writer, arrayWriter, reader, arrayReader, adder, remover);
			propByName.put(name, prop);
		}
		return prop;
	}

	public org.netbeans.modules.schema2beans.BeanProp beanProp() {
		if (parent == null) {
			org.netbeans.modules.schema2beans.BeanProp prop = (org.netbeans.modules.schema2beans.BeanProp) propByName.get("");
			if (prop == null) {
				prop = new org.netbeans.modules.schema2beans.ReflectiveBeanProp(this, "entity", "Entity", org.netbeans.modules.schema2beans.Common.TYPE_1 | org.netbeans.modules.schema2beans.Common.TYPE_BEAN, Entity.class, isRoot(), null, null, null, null, null, null);
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
			// Not necessarily the right schema name, but make a good guess.
			return "entity";
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
		return new Entity(this, null, false);
	}

	public Object cloneData() {
		return new Entity(this, null, true);
	}

	private void prepareBeanPropList() {
		if (beanPropList == null) {
			beanPropList = new java.util.ArrayList(28);
			beanPropList.add(beanProp(COMMENTS));
			beanPropList.add(beanProp(NAME));
			beanPropList.add(beanProp(TABLE));
			beanPropList.add(beanProp(UUID));
			beanPropList.add(beanProp(LOCALSERVICE));
			beanPropList.add(beanProp(REMOTESERVICE));
			beanPropList.add(beanProp(PERSISTENCECLASS));
			beanPropList.add(beanProp(DATASOURCE));
			beanPropList.add(beanProp(SESSIONFACTORY));
			beanPropList.add(beanProp(TXMANAGER));
			beanPropList.add(beanProp(CACHEENABLED));
			beanPropList.add(beanProp(COLUMN));
			beanPropList.add(beanProp(COLUMNNAME));
			beanPropList.add(beanProp(COLUMNDBNAME));
			beanPropList.add(beanProp(COLUMNTYPE));
			beanPropList.add(beanProp(COLUMNPRIMARY));
			beanPropList.add(beanProp(COLUMNENTITY));
			beanPropList.add(beanProp(COLUMNMAPPINGKEY));
			beanPropList.add(beanProp(COLUMNMAPPINGTABLE));
			beanPropList.add(beanProp(COLUMNIDTYPE));
			beanPropList.add(beanProp(COLUMNIDPARAM));
			beanPropList.add(beanProp(COLUMNCONVERTNULL));
			beanPropList.add(beanProp(ORDER));
			beanPropList.add(beanProp(FINDER));
			beanPropList.add(beanProp(REFERENCE));
			beanPropList.add(beanProp(REFERENCEPACKAGEPATH));
			beanPropList.add(beanProp(REFERENCEENTITY));
			beanPropList.add(beanProp(TX_REQUIRED));
		}
	}

	protected java.util.Iterator beanPropsIterator() {
		prepareBeanPropList();
		return beanPropList.iterator();
	}

	public org.netbeans.modules.schema2beans.BeanProp[] beanProps() {
		prepareBeanPropList();
		org.netbeans.modules.schema2beans.BeanProp[] ret = new org.netbeans.modules.schema2beans.BeanProp[28];
		ret = (org.netbeans.modules.schema2beans.BeanProp[]) beanPropList.toArray(ret);
		return ret;
	}

	public void setValue(String name, Object value) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			setComments((java.lang.String[]) value);
		} else if (name == NAME || name == "name") {
			setName((java.lang.String)value);
		} else if (name == TABLE || name == "table") {
			setTable((java.lang.String)value);
		} else if (name == UUID || name == "uuid") {
			setUuid((java.lang.String)value);
		} else if (name == LOCALSERVICE || name == "local-service") {
			setLocalService((java.lang.String)value);
		} else if (name == REMOTESERVICE || name == "remote-service") {
			setRemoteService((java.lang.String)value);
		} else if (name == PERSISTENCECLASS || name == "persistence-class") {
			setPersistenceClass((java.lang.String)value);
		} else if (name == DATASOURCE || name == "data-source") {
			setDataSource((java.lang.String)value);
		} else if (name == SESSIONFACTORY || name == "session-factory") {
			setSessionFactory((java.lang.String)value);
		} else if (name == TXMANAGER || name == "tx-manager") {
			setTxManager((java.lang.String)value);
		} else if (name == CACHEENABLED || name == "cache-enabled") {
			setCacheEnabled((java.lang.String)value);
		} else if (name == COLUMN || name == "column") {
			setColumn((String[]) value);
		} else if (name == COLUMNNAME || name == "name") {
			setColumnName((java.lang.String[]) value);
		} else if (name == COLUMNDBNAME || name == "db-name") {
			setColumnDbName((java.lang.String[]) value);
		} else if (name == COLUMNTYPE || name == "type") {
			setColumnType((java.lang.String[]) value);
		} else if (name == COLUMNPRIMARY || name == "primary") {
			setColumnPrimary((java.lang.String[]) value);
		} else if (name == COLUMNENTITY || name == "entity") {
			setColumnEntity((java.lang.String[]) value);
		} else if (name == COLUMNMAPPINGKEY || name == "mapping-key") {
			setColumnMappingKey((java.lang.String[]) value);
		} else if (name == COLUMNMAPPINGTABLE || name == "mapping-table") {
			setColumnMappingTable((java.lang.String[]) value);
		} else if (name == COLUMNIDTYPE || name == "id-type") {
			setColumnIdType((java.lang.String[]) value);
		} else if (name == COLUMNIDPARAM || name == "id-param") {
			setColumnIdParam((java.lang.String[]) value);
		} else if (name == COLUMNCONVERTNULL || name == "convert-null") {
			setColumnConvertNull((java.lang.String[]) value);
		} else if (name == ORDER || name == "order") {
			setOrder((Order)value);
		} else if (name == FINDER || name == "finder") {
			setFinder((Finder[]) value);
		} else if (name == REFERENCE || name == "reference") {
			setReference((String[]) value);
		} else if (name == REFERENCEPACKAGEPATH || name == "package-path") {
			setReferencePackagePath((java.lang.String[]) value);
		} else if (name == REFERENCEENTITY || name == "entity") {
			setReferenceEntity((java.lang.String[]) value);
		} else if (name == TX_REQUIRED || name == "tx-required") {
			setTxRequired((String[]) value);
		} else throw new IllegalArgumentException(name+" is not a valid property name for Entity");
	}

	public void setValue(String name, int index, Object value) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			setComments(index, (java.lang.String)value);
		} else if (name == NAME || name == "name") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == TABLE || name == "table") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == UUID || name == "uuid") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == LOCALSERVICE || name == "local-service") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == REMOTESERVICE || name == "remote-service") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == PERSISTENCECLASS || name == "persistence-class") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == DATASOURCE || name == "data-source") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == SESSIONFACTORY || name == "session-factory") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == TXMANAGER || name == "tx-manager") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == CACHEENABLED || name == "cache-enabled") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == COLUMN || name == "column") {
			setColumn(index, (String)value);
		} else if (name == COLUMNNAME || name == "name") {
			setColumnName(index, (java.lang.String)value);
		} else if (name == COLUMNDBNAME || name == "db-name") {
			setColumnDbName(index, (java.lang.String)value);
		} else if (name == COLUMNTYPE || name == "type") {
			setColumnType(index, (java.lang.String)value);
		} else if (name == COLUMNPRIMARY || name == "primary") {
			setColumnPrimary(index, (java.lang.String)value);
		} else if (name == COLUMNENTITY || name == "entity") {
			setColumnEntity(index, (java.lang.String)value);
		} else if (name == COLUMNMAPPINGKEY || name == "mapping-key") {
			setColumnMappingKey(index, (java.lang.String)value);
		} else if (name == COLUMNMAPPINGTABLE || name == "mapping-table") {
			setColumnMappingTable(index, (java.lang.String)value);
		} else if (name == COLUMNIDTYPE || name == "id-type") {
			setColumnIdType(index, (java.lang.String)value);
		} else if (name == COLUMNIDPARAM || name == "id-param") {
			setColumnIdParam(index, (java.lang.String)value);
		} else if (name == COLUMNCONVERTNULL || name == "convert-null") {
			setColumnConvertNull(index, (java.lang.String)value);
		} else if (name == ORDER || name == "order") {
			throw new IllegalArgumentException(name+" is not an indexed property for Entity");
		} else if (name == FINDER || name == "finder") {
			setFinder(index, (Finder)value);
		} else if (name == REFERENCE || name == "reference") {
			setReference(index, (String)value);
		} else if (name == REFERENCEPACKAGEPATH || name == "package-path") {
			setReferencePackagePath(index, (java.lang.String)value);
		} else if (name == REFERENCEENTITY || name == "entity") {
			setReferenceEntity(index, (java.lang.String)value);
		} else if (name == TX_REQUIRED || name == "tx-required") {
			setTxRequired(index, (String)value);
		} else throw new IllegalArgumentException(name+" is not a valid property name for Entity");
	}

	public Object getValue(String name) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			return getComments();
		} else if (name == NAME || name == "name") {
			return getName();
		} else if (name == TABLE || name == "table") {
			return getTable();
		} else if (name == UUID || name == "uuid") {
			return getUuid();
		} else if (name == LOCALSERVICE || name == "local-service") {
			return getLocalService();
		} else if (name == REMOTESERVICE || name == "remote-service") {
			return getRemoteService();
		} else if (name == PERSISTENCECLASS || name == "persistence-class") {
			return getPersistenceClass();
		} else if (name == DATASOURCE || name == "data-source") {
			return getDataSource();
		} else if (name == SESSIONFACTORY || name == "session-factory") {
			return getSessionFactory();
		} else if (name == TXMANAGER || name == "tx-manager") {
			return getTxManager();
		} else if (name == CACHEENABLED || name == "cache-enabled") {
			return getCacheEnabled();
		} else if (name == COLUMN || name == "column") {
			return getColumn();
		} else if (name == COLUMNNAME || name == "name") {
			return getColumnName();
		} else if (name == COLUMNDBNAME || name == "db-name") {
			return getColumnDbName();
		} else if (name == COLUMNTYPE || name == "type") {
			return getColumnType();
		} else if (name == COLUMNPRIMARY || name == "primary") {
			return getColumnPrimary();
		} else if (name == COLUMNENTITY || name == "entity") {
			return getColumnEntity();
		} else if (name == COLUMNMAPPINGKEY || name == "mapping-key") {
			return getColumnMappingKey();
		} else if (name == COLUMNMAPPINGTABLE || name == "mapping-table") {
			return getColumnMappingTable();
		} else if (name == COLUMNIDTYPE || name == "id-type") {
			return getColumnIdType();
		} else if (name == COLUMNIDPARAM || name == "id-param") {
			return getColumnIdParam();
		} else if (name == COLUMNCONVERTNULL || name == "convert-null") {
			return getColumnConvertNull();
		} else if (name == ORDER || name == "order") {
			return getOrder();
		} else if (name == FINDER || name == "finder") {
			return getFinder();
		} else if (name == REFERENCE || name == "reference") {
			return getReference();
		} else if (name == REFERENCEPACKAGEPATH || name == "package-path") {
			return getReferencePackagePath();
		} else if (name == REFERENCEENTITY || name == "entity") {
			return getReferenceEntity();
		} else if (name == TX_REQUIRED || name == "tx-required") {
			return getTxRequired();
		} else throw new IllegalArgumentException(name+" is not a valid property name for Entity");
	}

	public Object getValue(String name, int index) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			return getComments(index);
		} else if (name == COLUMN || name == "column") {
			return getColumn(index);
		} else if (name == COLUMNNAME || name == "name") {
			return getColumnName(index);
		} else if (name == COLUMNDBNAME || name == "db-name") {
			return getColumnDbName(index);
		} else if (name == COLUMNTYPE || name == "type") {
			return getColumnType(index);
		} else if (name == COLUMNPRIMARY || name == "primary") {
			return getColumnPrimary(index);
		} else if (name == COLUMNENTITY || name == "entity") {
			return getColumnEntity(index);
		} else if (name == COLUMNMAPPINGKEY || name == "mapping-key") {
			return getColumnMappingKey(index);
		} else if (name == COLUMNMAPPINGTABLE || name == "mapping-table") {
			return getColumnMappingTable(index);
		} else if (name == COLUMNIDTYPE || name == "id-type") {
			return getColumnIdType(index);
		} else if (name == COLUMNIDPARAM || name == "id-param") {
			return getColumnIdParam(index);
		} else if (name == COLUMNCONVERTNULL || name == "convert-null") {
			return getColumnConvertNull(index);
		} else if (name == FINDER || name == "finder") {
			return getFinder(index);
		} else if (name == REFERENCE || name == "reference") {
			return getReference(index);
		} else if (name == REFERENCEPACKAGEPATH || name == "package-path") {
			return getReferencePackagePath(index);
		} else if (name == REFERENCEENTITY || name == "entity") {
			return getReferenceEntity(index);
		} else if (name == TX_REQUIRED || name == "tx-required") {
			return getTxRequired(index);
		} else if (name == NAME || name == "name") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getName();
		} else if (name == TABLE || name == "table") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getTable();
		} else if (name == UUID || name == "uuid") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getUuid();
		} else if (name == LOCALSERVICE || name == "local-service") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getLocalService();
		} else if (name == REMOTESERVICE || name == "remote-service") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getRemoteService();
		} else if (name == PERSISTENCECLASS || name == "persistence-class") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getPersistenceClass();
		} else if (name == DATASOURCE || name == "data-source") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getDataSource();
		} else if (name == SESSIONFACTORY || name == "session-factory") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getSessionFactory();
		} else if (name == TXMANAGER || name == "tx-manager") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getTxManager();
		} else if (name == CACHEENABLED || name == "cache-enabled") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getCacheEnabled();
		} else if (name == ORDER || name == "order") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getOrder();
		} else throw new IllegalArgumentException(name+" is not a valid property name for Entity");
	}

	public void mergeUpdate(org.netbeans.modules.schema2beans.BaseBean sourceBean) {
		org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity source = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Entity) sourceBean;
		{
			java.lang.String[] srcProperty = source.getComments();
			setComments(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getName();
			setName(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getTable();
			setTable(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getUuid();
			setUuid(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getLocalService();
			setLocalService(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getRemoteService();
			setRemoteService(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getPersistenceClass();
			setPersistenceClass(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getDataSource();
			setDataSource(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getSessionFactory();
			setSessionFactory(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getTxManager();
			setTxManager(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getCacheEnabled();
			setCacheEnabled(srcProperty);
		}
		{
			String[] srcProperty = source.getColumn();
			setColumn(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnName();
			setColumnName(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnDbName();
			setColumnDbName(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnType();
			setColumnType(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnPrimary();
			setColumnPrimary(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnEntity();
			setColumnEntity(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnMappingKey();
			setColumnMappingKey(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnMappingTable();
			setColumnMappingTable(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnIdType();
			setColumnIdType(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnIdParam();
			setColumnIdParam(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getColumnConvertNull();
			setColumnConvertNull(srcProperty);
		}
		{
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order srcProperty = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order) source.getOrder();
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order dest;
			boolean needToSet = false;
			if (srcProperty == null) {
				dest = null;
				needToSet = true;
			} else {
				dest = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order) getOrder();
				if (dest == null) {
					// Use a temp variable, and store it after we've merged everything into it, so as to make it only 1 change event.
					dest = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order(srcProperty, this, false);
					needToSet = true;
				} else {
					dest.mergeUpdate(srcProperty);
				}
			}
			if (needToSet) {
				setOrder(dest);
			}
		}
		{
			org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder[] srcProperty = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder[]) source.getFinder();
			int destSize = sizeFinder();
			if (destSize == srcProperty.length) {
				for (int i = 0; i < srcProperty.length; ++i) {
					org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder dest;
					if (srcProperty[i] == null) {
						dest = null;
					} else {
						if (i < destSize) {
							dest = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder) getFinder(i);
						} else {
							dest = null;
						}
						if (dest == null) {
							// Use a temp variable, and store it after we've merged everything into it, so as to make it only 1 change event.
							dest = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder(srcProperty[i], this, false);
						} else {
							dest.mergeUpdate(srcProperty[i]);
						}
					}
					// Merge events were generated by the above dest.mergeUpdate, so just set it directly now.
					_Finder.set(i, dest);
				}
			} else {
				org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder[] destArray = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder[srcProperty.length];
				for (int i = 0; i < srcProperty.length; ++i) {
					org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder dest;
					if (srcProperty[i] == null) {
						dest = null;
					} else {
						if (i < destSize) {
							dest = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder) getFinder(i);
							if (!srcProperty[i].equals(dest)) {
								// It's different, so have it just dup the source one.
								dest = null;
							}
						} else {
							dest = null;
						}
						if (dest == null) {
							// Use a temp variable, and store it after we've merged everything into it, so as to make it only 1 change event.
							dest = new org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Finder(srcProperty[i], this, false);
						}
						destArray[i] = dest;
					}
				}
				setFinder(destArray);
			}
		}
		{
			String[] srcProperty = source.getReference();
			setReference(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getReferencePackagePath();
			setReferencePackagePath(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getReferenceEntity();
			setReferenceEntity(srcProperty);
		}
		{
			String[] srcProperty = source.getTxRequired();
			setTxRequired(srcProperty);
		}
	}

	/**
	 * @deprecated
	 */
	public void write(java.io.Writer out, String encoding) throws java.io.IOException {
		writeNode(out);
	}

	/**
	 * @deprecated
	 */
	public void write(java.io.OutputStream out) throws java.io.IOException {
		java.io.PrintWriter pw = new java.io.PrintWriter(out);
		writeNode(pw);
		pw.flush();
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
