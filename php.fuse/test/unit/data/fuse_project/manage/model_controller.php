<?php

require_once( dirname(__FILE__) . DIRECTORY_SEPARATOR . 'load_bootstrap.inc.php' );

FUSE::Require_class('AppManage/ModelGenerator');
FUSE::Require_class('AppManage/ControllerGenerator');

if ( !isset($argc) || $argc == 0 ) {
	echo "Usage: php " . basename(__FILE__) . ' table|ALL_TABLES [controller_class]';
	exit; 
}

try { 
	$modelg = new ModelGenerator;
	$cg = new ControllerGenerator;

	$options['from_console'] = true;

	if ( $argv[1] == 'ALL_TABLES' ) {
			$db = FUSE::global_db_object();
			$tables = $db->list_tables();
	}
	else {
		$tables = array( $argv[1] );
	}
	
	if ( is_array($tables) ) {
		foreach( $tables as $cur_table ) {
			$path = $modelg->generate_for_table( $cur_table, $options );
			if ( $path ) echo "Model Generated: {$path}\n";
		}
	}

	if ( isset($argv[2]) ) {
		$options['parent_class'] = $argv[2];
	}
	else {
		$options['parent_class'] = 'AppControl/FuseDataController';
	}
	
	foreach( $tables as $cur_table ) {
		$path = $cg->generate_by_table_name( $cur_table, $options );
		if ( $path ) echo "Controller Generated: {$path}\n";
	}
}
catch( Exception $e ) {
	echo "\nError: " . $e->getMessage() . "\n\n";
}

?>