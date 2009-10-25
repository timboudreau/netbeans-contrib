<?php

require_once( dirname(__FILE__) . DIRECTORY_SEPARATOR . 'load_bootstrap.inc.php' );

FUSE::Require_class('AppManage/ModelGenerator');
FUSE::Require_class('AppManage/ControllerGenerator');
FUSE::Require_class('AppManage/ViewGenerator');
FUSE::Require_class('AppManage/RouteGenerator');

if ( !isset($argc) || $argc < 2 ) {
	echo "Usage: php " . basename(__FILE__) . ' table|ALL_TABLES [controller_class]';
	exit; 
}

try { 
	$modelg = new ModelGenerator;
	$cg = new ControllerGenerator;
	$vg = new ViewGenerator;
	$rg = new RouteGenerator;

	ini_set('display_errors', 'On');
	error_reporting(E_ALL);


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

		$options['table_name'] = $cur_table;

		foreach ( array('List', 'Edit', 'View', 'Delete', 'Add') as $action ) {
			
			$controller_name = $cg->controller_name_by_table_name($cur_table);
			
			if ( $action != 'Delete' && $action != 'Add' ) {
				$view_path = $vg->generate_for_controller_action( $controller_name, $action, $options );
				if ( $view_path ) echo "View Generated: {$view_path}\n";
			}
		
			$route_uri = strtolower("{$controller_name}/{$action}");
			$route_setup = array();
			$route_setup['controller'] = $controller_name;
			$route_setup['action'] = strtolower($action);
			
			if ( $action == 'List' ) {
				$route_setup['method'] = 'show_list';
			}
			else if ( $action != 'Add') {
				$route_setup['requirements'] = array( 'id' => '/\d+/');
				$route_uri .= '/:id';
			}
		
			$rg->add_route( $route_uri, $route_setup, array('ignore_duplicates' => true));
		
		}
	}

	
}
catch( Exception $e ) {
	echo "\nError: " . $e->getMessage() . "\n\n";
	FUSE::Set_message_newline("\n");
	FUSE::Set_debug_level( ERROR_LEVEL_INTERNAL );
	echo FUSE::Get_error_messages();
}

?>