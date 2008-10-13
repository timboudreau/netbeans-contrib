/**
 * This interface has all of the bean info accessor methods.
 * 
 * @Generated
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans;

public interface Order {
	public void setComments(java.lang.String[] value);

	public void setComments(int index, java.lang.String value);

	public java.lang.String[] getComments();

	public java.util.List fetchCommentsList();

	public java.lang.String getComments(int index);

	public int sizeComments();

	public int addComments(java.lang.String value);

	public int removeComments(java.lang.String value);

	public void setBy(java.lang.String value);

	public java.lang.String getBy();

	public void setOrderColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.OrderColumn[] valueInterface);

	public void setOrderColumn(int index, org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.OrderColumn valueInterface);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.OrderColumn[] getOrderColumn();

	public java.util.List fetchOrderColumnList();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.OrderColumn getOrderColumn(int index);

	public int sizeOrderColumn();

	public int addOrderColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.OrderColumn valueInterface);

	public int removeOrderColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.OrderColumn valueInterface);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.OrderColumn newOrderColumn();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.OrderColumn newOrderColumn(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.OrderColumn source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData);

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener);

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener);

	public Object clone();

	public Object cloneData();

}
