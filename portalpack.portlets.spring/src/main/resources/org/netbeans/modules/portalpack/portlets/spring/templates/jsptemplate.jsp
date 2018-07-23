<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page import="javax.portlet.*"%>
<#if VERSION == "2.0">
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<#else>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
</#if>

<portlet:defineObjects />
<%PortletPreferences prefs = renderRequest.getPreferences();%> 

<#if MODE == "VIEW">
View Mode Page
</#if>
<#if MODE == "EDIT">
Edit Mode Page
</#if>
<#if MODE == "HELP">
Help Mode Page
</#if>