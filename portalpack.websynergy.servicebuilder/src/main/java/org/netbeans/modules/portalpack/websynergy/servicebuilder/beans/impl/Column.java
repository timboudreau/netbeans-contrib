/**
 *	This generated bean class Column
 *	matches the schema element 'column'.
 *  The root bean class is ServiceBuilder
 *
 * @Generated
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl;

public class Column extends org.netbeans.modules.schema2beans.BaseBean
 implements  
                org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column
            , org.netbeans.modules.schema2beans.Bean {
	public static final String COMMENTS = "Comments";	// NOI18N
	public static final String NAME = "Name";	// NOI18N
	public static final String DBNAME = "DbName";	// NOI18N
	public static final String TYPE = "Type";	// NOI18N
	public static final String PRIMARY = "Primary";	// NOI18N
	public static final String ENTITY = "Entity";	// NOI18N
	public static final String MAPPINGKEY = "MappingKey";	// NOI18N
	public static final String MAPPINGTABLE = "MappingTable";	// NOI18N
	public static final String IDTYPE = "IdType";	// NOI18N
	public static final String IDPARAM = "IdParam";	// NOI18N
	public static final String CONVERTNULL = "ConvertNull";	// NOI18N
	public static final String DUMMY_ELM = "DummyElm";	// NOI18N

	private static final org.netbeans.modules.schema2beans.Version baseBeanRuntimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);
	private java.util.List _Comments = new java.util.ArrayList();	// List<java.lang.String>
	private java.lang.String _Name;
	private java.lang.String _DbName;
	private java.lang.String _Type;
	private java.lang.String _Primary;
	private java.lang.String _Entity;
	private java.lang.String _MappingKey;
	private java.lang.String _MappingTable;
	private java.lang.String _IdType;
	private java.lang.String _IdParam;
	private java.lang.String _ConvertNull;
	private java.util.List _DummyElm = new java.util.ArrayList();	// List<String>
	private org.netbeans.modules.schema2beans.BaseBean parent;
	private java.beans.PropertyChangeSupport eventListeners;
	private java.util.Map propByName = new java.util.HashMap(14, 1.0f);
	private java.util.List beanPropList = null;	// List<org.netbeans.modules.schema2beans.BeanProp>

	/**
	 * Normal starting point constructor.
	 */
	public Column() {
		this(null, baseBeanRuntimeVersion);
	}

	/**
	 * This constructor is here for BaseBean compatibility.
	 */
	public Column(java.util.Vector comps, org.netbeans.modules.schema2beans.Version baseBeanRuntimeVersion) {
		super(null, baseBeanRuntimeVersion);
		_Name = "";
		_Type = "";
	}

	/**
	 * Required parameters constructor
	 */
	public Column(java.lang.String name, java.lang.String type) {
		super(null, baseBeanRuntimeVersion);
		_Name = name;
		_Type = type;
	}

	/**
	 * Deep copy
	 */
	public Column(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Column source) {
		this(source, false);
	}

	/**
	 * Deep copy
	 * @param justData just copy the XML relevant data
	 */
	public Column(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Column source, boolean justData) {
		this(source, null, justData);
	}

	/**
	 * Deep copy
	 */
	public Column(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Column source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData) {
		super(null, baseBeanRuntimeVersion);
		this.parent = parent;
		for (java.util.Iterator it = source._Comments.iterator(); 
			it.hasNext(); ) {
			java.lang.String srcElement = (java.lang.String)it.next();
			_Comments.add(srcElement);
		}
		_Name = source._Name;
		_DbName = source._DbName;
		_Type = source._Type;
		_Primary = source._Primary;
		_Entity = source._Entity;
		_MappingKey = source._MappingKey;
		_MappingTable = source._MappingTable;
		_IdType = source._IdType;
		_IdParam = source._IdParam;
		_ConvertNull = source._ConvertNull;
		for (java.util.Iterator it = source._DummyElm.iterator(); 
			it.hasNext(); ) {
			String srcElement = (String)it.next();
			_DummyElm.add(srcElement);
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
	public void setDbName(java.lang.String value) {
		if (value == null ? _DbName == null : value.equals(_DbName)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/DbName", getDbName(), value);
		}
		_DbName = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getDbName() {
		return _DbName;
	}

	// This attribute is mandatory
	public void setType(java.lang.String value) {
		if (value == null ? _Type == null : value.equals(_Type)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Type", getType(), value);
		}
		_Type = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getType() {
		return _Type;
	}

	// This attribute is optional
	public void setPrimary(java.lang.String value) {
		if (value == null ? _Primary == null : value.equals(_Primary)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Primary", getPrimary(), value);
		}
		_Primary = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getPrimary() {
		return _Primary;
	}

	// This attribute is optional
	public void setEntity(java.lang.String value) {
		if (value == null ? _Entity == null : value.equals(_Entity)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/Entity", getEntity(), value);
		}
		_Entity = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getEntity() {
		return _Entity;
	}

	// This attribute is optional
	public void setMappingKey(java.lang.String value) {
		if (value == null ? _MappingKey == null : value.equals(_MappingKey)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/MappingKey", getMappingKey(), value);
		}
		_MappingKey = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getMappingKey() {
		return _MappingKey;
	}

	// This attribute is optional
	public void setMappingTable(java.lang.String value) {
		if (value == null ? _MappingTable == null : value.equals(_MappingTable)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/MappingTable", getMappingTable(), value);
		}
		_MappingTable = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getMappingTable() {
		return _MappingTable;
	}

	// This attribute is optional
	public void setIdType(java.lang.String value) {
		if (value == null ? _IdType == null : value.equals(_IdType)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/IdType", getIdType(), value);
		}
		_IdType = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getIdType() {
		return _IdType;
	}

	// This attribute is optional
	public void setIdParam(java.lang.String value) {
		if (value == null ? _IdParam == null : value.equals(_IdParam)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/IdParam", getIdParam(), value);
		}
		_IdParam = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getIdParam() {
		return _IdParam;
	}

	// This attribute is optional
	public void setConvertNull(java.lang.String value) {
		if (value == null ? _ConvertNull == null : value.equals(_ConvertNull)) {
			// No change.
			return;
		}
		java.beans.PropertyChangeEvent event = null;
		if (eventListeners != null) {
			event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/ConvertNull", getConvertNull(), value);
		}
		_ConvertNull = value;
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public java.lang.String getConvertNull() {
		return _ConvertNull;
	}

	// This attribute is an array, possibly empty
	public void setDummyElm(String[] value) {
		if (value == null)
			value = new String[0];
		if (value.length == sizeDummyElm()) {
			boolean same = true;
			for (int i = 0; i < value.length; ++i) {
				if (!(value[i] == null ? getDummyElm(i) == null : value[i].equals(getDummyElm(i)))) {
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
			int oldSize = sizeDummyElm();
			int newSize = value.length;
			if (oldSize + 1 == newSize || oldSize == newSize + 1) {
				boolean checkAddOrRemoveOne = true;
				int oldIndex = 0, newIndex = 0;
				for (; oldIndex < oldSize && newIndex < newSize; 
					++newIndex, ++oldIndex) {
					if (value[newIndex] == null ? getDummyElm(oldIndex) == null : value[newIndex].equals(getDummyElm(oldIndex))) {
						// Same, so just continue.
					} else if (addIndex != -1 || removeIndex != -1) {
						// More than 1 difference detected.
						addIndex = removeIndex = -1;
						checkAddOrRemoveOne = false;
						break;
					} else if (oldIndex + 1 < oldSize && (value[newIndex] == null ? getDummyElm(oldIndex+1) == null : value[newIndex].equals(getDummyElm(oldIndex+1)))) {
						removeIndex = oldIndex;
						++oldIndex;
					} else if (newIndex + 1 < newSize && (value[newIndex+1] == null ? getDummyElm(oldIndex) == null : value[newIndex+1].equals(getDummyElm(oldIndex)))) {
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
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/DummyElm."+Integer.toHexString(addIndex), null, value[addIndex]);
				_DummyElm.add(addIndex, value[addIndex]);
				eventListeners.firePropertyChange(event);
				return;
			} else if (removeIndex >= 0) {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/DummyElm."+Integer.toHexString(removeIndex), getDummyElm(removeIndex), null);
				_DummyElm.remove(removeIndex);
				eventListeners.firePropertyChange(event);
				return;
			} else {
				event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/DummyElm.-1", getDummyElm(), value);
			}
		}
		_DummyElm.clear();
		((java.util.ArrayList) _DummyElm).ensureCapacity(value.length);
		for (int i = 0; i < value.length; ++i) {
			_DummyElm.add(value[i]);
		}
		if (event != null)
			eventListeners.firePropertyChange(event);
	}

	public void setDummyElm(int index, String value) {
		if (value == null ? getDummyElm(index) == null : value.equals(getDummyElm(index))) {
			// No change.
			return;
		}
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/DummyElm."+Integer.toHexString(index), getDummyElm(index), value);
			eventListeners.firePropertyChange(event);
		}
		_DummyElm.set(index, value);
	}

	public String[] getDummyElm() {
		String[] arr = new String[_DummyElm.size()];
		return (String[]) _DummyElm.toArray(arr);
	}

	public java.util.List fetchDummyElmList() {
		return _DummyElm;
	}

	public String getDummyElm(int index) {
		return (String)_DummyElm.get(index);
	}

	// Return the number of dummyElm
	public int sizeDummyElm() {
		return _DummyElm.size();
	}

	public int addDummyElm(String value) {
		_DummyElm.add(value);
		if (eventListeners != null) {
			java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/DummyElm."+Integer.toHexString(_DummyElm.size()-1), null, value);
			eventListeners.firePropertyChange(event);
		}
		int positionOfNewItem = _DummyElm.size()-1;
		return positionOfNewItem;
	}

	/**
	 * Search from the end looking for @param value, and then remove it.
	 */
	public int removeDummyElm(String value) {
		int pos = _DummyElm.indexOf(value);
		if (pos >= 0) {
			_DummyElm.remove(pos);
			if (eventListeners != null) {
				java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, nameSelf()+"/DummyElm."+Integer.toHexString(pos), value, null);
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
			return "/column";
		} else {
			String parentXPathExpr = parent._getXPathExpr();
			String myExpr = parent.nameChild(this, false, false, true);
			return parentXPathExpr + "/" + myExpr;
		}
	}

	public String _getXPathExpr(Object childObj) {
		String childName = nameChild(childObj, false, false, true);
		if (childName == null) {
			throw new IllegalArgumentException("childObj ("+childObj.toString()+") is not a child of this bean (Column).");
		}
		return _getXPathExpr() + "/" + childName;
	}

	public void writeNode(java.io.Writer out) throws java.io.IOException {
		String myName;
		if (parent == null) {
			myName = "column";
		} else {
			myName = parent.nameChild(this, false, true);
			if (myName == null) {
				myName = "column";
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
		// db-name is an attribute with namespace null
		if (_DbName != null) {
			out.write(" db-name='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _DbName, true);
			out.write("'");	// NOI18N
		}
		// type is an attribute with namespace null
		if (_Type != null) {
			out.write(" type='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _Type, true);
			out.write("'");	// NOI18N
		}
		// primary is an attribute with namespace null
		if (_Primary != null) {
			out.write(" primary='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _Primary, true);
			out.write("'");	// NOI18N
		}
		// entity is an attribute with namespace null
		if (_Entity != null) {
			out.write(" entity='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _Entity, true);
			out.write("'");	// NOI18N
		}
		// mapping-key is an attribute with namespace null
		if (_MappingKey != null) {
			out.write(" mapping-key='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _MappingKey, true);
			out.write("'");	// NOI18N
		}
		// mapping-table is an attribute with namespace null
		if (_MappingTable != null) {
			out.write(" mapping-table='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _MappingTable, true);
			out.write("'");	// NOI18N
		}
		// id-type is an attribute with namespace null
		if (_IdType != null) {
			out.write(" id-type='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _IdType, true);
			out.write("'");	// NOI18N
		}
		// id-param is an attribute with namespace null
		if (_IdParam != null) {
			out.write(" id-param='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _IdParam, true);
			out.write("'");	// NOI18N
		}
		// convert-null is an attribute with namespace null
		if (_ConvertNull != null) {
			out.write(" convert-null='");
			org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, _ConvertNull, true);
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
		for (java.util.Iterator it = _DummyElm.iterator(); it.hasNext(); ) {
			String element = (String)it.next();
			if (element != null) {
				out.write(nextIndent);
				out.write("<dummy_elm");	// NOI18N
				out.write(">");	// NOI18N
				org.netbeans.modules.schema2beans.XMLUtil.writeXML(out, element, false);
				out.write("</dummy_elm>\n");	// NOI18N
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
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("db-name");
		if (attr != null) {
			attrValue = attr.getValue();
			_DbName = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("type");
		if (attr != null) {
			attrValue = attr.getValue();
			_Type = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("primary");
		if (attr != null) {
			attrValue = attr.getValue();
			_Primary = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("entity");
		if (attr != null) {
			attrValue = attr.getValue();
			_Entity = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("mapping-key");
		if (attr != null) {
			attrValue = attr.getValue();
			_MappingKey = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("mapping-table");
		if (attr != null) {
			attrValue = attr.getValue();
			_MappingTable = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("id-type");
		if (attr != null) {
			attrValue = attr.getValue();
			_IdType = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("id-param");
		if (attr != null) {
			attrValue = attr.getValue();
			_IdParam = attrValue;
		}
		attr = (org.w3c.dom.Attr) attrs.getNamedItem("convert-null");
		if (attr != null) {
			attrValue = attr.getValue();
			_ConvertNull = attrValue;
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
		else if (childNodeName == "dummy_elm") {
			String aDummyElm;
			aDummyElm = childNodeValue;
			_DummyElm.add(aDummyElm);
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
		// Validating property dbName
		// Validating property type
		if (getType() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getType() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "type", this);	// NOI18N
		}
		// Validating property primary
		// Validating property entity
		// Validating property mappingKey
		// Validating property mappingTable
		// Validating property idType
		// Validating property idParam
		// Validating property convertNull
		// Validating property dummyElm
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
		else if (name == "name")
			setName((java.lang.String)value);
		else if (name == "dbName")
			setDbName((java.lang.String)value);
		else if (name == "type")
			setType((java.lang.String)value);
		else if (name == "primary")
			setPrimary((java.lang.String)value);
		else if (name == "entity")
			setEntity((java.lang.String)value);
		else if (name == "mappingKey")
			setMappingKey((java.lang.String)value);
		else if (name == "mappingTable")
			setMappingTable((java.lang.String)value);
		else if (name == "idType")
			setIdType((java.lang.String)value);
		else if (name == "idParam")
			setIdParam((java.lang.String)value);
		else if (name == "convertNull")
			setConvertNull((java.lang.String)value);
		else if (name == "dummyElm")
			addDummyElm((String)value);
		else if (name == "dummyElm[]")
			setDummyElm((String[]) value);
		else
			throw new IllegalArgumentException(name+" is not a valid property name for Column");
	}

	public Object fetchPropertyByName(String name) {
		if (name == "comments[]")
			return getComments();
		if (name == "name")
			return getName();
		if (name == "dbName")
			return getDbName();
		if (name == "type")
			return getType();
		if (name == "primary")
			return getPrimary();
		if (name == "entity")
			return getEntity();
		if (name == "mappingKey")
			return getMappingKey();
		if (name == "mappingTable")
			return getMappingTable();
		if (name == "idType")
			return getIdType();
		if (name == "idParam")
			return getIdParam();
		if (name == "convertNull")
			return getConvertNull();
		if (name == "dummyElm[]")
			return getDummyElm();
		throw new IllegalArgumentException(name+" is not a valid property name for Column");
	}

	public String nameSelf() {
		if (parent != null) {
			String parentName = parent.nameSelf();
			String myName = parent.nameChild(this, false, false);
			return parentName + "/" + myName;
		}
		return "Column";
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
			if (child == _DbName) {
				if (returnConstName) {
					return DBNAME;
				} else if (returnSchemaName) {
					return "db-name";
				} else if (returnXPathName) {
					return "@db-name";
				} else {
					return "DbName";
				}
			}
			if (child == _Type) {
				if (returnConstName) {
					return TYPE;
				} else if (returnSchemaName) {
					return "type";
				} else if (returnXPathName) {
					return "@type";
				} else {
					return "Type";
				}
			}
			if (child == _Primary) {
				if (returnConstName) {
					return PRIMARY;
				} else if (returnSchemaName) {
					return "primary";
				} else if (returnXPathName) {
					return "@primary";
				} else {
					return "Primary";
				}
			}
			if (child == _Entity) {
				if (returnConstName) {
					return ENTITY;
				} else if (returnSchemaName) {
					return "entity";
				} else if (returnXPathName) {
					return "@entity";
				} else {
					return "Entity";
				}
			}
			if (child == _MappingKey) {
				if (returnConstName) {
					return MAPPINGKEY;
				} else if (returnSchemaName) {
					return "mapping-key";
				} else if (returnXPathName) {
					return "@mapping-key";
				} else {
					return "MappingKey";
				}
			}
			if (child == _MappingTable) {
				if (returnConstName) {
					return MAPPINGTABLE;
				} else if (returnSchemaName) {
					return "mapping-table";
				} else if (returnXPathName) {
					return "@mapping-table";
				} else {
					return "MappingTable";
				}
			}
			if (child == _IdType) {
				if (returnConstName) {
					return IDTYPE;
				} else if (returnSchemaName) {
					return "id-type";
				} else if (returnXPathName) {
					return "@id-type";
				} else {
					return "IdType";
				}
			}
			if (child == _IdParam) {
				if (returnConstName) {
					return IDPARAM;
				} else if (returnSchemaName) {
					return "id-param";
				} else if (returnXPathName) {
					return "@id-param";
				} else {
					return "IdParam";
				}
			}
			if (child == _ConvertNull) {
				if (returnConstName) {
					return CONVERTNULL;
				} else if (returnSchemaName) {
					return "convert-null";
				} else if (returnXPathName) {
					return "@convert-null";
				} else {
					return "ConvertNull";
				}
			}
			index = 0;
			for (java.util.Iterator it = _DummyElm.iterator(); 
				it.hasNext(); ) {
				String element = (String)it.next();
				if (child == element) {
					if (returnConstName) {
						return DUMMY_ELM;
					} else if (returnSchemaName) {
						return "dummy_elm";
					} else if (returnXPathName) {
						return "dummy_elm[position()="+index+"]";
					} else {
						return "DummyElm."+Integer.toHexString(index);
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
		return o instanceof org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Column && equals((org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Column) o);
	}

	public boolean equals(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Column inst) {
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
		if (!(_DbName == null ? inst._DbName == null : _DbName.equals(inst._DbName))) {
			return false;
		}
		if (!(_Type == null ? inst._Type == null : _Type.equals(inst._Type))) {
			return false;
		}
		if (!(_Primary == null ? inst._Primary == null : _Primary.equals(inst._Primary))) {
			return false;
		}
		if (!(_Entity == null ? inst._Entity == null : _Entity.equals(inst._Entity))) {
			return false;
		}
		if (!(_MappingKey == null ? inst._MappingKey == null : _MappingKey.equals(inst._MappingKey))) {
			return false;
		}
		if (!(_MappingTable == null ? inst._MappingTable == null : _MappingTable.equals(inst._MappingTable))) {
			return false;
		}
		if (!(_IdType == null ? inst._IdType == null : _IdType.equals(inst._IdType))) {
			return false;
		}
		if (!(_IdParam == null ? inst._IdParam == null : _IdParam.equals(inst._IdParam))) {
			return false;
		}
		if (!(_ConvertNull == null ? inst._ConvertNull == null : _ConvertNull.equals(inst._ConvertNull))) {
			return false;
		}
		if (sizeDummyElm() != inst.sizeDummyElm())
			return false;
		// Compare every element.
		for (java.util.Iterator it = _DummyElm.iterator(), it2 = inst._DummyElm.iterator(); 
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
		result = 37*result + (_DbName == null ? 0 : _DbName.hashCode());
		result = 37*result + (_Type == null ? 0 : _Type.hashCode());
		result = 37*result + (_Primary == null ? 0 : _Primary.hashCode());
		result = 37*result + (_Entity == null ? 0 : _Entity.hashCode());
		result = 37*result + (_MappingKey == null ? 0 : _MappingKey.hashCode());
		result = 37*result + (_MappingTable == null ? 0 : _MappingTable.hashCode());
		result = 37*result + (_IdType == null ? 0 : _IdType.hashCode());
		result = 37*result + (_IdParam == null ? 0 : _IdParam.hashCode());
		result = 37*result + (_ConvertNull == null ? 0 : _ConvertNull.hashCode());
		result = 37*result + (_DummyElm == null ? 0 : _DummyElm.hashCode());
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
				} else if (name == DBNAME) {
					indexed = false;
					constName = DBNAME;
					schemaName = "db-name";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setDbName", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getDbName", new Class[] {});
				} else if (name == TYPE) {
					indexed = false;
					constName = TYPE;
					schemaName = "type";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setType", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getType", new Class[] {});
				} else if (name == PRIMARY) {
					indexed = false;
					constName = PRIMARY;
					schemaName = "primary";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setPrimary", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getPrimary", new Class[] {});
				} else if (name == ENTITY) {
					indexed = false;
					constName = ENTITY;
					schemaName = "entity";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setEntity", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getEntity", new Class[] {});
				} else if (name == MAPPINGKEY) {
					indexed = false;
					constName = MAPPINGKEY;
					schemaName = "mapping-key";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setMappingKey", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getMappingKey", new Class[] {});
				} else if (name == MAPPINGTABLE) {
					indexed = false;
					constName = MAPPINGTABLE;
					schemaName = "mapping-table";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setMappingTable", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getMappingTable", new Class[] {});
				} else if (name == IDTYPE) {
					indexed = false;
					constName = IDTYPE;
					schemaName = "id-type";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setIdType", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getIdType", new Class[] {});
				} else if (name == IDPARAM) {
					indexed = false;
					constName = IDPARAM;
					schemaName = "id-param";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setIdParam", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getIdParam", new Class[] {});
				} else if (name == CONVERTNULL) {
					indexed = false;
					constName = CONVERTNULL;
					schemaName = "convert-null";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_1|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					writer = getClass().getMethod("setConvertNull", new Class[] {java.lang.String.class});
					reader = getClass().getMethod("getConvertNull", new Class[] {});
				} else if (name == DUMMY_ELM) {
					indexed = true;
					constName = DUMMY_ELM;
					schemaName = "dummy_elm";
					options = org.netbeans.modules.schema2beans.Common.TYPE_KEY | org.netbeans.modules.schema2beans.Common.TYPE_0_N|org.netbeans.modules.schema2beans.Common.TYPE_STRING;
					reader = getClass().getMethod("getDummyElm", new Class[] {Integer.TYPE});
					arrayReader = getClass().getMethod("getDummyElm", new Class[] {});
					writer = getClass().getMethod("setDummyElm", new Class[] {Integer.TYPE, String.class});
					arrayWriter = getClass().getMethod("setDummyElm", new Class[] {String[].class});
					adder = getClass().getMethod("addDummyElm", new Class[] {String.class});
					remover = getClass().getMethod("removeDummyElm", new Class[] {String.class});
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
					if (name == "db-name") {
						prop = beanProp(DBNAME);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "type") {
						prop = beanProp(TYPE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "primary") {
						prop = beanProp(PRIMARY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "entity") {
						prop = beanProp(ENTITY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "mapping-key") {
						prop = beanProp(MAPPINGKEY);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "mapping-table") {
						prop = beanProp(MAPPINGTABLE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "id-type") {
						prop = beanProp(IDTYPE);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "id-param") {
						prop = beanProp(IDPARAM);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "convert-null") {
						prop = beanProp(CONVERTNULL);
						propByName.put(name, prop);
						return prop;
					}
					if (name == "dummy_elm") {
						prop = beanProp(DUMMY_ELM);
						propByName.put(name, prop);
						return prop;
					}
					throw new IllegalArgumentException(name+" is not a valid property name for Column");
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
				prop = new org.netbeans.modules.schema2beans.ReflectiveBeanProp(this, "column", "Column", org.netbeans.modules.schema2beans.Common.TYPE_1 | org.netbeans.modules.schema2beans.Common.TYPE_BEAN, Column.class, isRoot(), null, null, null, null, null, null);
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
			return "column";
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
		return new Column(this, null, false);
	}

	public Object cloneData() {
		return new Column(this, null, true);
	}

	private void prepareBeanPropList() {
		if (beanPropList == null) {
			beanPropList = new java.util.ArrayList(12);
			beanPropList.add(beanProp(COMMENTS));
			beanPropList.add(beanProp(NAME));
			beanPropList.add(beanProp(DBNAME));
			beanPropList.add(beanProp(TYPE));
			beanPropList.add(beanProp(PRIMARY));
			beanPropList.add(beanProp(ENTITY));
			beanPropList.add(beanProp(MAPPINGKEY));
			beanPropList.add(beanProp(MAPPINGTABLE));
			beanPropList.add(beanProp(IDTYPE));
			beanPropList.add(beanProp(IDPARAM));
			beanPropList.add(beanProp(CONVERTNULL));
			beanPropList.add(beanProp(DUMMY_ELM));
		}
	}

	protected java.util.Iterator beanPropsIterator() {
		prepareBeanPropList();
		return beanPropList.iterator();
	}

	public org.netbeans.modules.schema2beans.BeanProp[] beanProps() {
		prepareBeanPropList();
		org.netbeans.modules.schema2beans.BeanProp[] ret = new org.netbeans.modules.schema2beans.BeanProp[12];
		ret = (org.netbeans.modules.schema2beans.BeanProp[]) beanPropList.toArray(ret);
		return ret;
	}

	public void setValue(String name, Object value) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			setComments((java.lang.String[]) value);
		} else if (name == NAME || name == "name") {
			setName((java.lang.String)value);
		} else if (name == DBNAME || name == "db-name") {
			setDbName((java.lang.String)value);
		} else if (name == TYPE || name == "type") {
			setType((java.lang.String)value);
		} else if (name == PRIMARY || name == "primary") {
			setPrimary((java.lang.String)value);
		} else if (name == ENTITY || name == "entity") {
			setEntity((java.lang.String)value);
		} else if (name == MAPPINGKEY || name == "mapping-key") {
			setMappingKey((java.lang.String)value);
		} else if (name == MAPPINGTABLE || name == "mapping-table") {
			setMappingTable((java.lang.String)value);
		} else if (name == IDTYPE || name == "id-type") {
			setIdType((java.lang.String)value);
		} else if (name == IDPARAM || name == "id-param") {
			setIdParam((java.lang.String)value);
		} else if (name == CONVERTNULL || name == "convert-null") {
			setConvertNull((java.lang.String)value);
		} else if (name == DUMMY_ELM || name == "dummy_elm") {
			setDummyElm((String[]) value);
		} else throw new IllegalArgumentException(name+" is not a valid property name for Column");
	}

	public void setValue(String name, int index, Object value) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			setComments(index, (java.lang.String)value);
		} else if (name == NAME || name == "name") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == DBNAME || name == "db-name") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == TYPE || name == "type") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == PRIMARY || name == "primary") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == ENTITY || name == "entity") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == MAPPINGKEY || name == "mapping-key") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == MAPPINGTABLE || name == "mapping-table") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == IDTYPE || name == "id-type") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == IDPARAM || name == "id-param") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == CONVERTNULL || name == "convert-null") {
			throw new IllegalArgumentException(name+" is not an indexed property for Column");
		} else if (name == DUMMY_ELM || name == "dummy_elm") {
			setDummyElm(index, (String)value);
		} else throw new IllegalArgumentException(name+" is not a valid property name for Column");
	}

	public Object getValue(String name) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			return getComments();
		} else if (name == NAME || name == "name") {
			return getName();
		} else if (name == DBNAME || name == "db-name") {
			return getDbName();
		} else if (name == TYPE || name == "type") {
			return getType();
		} else if (name == PRIMARY || name == "primary") {
			return getPrimary();
		} else if (name == ENTITY || name == "entity") {
			return getEntity();
		} else if (name == MAPPINGKEY || name == "mapping-key") {
			return getMappingKey();
		} else if (name == MAPPINGTABLE || name == "mapping-table") {
			return getMappingTable();
		} else if (name == IDTYPE || name == "id-type") {
			return getIdType();
		} else if (name == IDPARAM || name == "id-param") {
			return getIdParam();
		} else if (name == CONVERTNULL || name == "convert-null") {
			return getConvertNull();
		} else if (name == DUMMY_ELM || name == "dummy_elm") {
			return getDummyElm();
		} else throw new IllegalArgumentException(name+" is not a valid property name for Column");
	}

	public Object getValue(String name, int index) {
		name = name.intern();
		if (name == COMMENTS || name == "comment") {
			return getComments(index);
		} else if (name == DUMMY_ELM || name == "dummy_elm") {
			return getDummyElm(index);
		} else if (name == NAME || name == "name") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getName();
		} else if (name == DBNAME || name == "db-name") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getDbName();
		} else if (name == TYPE || name == "type") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getType();
		} else if (name == PRIMARY || name == "primary") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getPrimary();
		} else if (name == ENTITY || name == "entity") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getEntity();
		} else if (name == MAPPINGKEY || name == "mapping-key") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getMappingKey();
		} else if (name == MAPPINGTABLE || name == "mapping-table") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getMappingTable();
		} else if (name == IDTYPE || name == "id-type") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getIdType();
		} else if (name == IDPARAM || name == "id-param") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getIdParam();
		} else if (name == CONVERTNULL || name == "convert-null") {
			if (index > 0) {
				throw new IllegalArgumentException("index > 0");
			}
			return getConvertNull();
		} else throw new IllegalArgumentException(name+" is not a valid property name for Column");
	}

	public void mergeUpdate(org.netbeans.modules.schema2beans.BaseBean sourceBean) {
		org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Column source = (org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.impl.Column) sourceBean;
		{
			java.lang.String[] srcProperty = source.getComments();
			setComments(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getName();
			setName(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getDbName();
			setDbName(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getType();
			setType(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getPrimary();
			setPrimary(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getEntity();
			setEntity(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getMappingKey();
			setMappingKey(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getMappingTable();
			setMappingTable(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getIdType();
			setIdType(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getIdParam();
			setIdParam(srcProperty);
		}
		{
			java.lang.String srcProperty = source.getConvertNull();
			setConvertNull(srcProperty);
		}
		{
			String[] srcProperty = source.getDummyElm();
			setDummyElm(srcProperty);
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
<!ELEMENT order-column (dummy_elm*)>

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
<!ELEMENT finder-column (dummy_elm*)>

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
