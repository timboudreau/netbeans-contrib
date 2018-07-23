/**
 * This interface has all of the bean info accessor methods.
 * 
 * @Generated
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans;


public interface Entity {
	public void setComments(java.lang.String[] value);

	public void setComments(int index, java.lang.String value);

	public java.lang.String[] getComments();

	public java.util.List fetchCommentsList();

	public java.lang.String getComments(int index);

	public int sizeComments();

	public int addComments(java.lang.String value);

	public int removeComments(java.lang.String value);

	public void setName(java.lang.String value);

	public java.lang.String getName();

	public void setTable(java.lang.String value);

	public java.lang.String getTable();

	public void setUuid(java.lang.String value);

	public java.lang.String getUuid();

	public void setLocalService(java.lang.String value);

	public java.lang.String getLocalService();

	public void setRemoteService(java.lang.String value);

	public java.lang.String getRemoteService();

	public void setPersistenceClass(java.lang.String value);

	public java.lang.String getPersistenceClass();

	public void setDataSource(java.lang.String value);

	public java.lang.String getDataSource();

	public void setSessionFactory(java.lang.String value);

	public java.lang.String getSessionFactory();

	public void setTxManager(java.lang.String value);

	public java.lang.String getTxManager();

	public void setCacheEnabled(java.lang.String value);

	public java.lang.String getCacheEnabled();

	public void setColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column[] value);

	public void setColumn(int index, org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column value);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column[] getColumn();

	public java.util.List fetchColumnList();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column getColumn(int index);

	public int sizeColumn();

	public int addColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column value);

	public int removeColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column value);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column newColumn();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column newColumn(Column source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData);

	public void setOrder(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order value);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order getOrder();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order newOrder();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Order newOrder(Order source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData);

	public void setFinder(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder[] value);

	public void setFinder(int index, org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder value);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder[] getFinder();

	public java.util.List fetchFinderList();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder getFinder(int index);

	public int sizeFinder();

	public int addFinder(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder value);

	public int removeFinder(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder value);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder newFinder();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder newFinder(Finder source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData);

	public void setReference(String[] value);

	public void setReference(int index, String value);

	public String[] getReference();

	public java.util.List fetchReferenceList();

	public String getReference(int index);

	public int sizeReference();

	public int addReference(String value);

	public int removeReference(String value);

	public void setReferencePackagePath(java.lang.String[] value);

	public void setReferencePackagePath(int index, java.lang.String value);

	public java.lang.String[] getReferencePackagePath();

	public java.util.List fetchReferencePackagePathList();

	public java.lang.String getReferencePackagePath(int index);

	public int sizeReferencePackagePath();

	public int addReferencePackagePath(java.lang.String value);

	public int removeReferencePackagePath(java.lang.String value);

	public void setReferenceEntity(java.lang.String[] value);

	public void setReferenceEntity(int index, java.lang.String value);

	public java.lang.String[] getReferenceEntity();

	public java.util.List fetchReferenceEntityList();

	public java.lang.String getReferenceEntity(int index);

	public int sizeReferenceEntity();

	public int addReferenceEntity(java.lang.String value);

	public int removeReferenceEntity(java.lang.String value);

	public void setTxRequired(String[] value);

	public void setTxRequired(int index, String value);

	public String[] getTxRequired();

	public java.util.List fetchTxRequiredList();

	public String getTxRequired(int index);

	public int sizeTxRequired();

	public int addTxRequired(String value);

	public int removeTxRequired(String value);

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener);

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener);

	public Object clone();

	public Object cloneData();

}
