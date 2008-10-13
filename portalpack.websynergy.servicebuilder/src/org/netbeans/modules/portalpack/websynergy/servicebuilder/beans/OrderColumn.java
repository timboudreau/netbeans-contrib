/**
 * This interface has all of the bean info accessor methods.
 * 
 * @Generated
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans;

public interface OrderColumn {
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

	public void setCaseSensitive(java.lang.String value);

	public java.lang.String getCaseSensitive();

	public void setOrderBy(java.lang.String value);

	public java.lang.String getOrderBy();

	public void setDummyElm(String[] value);

	public void setDummyElm(int index, String value);

	public String[] getDummyElm();

	public java.util.List fetchDummyElmList();

	public String getDummyElm(int index);

	public int sizeDummyElm();

	public int addDummyElm(String value);

	public int removeDummyElm(String value);

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener);

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener);

	public Object clone();

	public Object cloneData();

}
