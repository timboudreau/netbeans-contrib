/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.api.mdr;

import javax.jmi.reflect.*;
import org.netbeans.api.mdr.events.MDRChangeSource;

/** Interface for accessing content of a metadata repository.
 *
 * @author Martin Matula
 * @version 0.4
 */
public interface MDRepository extends MDRChangeSource {
    /** Instantiates MOF Model package.
     * @param substName name (unique within the repository) for the new MOF package extent
     * @throws CreationFailedException instantiation of the package failed (e.g. if an extent with a given name already exists)
     * @return reference to the created package extent
     */
    public RefPackage createExtent(String substName) throws CreationFailedException;
    
    /** Instantiates given package.
     * If the package that has to be instantiated clusters some other packages, these clustered packages will be instantiated
     * automatically but they will be not associated with any names so they will not be accessible directly. If the instantiated package will be deleted,
     * the automatically created extents will also be deleted (if there are no other references to them).
     * @param substName name (unique within the repository) for the new package extent
     * @param metaPackage reference to the package that should be instantiated
     * @throws CreationFailedException instantiation of the package failed (e.g. if an extent with a given name already exists)
     * @return reference to the created extent
     */
    public RefPackage createExtent(String substName, RefObject metaPackage) throws CreationFailedException;
    
    /** Instantiates given package.
     * If the package that is to be instantiated clusters some other packages, repository will use a corresponding existing package extent passed
     * in existingExtents parameter for clustering. If an instance of some particular package cannot be found in existingExtents paramter, it
     * will be automatically created (this instance will not have any name and thus it won't be accessible directly;
     * once none of existing extents will cluster it, it should be deleted automatically).
     * @param substName name (unique within the repository) for the new package extent
     * @param metaPackage reference to the package that should be instantiated
     * @param existingInstances existing package extents that will be used instead of creating a new extent if a package is clustered
     * @throws CreationFailedException instantiation of the package failed (e.g. if an extent with a given name already exists)
     * @return reference to the created extent
     */
    public RefPackage createExtent(String substName, RefObject metaPackage, RefPackage existingInstances[]) throws CreationFailedException;

    /** Returns reference to a package extent of a given name.
     * @param name name of the package extent to be returned
     * @return reference to the package extent (returns null if extent of a given name was not found)
     */
    public RefPackage getExtent(String name);
    
    /** Returns names for all named package extents in the repository.
     * @return Array of extent names.
     */
    public String[] getExtentNames();

    /**
     * Returns the object with the given MOF ID.
     *
     * @param mofId the MOF ID of the object to be returned
     * @return the object with the given MOF ID or <code>null</code> if no such object is found
     * 
     * 
     */
    public RefBaseObject getByMofId(String mofId);
    
    /** Starts a new transaction. This method causes that the repository will be locked
     * for read-only access or exclusive write access (depending on the value passed as
     * a parameter) and starts a new transaction. It is prefered to enclose any batch
     * operations on MDR by <code>beginTrans</code> and <code>endTrans</code> calls
     * to avoid autocommiting (which may be slow) after each JMI operation.<p>
     * Transactions can be nested however real nested transaction commit/rollback
     * does not need to be implemented - implementation can commit/rollback whole transaction
     * after the outermost <code>endTrans</code> call depending on whether any nested
     * transaction failed.<p>
     * During each transaction it is guaranteed that no other thread can
     * modify the metadata.<p>
     * <i>Important:</i>This call locks the repository and the lock is held till the
     * transaction is ended by a call to <code>endTrans</code>. To make sure there is
     * a corresponding <code>endTrans</code> call to each <code>beginTrans</code> call
     * use the <code>try-finally</code> construct:<p>
     * <code>
     * try {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;beginTrans(false);<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;// set of JMI calls<br>
     * } finally {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;endTrans();<br>
     * }
     * </code>
     * @param writeAccess <code>true</code> indicates that the transaction will be
     *  modifying the repository. <code>false</code> means the transaction is
     *  read-only (it will not be allowed to modify any data). Transactions with
     *  write access cannot be nested into read-only transactions.
     */
    public void beginTrans(boolean writeAccess);
    
    /** Ends transaction started by <code>beginTrans</code> call. If the transaction
     * modified some data, this call will attemp to commit it.<p>
     * This method has the same effect as calling <code>endTrans(false)</code>.
     */
    public void endTrans();
    
    /** Ends transaction started by <code>beginTrans</code> call.
     * Result of this call depends on whether it is nested in another
     * <code>beginTrans</code> - <code>endTrans</code> pair or not.
     * If this call is nested and value of <code>rollback</code> parameter is
     * <code>false</code>, it will not affect the result of the <code>endTrans</code>
     * call corresponding to the outermost call to <code>beginTrans</code>. However
     * if <code>true</code> is passed, the whole transaction will be rolled back by
     * the last (i.e. outermost) <code>endTrans</code> call (no matter what will be the
     * value of <code>rollback</code> parameter for this last call).
     * If this call is not nested (i.e. it is outermost) the whole transaction is
     * commited if <code>true</code> was passed to this call and all the nested
     * <code>endTrans</code> calls. Otherwise (if <code>false</code> was passed to this
     * call or any nested <code>endTrans</code> call) the transaction is rolled back.
     */
    public void endTrans(boolean rollback);
    
    /** Shuts down the repository. This method should be called from {@link MDRManager#shutdownAll} method.
     * Implementation of this method should do all the necessary clean-up actions,
     * such as flushing storage caches, etc.
     */
    public void shutdown();
}
