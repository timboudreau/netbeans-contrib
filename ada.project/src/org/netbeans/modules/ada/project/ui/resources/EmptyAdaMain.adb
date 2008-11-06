<#-- This is a FreeMarker template -->
<#-- See http://wiki.netbeans.org/FaqFreeMarker -->
<#assign licenseFirst  = "-- ">
<#assign licensePrefix = "-- ">
<#assign licenseLast   = "-- ">
<#include "../Licenses/license-${project.license}.txt">

--------------------------------------------------------------------------------
-- File: ${nameAndExt}
--
-- Created on ${date}
--------------------------------------------------------------------------------

with Ada.Text_IO;

--
-- Description of ${name}
--
-- @author ${user}
--
procedure ${name} is
begin

	Ada.Text_IO.Put_Line ("Hello, world!");

end ${name};