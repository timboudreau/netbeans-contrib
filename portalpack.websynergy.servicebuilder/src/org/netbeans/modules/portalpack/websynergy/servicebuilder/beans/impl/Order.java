/**
 *	This generated bean class Order
 *	matches the schema element 'order'.
 *  The root bean class is ServiceBuilder
 *
 * @Generated
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl;

public class Order extends org.netbeans.modules.schema2beans.BaseBean
 implements  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order
            , org.netbeans.modules.schema2beans.Bean {
	public static final String COMMENTS = "Comments";	// NOI18N
	public static final String BY = "By";	// NOI18N
	public static final String ORDER_COLUMN = "OrderColumn";	// NOI18N
	public static final String ORDERCOLUMNNAME = "OrderColumnName";	// NOI18N
	public static final String ORDERCOLUMNCASESENSITIVE = "OrderColumnCaseSensitive";	// NOI18N
	public static final String ORDERCOLUMNORDERBY = "OrderColumnOrderBy";	// NOI18N

	private static final org.netbeans.modules.schema2beans.Version baseBeanRuntimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);
	private java.util.List _Comments = new java.util.ArrayList();	// List<java.lang.String>
	private java.lang.String _By;
	private java.util.List _OrderColumn = new java.util.ArrayList();	// List<String>
	private java.util.List _OrderColumnName = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _OrderColumnCaseSensitive = new java.util.ArrayList();	// List<java.lang.String>
	private java.util.List _OrderColumnOrderBy = new java.util.ArrayList();	// List<java.lang.String>
	private org.netbeans.modules.schema2beans.BaseBean parent;
	private java.beans.PropertyChangeSupport eventListeners;
	private java.util.Map propByName = new java.util.HashMap(8, 1.0f);
	private java.util.List beanPropList = null;	// List<org.netbeans.modules.schema2beans.BeanProp>

	/**
	 * Normal starting point constructor.
	 */
	public Order() {
		this(null, baseBeanRuntimeVersion);
	}

	/**
	 * This constructor is here for BaseBean compatibility.
	 */
	public Order(java.util.Vector comps, org.netbeans.modules.schema2beans.Version baseBeanRuntimeVersion) {
		super(null, baseBeanRuntimeVersion);
	}

	/**
	 * Required parameters constructor
	 */
	public Order(String[] orderColumn, java.lang.String[] orderColumnName) {
		super(null, baseBeanRuntimeVersion);
		if (orderColumn!= null) {
			((java.util.ArrayList) _OrderColumn).ensureCapacity(orderColumn.length);
			for (int i = 0; i < orderColumn.length; ++i) {
				_OrderColumn.add(orderColumn[i]);
			}
		}
		if (orderColumnName!= null) {
			((java.util.ArrayList) _OrderColumnName).ensureCapacity(orderColumnName.length);
			for (int i = 0; i < orderColumnName.length; ++i) {
				_OrderColumnName.add(orderColumnName[i]);
			}
		}
	}

	/**
	 * Deep copy
	 */
	public Order(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order source) {
		this(source, false);
	}

	/**
	 * Deep copy
	 * @param justData just copy the XML relevant data
	 */
	public Order(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order source, boolean justData) {
		this(source, null, justData);
	}

	/**
	 * Deep copy
	 */
	public Order(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData) {
		super(null, baseBeanRuntimeVersion);
		this.parent = parent;
		for (java.util.Iterator it = source._Comments.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_Comments.add(srcElement);
		}
		_By = source._By;
		for (java.util.Iterator it = source._OrderColumn.iterator(); 
			it.hasNext(); ) {
			String srcElement = (String)it.next();
			_OrderColumn.add(srcElement);
		}
		for (java.util.Iterator it = source._OrderColumnName.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_OrderColumnName.add(srcElement);
		}
		for (java.util.Iterator it = source._OrderColumnCaseSensitive.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_OrderColumnCaseSensitive.add(srcElement);
		}
		for (java.util.Iterator it = source._OrderColumnOrderBy.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_OrderColumnOrderBy.add(srcElement);
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

	// This attribute is optional
	public void setBy(java.lang.String value) {
		if (value == null ? _By == null : value.equals(_By)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/By", getBy(), value);
		}
		_By = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getBy() {
		return _By;
	}

	// This attribute is an array containing at least one element
	public void setOrderColumn(String[] value) {
		if (value == null)
			value = new String[0];
		if (value.length == sizeOrderColumn()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getOrderColumn(i) == null : value[i].equals(getOrderColumn(i)))) {
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
			int oldSize = sizeOrderColumn();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getOrderColumn(oldIndex) == null : value[newIndex].equals(getOrderColumn(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getOrderColumn(oldIndex+1) == null : value[newIndex].equals(getOrderColumn(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getOrderColumn(oldIndex) == null : value[newIndex+1].equals(getOrderColumn(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumn."+Integer.toHexString(addIndex), null, value[addIndex]);
				_OrderColumn.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumn."+Integer.toHexString(removeIndex), getOrderColumn(removeIndex), null);
				_OrderColumn.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumn.-1", getOrderColumn(), value);
			}
		}
		_OrderColumn.clear();
		((java.util.ArrayList) _OrderColumn).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_OrderColumn.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setOrderColumn(int index, String value) {
		if (value == null ? getOrderColumn(index) == null : value.equals(getOrderColumn(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumn."+Integer.toHexString(index), getOrderColumn(index), value);
			eventListeners.firePropertyChange(event);
		}
		_OrderColumn.set(index, value);
	}

	public String[] getOrderColumn() {
		String[] arr = new String[_OrderColumn.size()];
		return (String[]) _OrderColumn.toArray(arr);
	}

	public java.util.List fetchOrderColumnList() {
		return _OrderColumn;
	}

	public String getOrderColumn(int index) {
		return (String)_OrderColumn.get(index);
	}

	// Return the number of orderColumn
	public int sizeOrderColumn() {
		return _OrderColumn.size();
	}

	public int addOrderColumn(String value) {
		_OrderColumn.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumn."+Integer.toHexString(_OrderColumn.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _OrderColumn.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeOrderColumn(String value) {
		int pos = _OrderColumn.indexOf(value);
		if (pos >= 0) {
			_OrderColumn.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumn."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array containing at least one element
	public void setOrderColumnName(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeOrderColumnName()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getOrderColumnName(i) == null : value[i].equals(getOrderColumnName(i)))) {
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
			int oldSize = sizeOrderColumnName();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getOrderColumnName(oldIndex) == null : value[newIndex].equals(getOrderColumnName(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getOrderColumnName(oldIndex+1) == null : value[newIndex].equals(getOrderColumnName(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getOrderColumnName(oldIndex) == null : value[newIndex+1].equals(getOrderColumnName(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnName."+Integer.toHexString(addIndex), null, value[addIndex]);
				_OrderColumnName.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnName."+Integer.toHexString(removeIndex), getOrderColumnName(removeIndex), null);
				_OrderColumnName.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnName.-1", getOrderColumnName(), value);
			}
		}
		_OrderColumnName.clear();
		((java.util.ArrayList) _OrderColumnName).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_OrderColumnName.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setOrderColumnName(int index, java.lang.String value) {
		if (value == null ? getOrderColumnName(index) == null : value.equals(getOrderColumnName(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnName."+Integer.toHexString(index), getOrderColumnName(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _OrderColumnName.size(); index >= size; ++size) {
			_OrderColumnName.add(null);
		}
		_OrderColumnName.set(index, value);
	}

	public java.lang.String[] getOrderColumnName() {
		java.lang.String[] arr = new java.lang.String[_OrderColumnName.size()];
		return (java.lang.String[]) _OrderColumnName.toArray(arr);
	}

	public java.util.List fetchOrderColumnNameList() {
		return _OrderColumnName;
	}

	public java.lang.String getOrderColumnName(int index) {
		return (java.lang.String)_OrderColumnName.get(index);
	}

	// Return the number of orderColumnName
	public int sizeOrderColumnName() {
		return _OrderColumnName.size();
	}

	public int addOrderColumnName(java.lang.String value) {
		_OrderColumnName.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnName."+Integer.toHexString(_OrderColumnName.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _OrderColumnName.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeOrderColumnName(java.lang.String value) {
		int pos = _OrderColumnName.indexOf(value);
		if (pos >= 0) {
			_OrderColumnName.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnName."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setOrderColumnCaseSensitive(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeOrderColumnCaseSensitive()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getOrderColumnCaseSensitive(i) == null : value[i].equals(getOrderColumnCaseSensitive(i)))) {
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
			int oldSize = sizeOrderColumnCaseSensitive();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getOrderColumnCaseSensitive(oldIndex) == null : value[newIndex].equals(getOrderColumnCaseSensitive(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getOrderColumnCaseSensitive(oldIndex+1) == null : value[newIndex].equals(getOrderColumnCaseSensitive(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getOrderColumnCaseSensitive(oldIndex) == null : value[newIndex+1].equals(getOrderColumnCaseSensitive(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnCaseSensitive."+Integer.toHexString(addIndex), null, value[addIndex]);
				_OrderColumnCaseSensitive.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnCaseSensitive."+Integer.toHexString(removeIndex), getOrderColumnCaseSensitive(removeIndex), null);
				_OrderColumnCaseSensitive.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnCaseSensitive.-1", getOrderColumnCaseSensitive(), value);
			}
		}
		_OrderColumnCaseSensitive.clear();
		((java.util.ArrayList) _OrderColumnCaseSensitive).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_OrderColumnCaseSensitive.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setOrderColumnCaseSensitive(int index, java.lang.String value) {
		if (value == null ? getOrderColumnCaseSensitive(index) == null : value.equals(getOrderColumnCaseSensitive(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnCaseSensitive."+Integer.toHexString(index), getOrderColumnCaseSensitive(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _OrderColumnCaseSensitive.size(); index >= size; 
			++size) {
			_OrderColumnCaseSensitive.add(null);
		}
		_OrderColumnCaseSensitive.set(index, value);
	}

	public java.lang.String[] getOrderColumnCaseSensitive() {
		java.lang.String[] arr = new java.lang.String[_OrderColumnCaseSensitive.size()];
		return (java.lang.String[]) _OrderColumnCaseSensitive.toArray(arr);
	}

	public java.util.List fetchOrderColumnCaseSensitiveList() {
		return _OrderColumnCaseSensitive;
	}

	public java.lang.String getOrderColumnCaseSensitive(int index) {
		return (java.lang.String)_OrderColumnCaseSensitive.get(index);
	}

	// Return the number of orderColumnCaseSensitive
	public int sizeOrderColumnCaseSensitive() {
		return _OrderColumnCaseSensitive.size();
	}

	public int addOrderColumnCaseSensitive(java.lang.String value) {
		_OrderColumnCaseSensitive.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnCaseSensitive."+Integer.toHexString(_OrderColumnCaseSensitive.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _OrderColumnCaseSensitive.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeOrderColumnCaseSensitive(java.lang.String value) {
		int pos = _OrderColumnCaseSensitive.indexOf(value);
		if (pos >= 0) {
			_OrderColumnCaseSensitive.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnCaseSensitive."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	// This attribute is an array, possibly empty
	public void setOrderColumnOrderBy(java.lang.String[] value) {
		if (value == null)
			value = new java.lang.String[0];
		if (value.length == sizeOrderColumnOrderBy()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getOrderColumnOrderBy(i) == null : value[i].equals(getOrderColumnOrderBy(i)))) {
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
			int oldSize = sizeOrderColumnOrderBy();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getOrderColumnOrderBy(oldIndex) == null : value[newIndex].equals(getOrderColumnOrderBy(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getOrderColumnOrderBy(oldIndex+1) == null : value[newIndex].equals(getOrderColumnOrderBy(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getOrderColumnOrderBy(oldIndex) == null : value[newIndex+1].equals(getOrderColumnOrderBy(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnOrderBy."+Integer.toHexString(addIndex), null, value[addIndex]);
				_OrderColumnOrderBy.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnOrderBy."+Integer.toHexString(removeIndex), getOrderColumnOrderBy(removeIndex), null);
				_OrderColumnOrderBy.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnOrderBy.-1", getOrderColumnOrderBy(), value);
			}
		}
		_OrderColumnOrderBy.clear();
		((java.util.ArrayList) _OrderColumnOrderBy).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_OrderColumnOrderBy.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setOrderColumnOrderBy(int index, java.lang.String value) {
		if (value == null ? getOrderColumnOrderBy(index) == null : value.equals(getOrderColumnOrderBy(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnOrderBy."+Integer.toHexString(index), getOrderColumnOrderBy(index), value);
			eventListeners.firePropertyChange(event);
		}
		for (int size = _OrderColumnOrderBy.size(); index >= size; ++size) {
			_OrderColumnOrderBy.add(null);
		}
		_OrderColumnOrderBy.set(index, value);
	}

	public java.lang.String[] getOrderColumnOrderBy() {
		java.lang.String[] arr = new java.lang.String[_OrderColumnOrderBy.size()];
		return (java.lang.String[]) _OrderColumnOrderBy.toArray(arr);
	}

	public java.util.List fetchOrderColumnOrderByList() {
		return _OrderColumnOrderBy;
	}

	public java.lang.String getOrderColumnOrderBy(int index) {
		return (java.lang.String)_OrderColumnOrderBy.get(index);
	}

	// Return the number of orderColumnOrderBy
	public int sizeOrderColumnOrderBy() {
		return _OrderColumnOrderBy.size();
	}

	public int addOrderColumnOrderBy(java.lang.String value) {
		_OrderColumnOrderBy.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnOrderBy."+Integer.toHexString(_OrderColumnOrderBy.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _OrderColumnOrderBy.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeOrderColumnOrderBy(java.lang.String value) {
		int pos = _OrderColumnOrderBy.indexOf(value);
		if (pos >= 0) {
			_OrderColumnOrderBy.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/OrderColumnOrderBy."+Integer.toHexString(pos), value, null);
				eventListeners.firePropertyChange(event);
			}
		}
		return pos;
	}

	public void _setParent(org.netbeans.modules.schema2beans.BaseBean parent) {
		this.parent = parent;
	}

	public String _getXPathExpr() {
		if (parent == null) {
			return "/order";
		} else {
			String parentXPathExpr = parent._getXPathExpr();
			String myExpr = parent.nameChild(this, false, false, true);
			return parentXPathExpr + "/" + myExpr;
		}
	}

	public String _getXPathExpr(Object childObj) {
		String childName = nameChild(childObj, false, false, true);
		if (childName == null) {
			throw new IllegalArgumentException("childObj ("+childObj.toString()+") is not a child of this bean (Order).");
		}
		return _getXPathExpr() + "/" + childName;
	}

	public void writeNode(java.io.Writer out) throws java.io.IOException {
		String myName;
		if (parent == null) {
			myName = "order";
		} else {
			myName = parent.nameChild(this, false, true);
			if (myName == null) {
				myName = "order";
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
		// by is an attribute with namespace null
		if (_By != null) {
			out.write(" by='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _By, true);
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
		for (java.util.Iterator it = _OrderColumn.iterator(); 
			it.hasNext(); ) {
			String element = (String)it.next();
			if (element != null) {
				out.write(nextIndent);
				out.write("<order-column");	// NOI18N
				if (index < sizeOrderColumnName()) {
					// name is an attribute with namespace null
					if (getOrderColumnName(index) != null) {
						out.write(" name='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getOrderColumnName(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeOrderColumnCaseSensitive()) {
					// case-sensitive is an attribute with namespace null
					if (getOrderColumnCaseSensitive(index) != null) {
						out.write(" case-sensitive='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getOrderColumnCaseSensitive(index), true);
						out.write("'");	// NOI18N
					}
				}
				if (index < sizeOrderColumnOrderBy()) {
					// order-by is an attribute with namespace null
					if (getOrderColumnOrderBy(index) != null) {
						out.write(" order-by='");
						org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, getOrderColumnOrderBy(index), true);
						out.write("'");	// NOI18N
					}
				}
				out.write(">");	// NOI18N
				org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, element, false);
				out.write("</order-column>\n");	// NOI18N
			}
			++index;
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
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("by");
		if (attr != null) {
			attrValue = attr.getValue();
			_By = attrValue;
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
		else if (childNodeName == "order-column") {
			String aOrderColumn;
			aOrderColumn = childNodeValue;
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("name");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_OrderColumnName;
			processedValueFor_OrderColumnName = attrValue;
			addOrderColumnName(processedValueFor_OrderColumnName);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("case-sensitive");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_OrderColumnCaseSensitive;
			processedValueFor_OrderColumnCaseSensitive = attrValue;
			addOrderColumnCaseSensitive(processedValueFor_OrderColumnCaseSensitive);
			attr = (org.w3c.dom.Attr) attrs.getNamedItem("order-by");
			if (attr != null) {
				attrValue = attr.getValue();
			} else {
				attrValue = null;
			}
			java.lang.String processedValueFor_OrderColumnOrderBy;
			processedValueFor_OrderColumnOrderBy = attrValue;
			addOrderColumnOrderBy(processedValueFor_OrderColumnOrderBy);
			_OrderColumn.add(aOrderColumn);
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
		// Validating property by
		// Validating property orderColumn
		if (sizeOrderColumn() == 0) {
			throw new org.netbeans.modules.schema2beans.ValidateException("sizeOrderColumn() == 0", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "orderColumn", this);	// NOI18N
		}
		// Validating property orderColumnName
		if (sizeOrderColumnName() == 0) {
			throw new org.netbeans.modules.schema2beans.ValidateException("sizeOrderColumnName() == 0", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "orderColumnName", this);	// NOI18N
		}
		// Validating property orderColumnCaseSensitive
		// Validating property orderColumnOrderBy
	}

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		if (eventListeners == null) {
			eventListeners = new java.beans.PropertyChangeSupport(this);
		}
		eventListeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
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
	}

	public void changePropertyByName(String name, Object value) {
		if (name == null) return;
		name = name.intern();
		if (name == "comments")
			addComments((java.lang.String)value);
		else if (name == "comments[]")
			setComments((java.lang.String[]) value);
		else if (name == "by")
			setBy((java.lang.String)value);
		else if (name == "orderColumn")
			addOrderColumn((String)value);
		else if (name == "orderColumn[]")
			setOrderColumn((String[]) value);
		else if (name == "orderColumnName")
			addOrderColumnName((java.lang.String)value);
		else if (name == "orderColumnName[]")
			setOrderColumnName((java.lang.String[]) value);
		else if (name == "orderColumnCaseSensitive")
			addOrderColumnCaseSensitive((java.lang.String)value);
		else if (name == "orderColumnCaseSensitive[]")
			setOrderColumnCaseSensitive((java.lang.String[]) value);
		else if (name == "orderColumnOrderBy")
			addOrderColumnOrderBy((java.lang.String)value);
		else if (name == "orderColumnOrderBy[]")
			setOrderColumnOrderBy((java.lang.String[]) value);
		else
			throw new IllegalArgumentException(name+" is not a valid property name for Order");
	}

	public Object fetchPropertyByName(String name) {
		if (name == "comments[]")
			return getComments();
		if (name == "by")
			return getBy();
		if (name == "orderColumn[]")
			return getOrderColumn();
		if (name == "orderColumnName[]")
			return getOrderColumnName();
		if (name == "orderColumnCaseSensitive[]")
			return getOrderColumnCaseSensitive();
		if (name == "orderColumnOrderBy[]")
			return getOrderColumnOrderBy();
		throw new IllegalArgumentException(name+" is not a valid property name for Order");
	}

	public String nameSelf() {
		if (parent != null) {
			String parentName = parent.nameSelf();
			String myName = parent.nameChild(this, false, false);
			return parentName + "/" + myName;
		}
		return "Order";
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
			if (child == _By) {
				if (returnConstName) {
					return BY;
				} else if (returnSchemaName) {
					return "by";
				} else if (returnXPathName) {
					return "@by";
				} else {
					return "By";
				}
			}
			index = 0;
			for (java.util.Iterator it = _OrderColumn.iterator(); 
				it.hasNext(); ) {
				String element = (String)it.next();
				if (child == element) {
					if (returnConstName) {
						return ORDER_COLUMN;
					} else if (returnSchemaName) {
						return "order-column";
					} else if (returnXPathName) {
						return "order-column[position()="+index+"]";
					} else {
						return "OrderColumn."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _OrderColumnName.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return ORDERCOLUMNNAME;
					} else if (returnSchemaName) {
						return "name";
					} else if (returnXPathName) {
						return "@name[position()="+index+"]";
					} else {
						return "OrderColumnName."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _OrderColumnCaseSensitive.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return ORDERCOLUMNCASESENSITIVE;
					} else if (returnSchemaName) {
						return "case-sensitive";
					} else if (returnXPathName) {
						return "@case-sensitive[position()="+index+"]";
					} else {
						return "OrderColumnCaseSensitive."+Integer.toHexString(index);
					}
				}
				++index;
			}
			index = 0;
			for (java.util.Iterator it = _OrderColumnOrderBy.iterator(); 
				it.hasNext(); ) {
				java.lang.String element = (java.lang.String)it.next();
				if (child == element) {
					if (returnConstName) {
						return ORDERCOLUMNORDERBY;
					} else if (returnSchemaName) {
						return "order-by";
					} else if (returnXPathName) {
						return "@order-by[position()="+index+"]";
					} else {
						return "OrderColumnOrderBy."+Integer.toHexString(index);
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
	}

	public boolean equals(Object o) {
		return o instanceof org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order && equals((org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order) o);
	}

	public boolean equals(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order inst) {
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
		if (!(_By == null ? inst._By == null : _By.equals(inst._By))) {
			return false;
		}
		if (sizeOrderColumn() != inst.sizeOrderColumn())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _OrderColumn.iterator(), it2 = inst._OrderColumn.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			String element = (String)it.next();
			String element2 = (String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeOrderColumnName() != inst.sizeOrderColumnName())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _OrderColumnName.iterator(), it2 = inst._OrderColumnName.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeOrderColumnCaseSensitive() != inst.sizeOrderColumnCaseSensitive())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _OrderColumnCaseSensitive.iterator(), it2 = inst._OrderColumnCaseSensitive.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		if (sizeOrderColumnOrderBy() != inst.sizeOrderColumnOrderBy())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _OrderColumnOrderBy.iterator(), it2 = inst._OrderColumnOrderBy.iterator(); 
			it.hasNext() && it2.hasNext(); ) {
			java.lang.String element = (java.lang.String)it.next();
			java.lang.String element2 = (java.lang.String)it2.next();
			if (!(element == null ? element2 == null : element.equals(element2))) {
				return false;
			}
		}
		return true;
	}

	public int hashCode() {
		int result = 17;
		result = 37*result + (_Comments == null ? 0 : _Comments.hashCode());
		result = 37*result + (_By == null ? 0 : _By.hashCode());
		result = 37*result + (_OrderColumn == null ? 0 : _OrderColumn.hashCode());
		result = 37*result + (_OrderColumnName == null ? 0 : _OrderColumnName.hashCode());
		result = 37*result + (_OrderColumnCaseSensitive == null ? 0 : _OrderColumnCaseSensitive.hashCode());
		result = 37*result + (_OrderColumnOrderBy == null ? 0 : _OrderColumnOrderBy.hashCode());
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
				} else if (name == BY) {
					indexed = false;
					constName = BY;
					schemaName = "by";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setBy", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getBy", new Class[] {});
				} else if (name == ORDER_COLUMN) {
					indexed = true;
					constName = ORDER_COLUMN;
					schemaName = "order-column";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_1_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getOrderColumn", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getOrderColumn", new Class[] {});
					writer = getClass().getMethod("setOrderColumn", new Class[] {Integer.TYPE, String.class});
					arrayWriter = getClass().getMethod("setOrderColumn", new Class[] {String[].class});
					adder = getClass().getMethod("addOrderColumn", new Class[] {String.class});
					remover = getClass().getMethod("removeOrderColumn", new Class[] {String.class});
				} else if (name == ORDERCOLUMNNAME) {
					indexed = true;
					constName = ORDERCOLUMNNAME;
					schemaName = "name";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_1_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getOrderColumnName", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getOrderColumnName", new Class[] {});
					writer = getClass().getMethod("setOrderColumnName", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setOrderColumnName", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addOrderColumnName", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeOrderColumnName", new Class[] {java.lang.String.class});
				} else if (name == ORDERCOLUMNCASESENSITIVE) {
					indexed = true;
					constName = ORDERCOLUMNCASESENSITIVE;
					schemaName = "case-sensitive";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getOrderColumnCaseSensitive", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getOrderColumnCaseSensitive", new Class[] {});
					writer = getClass().getMethod("setOrderColumnCaseSensitive", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setOrderColumnCaseSensitive", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addOrderColumnCaseSensitive", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeOrderColumnCaseSensitive", new Class[] {java.lang.String.class});
				} else if (name == ORDERCOLUMNORDERBY) {
					indexed = true;
					constName = ORDERCOLUMNORDERBY;
					schemaName = "order-by";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getOrderColumnOrderBy", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getOrderColumnOrderBy", new Class[] {});
					writer = getClass().getMethod("setOrderColumnOrderBy", new Class[] {Integer.TYPE, java.lang.String.class});
					arrayWriter = getClass().getMethod("setOrderColumnOrderBy", new Class[] {java.lang.String[].class});
					adder = getClass().getMethod("addOrderColumnOrderBy", new Class[] {java.lang.String.class});
					remover = getClass().getMethod("removeOrderColumnOrderBy", new Class[] {java.lang.String.class});
				} else {
					// Check if name is a schema name.
					if (name == "comment") {
						prop = beanProp(COMMENTS);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "by") {
						prop = beanProp(BY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "order-column") {
						prop = beanProp(ORDER_COLUMN);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "name") {
						prop = beanProp(ORDERCOLUMNNAME);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "case-sensitive") {
						prop = beanProp(ORDERCOLUMNCASESENSITIVE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "order-by") {
						prop = beanProp(ORDERCOLUMNORDERBY);
						propByName.put(name, prop);
						return prop;
					}
					throw new IllegalArgumentException(name+" is not a valid property name for Order");
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
				prop = new org.netbeans.modules.schema2beans.ReflectiveBeanProp(this, "order", "Order", org.netbeans.modules.schema2beans.Common.TYPE_1 | org.netbeans.modules.schema2beans.Common.TYPE_BEAN, Order.class, isRoot(), null, null, null, null, null, null);
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
			return "order";
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
		return new Order(this, null, false);
	}

	public Object cloneData() {
		return new Order(this, null, true);
	}

	private void prepareBeanPropList() {
		if (beanPropList == null) {
			beanPropList = new java.util.ArrayList(6);
			beanPropList.add(beanProp(COMMENTS));
			beanPropList.add(beanProp(BY));
			beanPropList.add(beanProp(ORDER_COLUMN));
			beanPropList.add(beanProp(ORDERCOLUMNNAME));
			beanPropList.add(beanProp(ORDERCOLUMNCASESENSITIVE));
			beanPropList.add(beanProp(ORDERCOLUMNORDERBY));
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
		} else if (name == BY || name == "by") {
			setBy((java.lang.String)value);
		} else if (name == ORDER_COLUMN || name == "order-column") {
			setOrderColumn((String[]) value);
		} else if (name == ORDERCOLUMNNAME || name == "name") {
			setOrderColumnName((java.lang.String[]) value);
		} else if (name == ORDERCOLUMNCASESENSITIVE || name == "case-sensitive") {
			setOrderColumnCaseSensitive((java.lang.String[]) value);
		} else if (name == ORDERCOLUMNORDERBY || name == "order-by") {
			setOrderColumnOrderBy((java.lang.String[]) value);
		} else throw new IllegalArgumentException(name+" is not a valid property name for Order");
	}

	public void setValue(String name, int index, Object value) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			setComments(index, (java.lang.String)value);
		} else if (name == BY || name == "by") {
			throw new IllegalArgumentException(name+" is not an indexed property for Order");
		} else if (name == ORDER_COLUMN || name == "order-column") {
			setOrderColumn(index, (String)value);
		} else if (name == ORDERCOLUMNNAME || name == "name") {
			setOrderColumnName(index, (java.lang.String)value);
		} else if (name == ORDERCOLUMNCASESENSITIVE || name == "case-sensitive") {
			setOrderColumnCaseSensitive(index, (java.lang.String)value);
		} else if (name == ORDERCOLUMNORDERBY || name == "order-by") {
			setOrderColumnOrderBy(index, (java.lang.String)value);
		} else throw new IllegalArgumentException(name+" is not a valid property name for Order");
	}

	public Object getValue(String name) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			return getComments();
		} else if (name == BY || name == "by") {
			return getBy();
		} else if (name == ORDER_COLUMN || name == "order-column") {
			return getOrderColumn();
		} else if (name == ORDERCOLUMNNAME || name == "name") {
			return getOrderColumnName();
		} else if (name == ORDERCOLUMNCASESENSITIVE || name == "case-sensitive") {
			return getOrderColumnCaseSensitive();
		} else if (name == ORDERCOLUMNORDERBY || name == "order-by") {
			return getOrderColumnOrderBy();
		} else throw new IllegalArgumentException(name+" is not a valid property name for Order");
	}

	public Object getValue(String name, int index) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			return getComments(index);
		} else if (name == ORDER_COLUMN || name == "order-column") {
			return getOrderColumn(index);
		} else if (name == ORDERCOLUMNNAME || name == "name") {
			return getOrderColumnName(index);
		} else if (name == ORDERCOLUMNCASESENSITIVE || name == "case-sensitive") {
			return getOrderColumnCaseSensitive(index);
		} else if (name == ORDERCOLUMNORDERBY || name == "order-by") {
			return getOrderColumnOrderBy(index);
		} else if (name == BY || name == "by") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getBy();
		} else throw new IllegalArgumentException(name+" is not a valid property name for Order");
	}

	public void mergeUpdate(org.netbeans.modules.schema2beans.BaseBean sourceBean) {
		org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order source = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Order) sourceBean;
		{
			java.lang.String[] srcProperty = source.getComments();
			setComments(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getBy();
			setBy(srcProperty);
		}
		{
			String[] srcProperty = source.getOrderColumn();
			setOrderColumn(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getOrderColumnName();
			setOrderColumnName(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getOrderColumnCaseSensitive();
			setOrderColumnCaseSensitive(srcProperty);
		}
		{
			java.lang.String[] srcProperty = source.getOrderColumnOrderBy();
			setOrderColumnOrderBy(srcProperty);
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
<!-- original <!ELEMENT column (#PCDATA)> -->
<!-- modified -->

<!ELEMENT column (dummy_elm*)>
<!ELEMENT dummy_elm (#PCDATA)>

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
