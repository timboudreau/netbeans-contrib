/*
 * FDataLoaderBeanInfo.java
 *
 * Created on July 9, 2007, 4:41 PM
 *
 */
package org.netbeans.modules.fortran;
 
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.SimpleBeanInfo;
import org.openide.loaders.UniFileLoader;
import org.openide.util.Utilities;

/**
 * A support class
 */
public class FDataLoaderBeanInfo extends SimpleBeanInfo {
    
    /**
     * additional info
     */
    public BeanInfo[] getAdditionalBeanInfo() {
        try {
            return new BeanInfo[] {Introspector.getBeanInfo(UniFileLoader.class)};
        } catch (IntrospectionException e) {
            throw new AssertionError(e);
        }
    }
    
    /**
     * icon
     */
    public Image getIcon(int type) {
        return super.getIcon(type); // TODO add a custom icon here
        
    }
    
}
