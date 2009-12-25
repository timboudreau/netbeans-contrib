#!/usr/bin/env php
<?php

//
// Fuse Application Scaffold Creator
//
// Jim Keller
// Distributed under terms of the GPL
//

class FuseAppStructure {
	
	const FUSE_MAIN_FILE_NAME = 'FUSE.php';
	
	const MAX_PATH_LENGTH = 512;
	const MAX_URI_LENGTH = 512;
	const MAX_DB_CONFIG_STRING_LENGTH = 128;
	
	static $App_directories = 
			array( 'config' => array( 'dir_name' => 'config' ), 
				   'include' => array('dir_name' => 'include'), 
				   'models' => array( 'dir_name' => 'models' ),
				   'cache' => array( 'dir_name' => 'cache' ),
				   'controllers' => array( 'dir_name' => 'controllers' ),
				   'views' => array( 'dir_name' => 'views', 
				   					 'subdirs' => array( 
													'Layout' => array( 
														'dir_name' => 'Layout', 
														'subdirs' => array( 
															'default' => array( 
																	'dir_name' => 'default' 
															), 
														)
													),
													'Home' => array( 
														'dir_name' => 'Home'
													), 
										)
									),
				   'script' => array( 'dir_name' => 'script', 
				   					  'subdirs' => array( 'forms' => array(
				   					  						'dir_name' => 'forms' )
				   					  					)
				   					  ), 
				   'static' => array( 'dir_name' => 'static' ), 
				   'manage' => array( 'dir_name' => 'manage')
				   );
				   				
	public $common_config_file_prefix = 'common';
	public $config_file_extension = '.conf.php';			   						  		
	public $bootstrap_filename = 'bootstrap.inc.php';
	public $environment_config_filename = 'environment.conf.php';
	public $htaccess_filename = '.htaccess';
	public $index_filename = 'index.php';
	public $main_router_filename = 'routes.conf.php';
	public $controller_mixin_filename = 'ControllerMixin.class.php';
	public $base_path;
	public $base_uri;	
	public $fuse_base_path;
	public $directory_chmod = 0755;
	
	public $db_config = array();
	
	public function create_app_directories( $options = null ) {
		
		try {
			
			if ( isset($options['dir_array']) && $options['dir_array'] ) {
				$dir_array = $options['dir_array'];
			}
			else {
				$dir_array = self::$App_directories;
			}

			if ( isset($options['base_path']) && $options['base_path'] ) {
				$base_path = $options['base_path'];
			}
			else {
				$base_path = $this->base_path;
			}

			
			foreach( $dir_array as $dir_key => $dir_info ) {
				
				$dir_path = $base_path . DIRECTORY_SEPARATOR . $dir_info['dir_name'];
				
				if ( is_dir($dir_path) ) {
					echo "{$dir_path} exists. I will skip it.\n";
				} 
				else {
					if ( !mkdir($dir_path, $this->directory_chmod) ) {
						echo "Couldn't create {$dir_path}\n";
					}
					else {
						echo "Created {$dir_path}\n";
					}
				}
				
				if ( isset($dir_info['subdirs']) && is_array($dir_info['subdirs']) ) {
					
					$sub_options = $options;
					$sub_options['dir_array'] = $dir_info['subdirs'];
					$sub_options['base_path'] = $dir_path; 
					
					$this->create_app_directories($sub_options);
				}
			}
			
		}
		catch( Exception $e ) {
			throw $e;
		}
		
		
	}	
	
	public function get_manage_path() {
		
		return $this->base_path . DIRECTORY_SEPARATOR . self::$App_directories['manage']['dir_name'];
		
	}
	
	public function get_bootstrap_path() {
		
		return $this->get_config_path() . DIRECTORY_SEPARATOR . $this->bootstrap_filename;
		
	}

