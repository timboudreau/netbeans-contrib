/**
 * This interface has all of the bean info accessor methods.
 * 
 * @Generated
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans;

public interface Finder {
        
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

	public void setReturnType(java.lang.String value);

	public java.lang.String getReturnType();

	public void setWhere(java.lang.String value);

	public java.lang.String getWhere();

	public void setDbIndex(java.lang.String value);

	public java.lang.String getDbIndex();

	public void setFinderColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn[] valueInterface);

	public void setFinderColumn(int index, org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn valueInterface);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn[] getFinderColumn();

	public java.util.List fetchFinderColumnList();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn getFinderColumn(int index);

	public int sizeFinderColumn();

	public int addFinderColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn valueInterface);

	public int removeFinderColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn valueInterface);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn newFinderColumn();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn newFinderColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData);

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener);

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener);

	public Object clone();

	public Object cloneData();

}
