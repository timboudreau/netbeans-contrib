/*
 * ${name}.fx
 *
 * Created on ${date}, ${time}
 */

<#if package?? && package != "">
package ${package};
</#if>

import javafx.gui.*;

/**
 * @author ${user}
 */

Application{
    content: Label{ text: "Application content"}
} 