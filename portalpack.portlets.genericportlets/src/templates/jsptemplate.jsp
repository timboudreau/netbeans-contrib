<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<#if VERSION == "2.0">
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<#else>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
</#if>

<portlet:defineObjects />
<%PortletPreferences prefs = renderRequest.getPreferences();%> 
--%>

<b>
    ${DESC}
</b>