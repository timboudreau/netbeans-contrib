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
        $this->setBrowser("*firefox");
        $this->setBrowserUrl("http://localhost/");
    }

    function testMyTestCase() {
        $this->open("/");
    }
}
?>