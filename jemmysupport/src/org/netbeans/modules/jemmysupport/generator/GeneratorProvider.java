/*
 * GeneratorProvider.java
 *
 * Created on November 4, 2002, 2:46 PM
 */

package org.netbeans.modules.jemmysupport.generator;

import java.util.Properties;

/** Generator Provider interface
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 1.0
 */
public interface GeneratorProvider {
    public ComponentGenerator getInstance(Properties props);
}
