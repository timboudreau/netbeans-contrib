<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page import="javax.portlet.*"%>
<#if VERSION == "2.0">
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<#else>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
</#if>
<portlet:defineObjects/>
<#if MODE == "VIEW">
<h1>Form Submission Portlet</h1>

<#assign enctype = "">
<#foreach ip in input_params>
    <#if ip.getDataType().getType() == "file">
        <#assign enctype = "multipart/form-data">
    </#if>
</#foreach>
<table>
<#if enctype == "">
<form method="post" action="<portlet:actionURL/>">
<#else>    
<form method="post" action="<portlet:actionURL/>" enctype="multipart/form-data">
</#if>

<#foreach ip in input_params>
<#assign values = ip.getValues()>
<tr>  
 <#if ip.getComponentType() == "radio" || ip.getComponentType() == "checkbox">
 <tr/>
 <tr>
    <td>${ip.getLabel()}</td>
 </tr>   
    <#foreach d in values>
    <tr>
        <td/>
        <td>${d}</td>
        <td>
            <input type="${ip.getComponentType()}" name="${ip.getName()}" value="${d}"/>
        </td>
    </tr>
    </#foreach>
  <tr/>  
 <#elseif ip.getComponentType() == "select">
    <td>${ip.getLabel()}</td>
    <td>
        <select name="${ip.getName()}">
    <#foreach d in values>
            <option value="${d}">${d}</option>
    </#foreach>
        </select>
    </td>
 <#else>
    <td>${ip.getLabel()}</td>   
    <td>
        <#if ip.getDataType().getType() == "file">
        <input type="file" name="${ip.getName()}"/>
        <#else>
        <input type="${ip.getComponentType()}" name="${ip.getName()}"/>
        </#if>
    </td>
    </#if>
</tr>
</#foreach>    

<tr><td><button type="submit">Submit</button></td></tr>
</form>

</table>
</#if>

<#if MODE == "EDIT">
<b> EDIT Page </b>
</#if>

<#if MODE == "HELP">
<b> HELP Page </b>
</#if>