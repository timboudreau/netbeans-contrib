<?php

require_once( dirname(__FILE__) . DIRECTORY_SEPARATOR . 'load_bootstrap.inc.php' );

FUSE::Require_class('AppManage/ControllerGenerator');

if ( !isset($argc) || $argc == 0 ) {
	echo "Usage: " . basename(__FILE__) . ' controller_name [parent_class]';
	exit; 
}

try { 
	$cg = new ControllerGenerator;

	$options['from_console'] = true;

	if ( isset($argv[2]) ) {
		$options['parent_class'] = $argv[2];
	}

	$path = $cg->generate_by_controller_name( $argv[1], $options );
	if ( $path ) echo "Controller Generated: {$path}\n";
}
catch( Exception $e ) {
	echo "\nError: " . $e->getMessage() . "\n\n";
}

?>