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

--
-- Description of ${name}
--
-- @author ${user}
--
package body ${name} is

end ${name};

