<?xml version='1.0' encoding='ISO-8859-1' ?>
<!--
*     Copyright 2002 Sun Microsystems, Inc. All rights reserved.
*     Use is subject to license terms.
-->
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">
<helpset version="1.0">


  <!-- title -->
  <title>JemmySupport Module Help</title>
  
  <!-- maps -->
  <maps>
     <homeID>org.netbeans.modules.jemmysupport.docs</homeID><!-- .HOMEID</homeID> -->
     <mapref location="jemmysupportMap.jhm" />
  </maps>
  
  <!-- views -->
  <view>
    <name>TOC</name>
    <label>Table Of Contents</label>
    <type>javax.help.TOCView</type>
    <data>jemmysupport-toc.xml</data> 
 </view>
 
  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>jemmysupport-idx.xml</data>
  </view>

  <view>
    <name>Search</name>

   <label>Search</label>

   <type>javax.help.SearchView</type>
 
  <data engine="com.sun.java.help.search.DefaultSearchEngine">
      JavaHelpSearch2

   </data>
  </view>


</helpset>
