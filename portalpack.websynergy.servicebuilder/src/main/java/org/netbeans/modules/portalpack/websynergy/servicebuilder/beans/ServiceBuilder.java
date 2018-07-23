/**
 * This interface has all of the bean info accessor methods.
 * 
 * @Generated
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.beans;

public interface ServiceBuilder {
	public void setComments(java.lang.String[] value);

	public void setComments(int index, java.lang.String value);

	public java.lang.String[] getComments();

	public java.util.List fetchCommentsList();

	public java.lang.String getComments(int index);

	public int sizeComments();

	public int addComments(java.lang.String value);

	public int removeComments(java.lang.String value);

	public void setPackagePath(java.lang.String value);

	public java.lang.String getPackagePath();

	public void setAuthor(String value);

	public String getAuthor();

	public void setNamespace(String value);

	public String getNamespace();

	public void setEntity(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity[] value);

	public void setEntity(int index, org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity value);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity[] getEntity();

	public java.util.List fetchEntityList();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity getEntity(int index);

	public int sizeEntity();

	public int addEntity(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity value);

	public int removeEntity(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity value);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity newEntity();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity newEntity(Entity source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData);

	public void setExceptions(org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions value);

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions getExceptions();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions newExceptions();

	public org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Exceptions newExceptions(Exceptions source, org.netbeans.modules.schema2beans.BaseBean parent, boolean justData);

	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener);

	public void removePropertyChangeListener(java.beans.PropertyChangeListener listener);

	public Object clone();

	public Object cloneData();

}
