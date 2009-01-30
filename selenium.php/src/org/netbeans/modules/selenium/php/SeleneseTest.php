<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">


require_once 'PHPUnit/Extensions/SeleniumTestCase.php';

/**
 * Description of ${name}
 *
 * @author ${user}
 */
class ${name} extends PHPUnit_Extensions_SeleniumTestCase {
    
    function setUp() {
        $this->setBrowser("*chrome");
        $this->setBrowserUrl("http://change-this-to-the-site-you-are-testing/");
    }

    function testMyTestCase() {
        
    }
}
?>