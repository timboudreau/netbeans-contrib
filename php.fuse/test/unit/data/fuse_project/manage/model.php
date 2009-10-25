<?php

require_once( dirname(__FILE__) . DIRECTORY_SEPARATOR . 'load_bootstrap.inc.php' );

FUSE::Require_class('AppManage/ModelGenerator');

if ( !isset($argc) || $argc == 0 ) {
	echo "Usage: " . basename(__FILE__) . ' table|ALL_TABLES';
	exit; 
}

try { 

	$modelg = new ModelGenerator;

	$options['from_console'] = true;

	if ( $argv[1] == 'ALL_TABLES' ) {
		$db = FUSE::global_db_object();
		$tables = $db->list_tables();
		
	}
	else {
		$tables = array($argv[1]);
	}
	
	foreach( $tables as $cur_table ) {
		$path = $modelg->generate_for_table( $cur_table, $options );
		if ( $path ) echo "Model Generated: {$path}\n";
	}
}
catch( Exception $e ) {
	echo "\nError: " . $e->getMessage() . "\n\n";
}

?>
