<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page import="javax.portlet.*"%>
<#if VERSION == "2.0">
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<#else>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
</#if>
<table>

<portlet:defineObjects/>
<#foreach ip in input_params>
<#if ip.getComponentType() == "checkbox">
<tr>
    <td>${ip.getLabel()}:</td>
    <td>${r"${command."}${ip.getName()}[0]}</td>
</tr>
<#else>
<tr>
    <td>${ip.getLabel()}:</td>
    <td>${r"${command."}${ip.getName()}}</td>
</tr>
</#if>
</#foreach>
</table>