	public function get_config_path() {
		
		return $this->base_path . DIRECTORY_SEPARATOR . self::$App_directories['config']['dir_name'];
		
	}
	
	
	public function write_bootstrap() {
		
		$bootstrap_path = $this->get_bootstrap_path();
		$bootstrap_contents = $this->get_bootstrap_contents();
		
		return file_put_contents($bootstrap_path, $bootstrap_contents);
		
	}
	
	public function get_bootstrap_contents() {
		
		$contents = '<?php';
		$contents .= '

/**
 * 
 * FUSE bootstrap
 * You should not edit below here
 * unless you know what you\'re doing!
 * 
 * 
 */
 
if ( !defined(\'APP_BASE_PATH\') ) {
	define (\'APP_BASE_PATH\', realpath(dirname(__FILE__) . DIRECTORY_SEPARATOR . \'..\') );
}
		
if ( !defined(\'APP_CONFIG_PATH\') ) {
	define (\'APP_CONFIG_PATH\', dirname(__FILE__) );
}
		
$env_path = dirname(__FILE__) . DIRECTORY_SEPARATOR . \'environment' . $this->config_file_extension . '\';
 
if ( is_readable($env_path) ) {
	require_once( $env_path );
}

if ( defined(\'APP_ENVIRONMENT\') ) {

	$env = constant(\'APP_ENVIRONMENT\');
	$env_config_path = dirname(__FILE__) . DIRECTORY_SEPARATOR . $env;

	$common_env_file = \'common-\' . $env . \'' . $this->config_file_extension . '\';
	$common_file_path = $env_config_path . DIRECTORY_SEPARATOR . $common_env_file; 
	$common_path_msg = $common_file_path; //want the warning message to display lowercase version
	
	if ( !is_readable($common_file_path) ) {
		$common_env_file = ucfirst($common_env_file);
		$common_file_path = $env_config_path . DIRECTORY_SEPARATOR . $common_env_file; 
	}
				
	if ( is_readable ($common_file_path) ) {
		require_once( $common_file_path );
	}
	else {
		if ( is_dir($env_config_path) ) {
			trigger_error ("Could not read common configuration file for current environment - " . $common_path_msg . " does not exist", E_USER_WARNING );
		}	
	}	
}

$common_file_path = dirname(__FILE__) . DIRECTORY_SEPARATOR . \'common' . $this->config_file_extension . '\'; 

if ( !is_readable($common_file_path) ) { //check for uppercase version...possibly will be deprecated
	$common_file_path = dirname(__FILE__) . DIRECTORY_SEPARATOR . \'Common' . $this->config_file_extension . '\'; 
}			
			
if ( is_readable ($common_file_path) ) {
	require_once( $common_file_path );	
}

$app_relative_uri = \'\';		
				
if ( defined(\'APP_RELATIVE_URI\') ) {		
	$app_relative_uri = constant(\'APP_RELATIVE_URI\');
	if ( substr($app_relative_uri, 0, 1) != \'/\')	{	
			$app_relative_uri = \'/\' . $app_relative_uri;
	}
}	
					
if ( !defined(\'SITE_BASE_URI\') ) {
	define (\'SITE_BASE_URI\', determine_base_uri() . $app_relative_uri );	
}

$fuse_file_name = ( defined(\'FUSE_MAIN_FILE_NAME\') ) ? constant(\'FUSE_MAIN_FILE_NAME\') : \'FUSE.php\';

if ( defined(\'FUSE_BASE_PATH\') ) {
	$fuse_full_path = constant(\'FUSE_BASE_PATH\') . DIRECTORY_SEPARATOR . $fuse_file_name;
}
else {
	$fuse_full_path = $fuse_file_name;
}


require_once($fuse_full_path);

FUSE::Start();

function determine_base_uri() {

	if ( isset($_SERVER[\'HTTPS\']) && $_SERVER[\'HTTPS\'] && strtolower($_SERVER[\'HTTPS\']) != \'off\' ) {
		$schema = \'https\';
	}
	else {
		$schema = \'http\';
	}
	
	if ( isset($_SERVER[\'HTTP_HOST\']) ) {
		$base_uri = "{$schema}://{$_SERVER[\'HTTP_HOST\']}";
    }
	else {
        $base_uri = \'/\';
	}
		
	return $base_uri;	
}		
';

		$contents .= "\n\n" . '?>';
		
		return $contents;
	}	
	
	public function get_common_config_filepath() {
	
		$common_config_filename = $this->common_config_file_prefix . $this->config_file_extension;
		$config_path 			= $this->base_path . DIRECTORY_SEPARATOR . self::$App_directories['config']['dir_name'];
		$common_config_filepath	= $config_path . DIRECTORY_SEPARATOR . $common_config_filename;
		
		return $common_config_filepath;
	}
	
	public function get_common_config_contents() {
		
		$common_config_filepath = $this->get_common_config_filepath();

		$config_contents  = '<?php' . "\n";
		//$config_contents .= "define ('APP_BASE_PATH', '" . realpath($this->base_path) . "');\n";

		if ( $this->base_uri ) {
			
			list( , $relative_uri ) = explode('://', $this->base_uri, 2);
        	list( , $relative_uri ) = explode('/', $relative_uri, 2);
        	
			$config_contents .= "define ('SITE_BASE_URI', determine_base_uri() . '/{$relative_uri}' );\n";
		}
		
		if ( $this->fuse_base_path ) {
			$config_contents .= "define ('FUSE_BASE_PATH', '" . $this->parse_string_path($this->fuse_base_path) . "');\n";
		}
		
		if ( isset($this->db_config['default']) ) {

			$config_contents .= "define ('FUSE_DB_HOSTNAME', '" . $this->db_config['default']['hostname'] . "');\n";
			$config_contents .= "define ('FUSE_DB_USERNAME', '" . $this->db_config['default']['username'] . "');\n";
			$config_contents .= "define ('FUSE_DB_PASSWORD', '" . $this->db_config['default']['password'] . "');\n";
			$config_contents .= "define ('FUSE_DB_NAME', '" . $this->db_config['default']['db_name'] . "');\n";
		}
		else {
			$config_contents .= "//define ('FUSE_DB_HOSTNAME', '');\n";
			$config_contents .= "//define ('FUSE_DB_USERNAME', '');\n";
			$config_contents .= "//define ('FUSE_DB_PASSWORD', '');\n";
			$config_contents .= "//define ('FUSE_DB_NAME', '');\n";
			
		}

		$config_contents .= "\n\n" . '?>';
		
		return $config_contents;
		
	}
	
	public function parse_string_path( $path ) {
		
		return str_replace('\\', '\\\\', $path);
		
	}
	
	public function get_controller_mixin_contents() {
		
		return '<?php

FUSE::Require_class(\'AppControl/FuseControllerMixin\');

class ControllerMixin extends FuseControllerMixin {
	
   public function before_action() {

	
    }

    public function before_render() {

	
    }
    
}
?>';
		
	}
	
	public function get_controller_mixin_filepath() {
		
		return $this->base_path . DIRECTORY_SEPARATOR . self::$App_directories['include']['dir_name'] . DIRECTORY_SEPARATOR . $this->controller_mixin_filename;
		
	}

	
	public function get_htaccess_filepath() {
		
		return $this->base_path . DIRECTORY_SEPARATOR . $this->htaccess_filename;
		
		
	}

	public function write_htaccess_file( $options = null ) {
		
		$htaccess_filepath = $this->get_htaccess_filepath();
		
		file_put_contents( $htaccess_filepath, $this->get_htaccess_contents($options) );
		
	}
	
	public function get_htaccess_contents( $options = null ) {
		
		//http://localhost/eclipse/test
		// /eclipse/test
		
		$rewrite_base = preg_replace('/^[A-Za-z0-9]*:\/\//', '', $this->base_uri);
		
		if ( substr($rewrite_base, -1) == '/' ) {
			$rewrite_base = substr($rewrite_base, 0, -1);
		}
		
		//
		// If our base URI has no subdirectory  
		// (e.g. it looks like http://www.myfusesite.com), 
		// we don't need a rewrite base
		//
		if ( strpos($rewrite_base, '/') === false ) {
			$rewrite_base = null;
		}
		else { 
			$rewrite_base = substr($rewrite_base, strpos($rewrite_base, '/'));

		}
		
		
		$contents = "Options -Indexes SymLinksIfOwnerMatch\n";
		$contents .= "RewriteEngine on\n";
		
		if ( $rewrite_base ) { 
			$contents .= "\nRewriteBase {$rewrite_base}\n";
			//$contents .= 'RewriteCond %{REQUEST_URI} ^' . $rewrite_base . '$' . "\n";
			//$contents .= 'RewriteRule (.+) ' . $rewrite_base . '/ [R,L]' . "\n";
		}

		$contents .= 'RewriteCond %{REQUEST_FILENAME} !-f' . "\n";
		$contents .= 'RewriteCond %{REQUEST_FILENAME} !-d' . "\n";
		$contents .= 'RewriteCond %{REQUEST_URI} !^/' . $this->index_filename . "\n";
		$contents .= 'RewriteRule ^(.*)$ index.php?request_uri=$1 [QSA,L]' . "\n";
		
		return $contents;
		
	}
	
	public function get_base_path() {
		
		return $this->base_path;
		
	}


	public function get_index_contents() {
		
		$contents = '<?php' . "\n\n";
		
		$contents .= 'require_once(\''  
							. self::$App_directories['config']['dir_name'] 
							. '\' . DIRECTORY_SEPARATOR . \'' . $this->bootstrap_filename 
							. '\');' . "\n\n";
							
		$contents .= 'FUSE::require_class(\'AppControl/FuseURIRouter\' );' . "\n";

		$contents .= 'require_once(\''  
							. self::$App_directories['config']['dir_name'] 
							. '\' . DIRECTORY_SEPARATOR . \'' . $this->main_router_filename 
							. '\');' . "\n\n";

		$contents .= 'FuseURIRouter::Route();' . "\n\n";


		$contents .= '?>';
		
		return $contents;
	}
	
	
	public function get_index_filepath() {
		
		return $this->get_base_path() . DIRECTORY_SEPARATOR . $this->index_filename;
	
	}
	
	public function get_main_router_filepath() {
		
		return $this->get_config_path() . DIRECTORY_SEPARATOR . $this->main_router_filename;
		
	}


	public function get_main_router_contents() {
		
		$contents = '<?php';
		
		$contents .= "\n\n" . 'FuseURIRouter::Route_connect( \'/\', array(  
		\'action\' => \'index\',
		\'controller\' => \'Home\' 
	)
);'; 
		
		$contents .= "\n\n//\n";
		$contents .= "// Your routes go here\n";
		$contents .= "//\n\n\n";
		
		
		$contents .= '?>';
		
		return $contents;
	}
	
	public function write_main_router_file() {
		
		return file_put_contents( $this->get_main_router_filepath(), $this->get_main_router_contents() );
		
	}

	public function get_header_filepath() {
		
		return $this->get_base_path() . DIRECTORY_SEPARATOR . self::$App_directories['views']['dir_name'] . '/Layout/default/default-header.tmpl';
		
	}
	
	
	public function get_header_contents() {

		return 	'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>FUSE Project</title>
<{HTML_HEAD_ELEMENTS}>

</head>
<body>' . "\n";
		
	}

	public function write_header_file() {
		
		return file_put_contents( $this->get_header_filepath(), $this->get_header_contents() );
		
	}

	public function get_footer_filepath() {
		
		return $this->get_base_path() . DIRECTORY_SEPARATOR . self::$App_directories['views']['dir_name'] . '/Layout/default/default-footer.tmpl';
		
	}

	
	public function get_footer_contents() {
		
		return '</body></html>';
		
	}

	public function write_footer_file() {
		
		return file_put_contents( $this->get_footer_filepath(), $this->get_footer_contents() );
		
	}

	public function get_home_controller_filepath() {
		
		return $this->get_base_path() . DIRECTORY_SEPARATOR . self::$App_directories['controllers']['dir_name'] . DIRECTORY_SEPARATOR . 'HomeController.class.php';

	}

	
	public function get_home_controller_contents() {
		
		return '
<?php

FUSE::Require_class(\'AppControl/FuseApplicationController\');

class HomeController extends FuseApplicationController {
	
	function index() {
		
		$this->render();
		
	}
	
}
?>';

	}

	public function get_home_view_filepath() {
		
		return $this->get_base_path() . DIRECTORY_SEPARATOR . self::$App_directories['views']['dir_name'] . DIRECTORY_SEPARATOR . 'Home' . DIRECTORY_SEPARATOR . 'Home-Index.tmpl';

	}

	
	public function get_home_view_contents() {
		
		return '<div style="font-size: 1.4em; text-align:center;">' 
				. 'Welcome to your FUSE project.' 
				. '</div>' 
				. '<div style="margin-top: 10px; text-align:center;">' 
				. 'Your next step is to get started building Controllers, Views, and Models.<br />'
				. 'Visit the <a href="http://www.phpfuse.net/wiki/">FUSE wiki</a> for more information.<br />'
				. '<ul>'
				. '<li><a href="http://phpfuse.net/wiki/index.php?title=Controllers">Controllers</a></li>'
				. '<li><a href="http://phpfuse.net/wiki/index.php?title=Views">Views (templates)</a></li>'
				. '<li><a href="http://phpfuse.net/wiki/index.php?title=Data_Model">Models</a></li>'
				. '</ul>'
				. '</div>'
				;

	}

	public function get_environment_config_filepath() {
		
		return $this->get_base_path() . DIRECTORY_SEPARATOR . self::$App_directories['config']['dir_name'] . DIRECTORY_SEPARATOR . $this->environment_config_filename;
		
	}

	
	public function get_environment_config_contents() {
		
		return '<?php

define (\'APP_ENVIRONMENT\', \'devel\');

?>';
	
	}

}



$structure = new FuseAppStructure();

//
// Base Path
//
if ( isset($argv[1]) ) {
	$structure->base_path = $argv[1];
}

while ( !$structure->base_path ) {

	echo "\n" . 'Enter the base path for your application (where the app files will go): ';
	$structure->base_path = read_user_response(FuseAppStructure::MAX_PATH_LENGTH);

}

if ( !is_dir($structure->base_path) ) {
	echo $structure->base_path . ' doesn\'t exist. Should I create it [Y/N]? '; 
	
	if ( user_answered_yes() ) {
		if ( !mkdir($structure->base_path, $structure->directory_chmod) ) {
			echo "\n-- Error: Couldn't create directory {$structure->base_path}\n\n";
			exit;
		}
	}
	else {
		echo "\n{$structure->base_path} does not exist. Please create this directory and run the script again.\n";
		exit;
	}
}

//
// Base URI
//
while ( !$structure->base_uri ) {

	echo "\n" . 'Base URI for your application (e.g. http://localhost/myApp)? ';
	$structure->base_uri = read_user_response(FuseAppStructure::MAX_URI_LENGTH);

}

//if ( strtolower($structure->base_uri) == 'a' ) {
//	$structure->base_uri = null;
//}
//else {
	if ( substr($structure->base_uri, -1) == '/' ) {
		$structure->base_uri = substr($structure->base_uri, 0, -1);
	}
//}

//
// DB Config
//
echo "\n" . 'Would you like to configure a database connection (e.g. mySQL) [Y/N]?';

if ( user_answered_yes() ) {

	echo 'Database hostname: ';
	$structure->db_config['default']['hostname'] = read_user_response(FuseAppStructure::MAX_DB_CONFIG_STRING_LENGTH);

	echo 'Database username: ';
	$structure->db_config['default']['username'] = read_user_response(FuseAppStructure::MAX_DB_CONFIG_STRING_LENGTH);

	echo 'Database password: ';
	$structure->db_config['default']['password'] = read_user_response(FuseAppStructure::MAX_DB_CONFIG_STRING_LENGTH);

	echo 'Database name: ';
	$structure->db_config['default']['db_name'] = read_user_response(FuseAppStructure::MAX_DB_CONFIG_STRING_LENGTH);

}

//
// Fuse Path
//

//echo 'Is FUSE already in the PHP include_path (press N if unsure)? [Y/N]';

if ( !@include(FuseAppStructure::FUSE_MAIN_FILE_NAME) ) {

	$fuse_base_path_ok = false;
	
	while ( !$fuse_base_path_ok ) {
		echo "\n" . 'Where is FUSE installed? ';
		$structure->fuse_base_path = read_user_response( FuseAppStructure::MAX_PATH_LENGTH );
		
		if ( !file_exists($structure->fuse_base_path . DIRECTORY_SEPARATOR . FuseAppStructure::FUSE_MAIN_FILE_NAME) ) {
			echo 'I could not find FUSE in this directory. Please try again.' . "\n";
		} 
		else {
			$fuse_base_path_ok = true;
		}
	}
}


echo "\n" . 'FUSE project will be created in ' . $structure->base_path . '. Continue [Y/N]?';

if ( user_answered_yes()  ) {
	
	$structure->create_app_directories();
	$structure->write_bootstrap();
	
	write_file_check_overwrite('common_config');
	write_file_check_overwrite('controller_mixin');
	write_file_check_overwrite('htaccess');
	write_file_check_overwrite('main_router');
	write_file_check_overwrite('header');
	write_file_check_overwrite('footer');
	write_file_check_overwrite('home_controller');
	write_file_check_overwrite('home_view');
	write_file_check_overwrite('environment_config');
	write_file_check_overwrite('index');
	
	require_once( $structure->get_bootstrap_path() );

	FUSE::Require_class('File/FileOperation');
	
	$manage_script_src = constant('FUSE_BASE_PATH')
						. DIRECTORY_SEPARATOR 
						. 'scripts'
						. DIRECTORY_SEPARATOR
						. 'manage';
	
	$manage_script_dst = $structure->get_manage_path();
	
	FileOperation::Copy_path( $manage_script_src, $manage_script_dst, array('overwrite' => true) );		
	
	echo "\n ** FUSE Project successfuly created ** \n";
	
	if ( $structure->base_uri ) {
		echo "\n *** Get started by going to {$structure->base_uri} ***\n\n";	
	}
	else {
		echo "\n *** Get started by opening your project URL in a browser ***\n\n";
	}
	
}

function write_file_check_overwrite( $file_key ) {

	global $structure;

	$wrote = false;
	$write_method = "write_{$file_key}_file";
	$contents_method = "get_{$file_key}_contents";
	$path_method = "get_{$file_key}_filepath";
	$filepath    = $structure->$path_method();
	
	if ( file_exists($filepath) ) {
		
		echo "{$filepath} exists. Overwrite [Y/N]?";

		if ( user_answered_yes() ) {
			file_put_contents( $filepath, $structure->$contents_method() );
			$wrote = true;
		}
	}
	else {
		file_put_contents( $filepath, $structure->$contents_method() );
		$wrote = true;
	}
	
	if ( $wrote ) {
		echo "Wrote: {$filepath}\n";
	}
	
}

function user_answered_yes() {
	
	fseek(STDIN, 0);
	$answer = trim(fread(STDIN, 1));
	
	if ( strtolower(substr($answer,0,1)) == 'y' ) {
		return true;
	}
	
	return 0;
}

function read_user_response( $length = null ) {
	
	fseek(STDIN, 0);
	$response = trim(fread(STDIN, $length));
	
	return $response;
} 
?>
