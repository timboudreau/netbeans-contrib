<?php

ini_set('display_errors', 'On');

require_once( dirname(__FILE__) . DIRECTORY_SEPARATOR . 'load_bootstrap.inc.php' );

FUSE::Require_class('AppManage/ViewGenerator');


if ( !isset($argc) || $argc < 2 ) {
	echo "Usage: " . basename(__FILE__) . ' controller action [table]';
	exit; 
}

try { 

	
	$vg = new ViewGenerator;

	$written_path = null;
	$controller_name = $argv[1];
	$action_name = $argv[2];
	
	$table_name = isset( $argv[3] ) ? $argv[3] : null;

	$options['from_console'] = true;
	$options['table_name'] = $table_name;

	$written_path = $vg->generate_for_controller_action( $controller_name, $action_name, $options );

	if ( $written_path ) echo "View Generated: {$written_path}\n";
	
	
}
catch( Exception $e ) {
	echo "\nError: " . $e->getMessage() . "\n\n";
	FUSE::Set_message_newline("\n");
	FUSE::Set_debug_level( ERROR_LEVEL_INTERNAL );
	echo FUSE::Get_error_messages();
}

?>
