#!/bin/sh

#set -v

trap "on_exit; exit" 1 2 15 EXIT

PID=$$
TMP_DIR=/tmp/ssinstaller.${PID}
mkdir -p ${TMP_DIR}

CWD=`pwd`
PATH=/usr/bin:/usr/sbin:/bin:/opt/sun/servicetag/bin
INSTALLER_DIR=`dirname $0`
INSTALLER_NAME=`basename $0`

SOLARIS_VERSION=12.0


umask 022

on_exit() {
   cd /
   if [ -d "$TMP_DIR" ]; then
      rm -fr $TMP_DIR;
      
   fi      
}

error() {
    MESSAGE=$1
    echo $MESSAGE
    exit 1
}

message() {
    MESSAGE=$1
    echo $MESSAGE
}

init() {
    un=`uname` 2> /dev/null    
    if [ "$un" = "SunOS" ]; then
	SYMLINK_PACKAGE=SPROsslnk
        pl=`uname -i` 2>/dev/null
        if [ "$pl" = "i86pc" ]; then
            arch=intel-S2
        else
            arch=sparc-S2
        fi
    elif [ "$un" = "Linux" ]; then
        arch=intel-Linux
	error "Linux does not work now... Exiting..."
    else
        error "Neither SunOS nor Linux... Exiting..."
    fi

    #if [ "$UID" != "0" ]
    #then
    #	error "You should be root to install Sun Studio."
    #fi 

    PACKAGES_DIR=$INSTALLER_DIR/packages
    if [ "$arch" = "intel-Linux" ]; then
        install_dir="/opt/sun"
    else
        install_dir="/opt"
    fi
    INSTALLATION_DIR="${ALTERNATIVE_ROOT}${BASE_DIR-$install_dir}"
    UNINSTALLER_SCRIPT=$INSTALLATION_DIR/uninstaller.sh
}

print_params() {
    echo "========================================================"
    echo "INSTALLATION_DIR=${INSTALLATION_DIR}"
    echo "========================================================"
}

print_usage() {
echo "Syntax is $INSTALLER_NAME [options]"
echo "Options: "
echo "  --accept-sla" 
echo "    Accept license. This option is required to install Sun Studio."
echo "  --print-sla"
echo "    Print license."
echo "  --installation-dir <directory>" 
echo "    Install Sun Studio into this directory."
echo "  --alternative-root <directory>"
echo "    Use given directory as alternative root."
echo "  --local-zone"
echo "    Install into local zone only."
echo "  --create-symlinks"
echo "    Create symlinks in /usr/bin."
echo "  --install-patches"
echo "    Install additional patches."
echo "End."
}

print_license() {
echo "License is here"
}

parse_args() {
while [ "$#" -gt "0" ]
do
    case $1 in
	--accept-sla)
	    ACCEPT_LICENSE=1
	    ;;
	--print-sla)
	    print_license;
	    exit 0;
	    ;;
	--installation-dir)
	    shift
	    BASE_DIR="$1"
	    ;;
	--alternative-root)
	    shift
	    ALTERNATIVE_ROOT="$1"
	    ALTERNATIVE_ROOT_ARG="-R $1"
	    ;;
	--local-zone)
	    LOCAL_ZONE_ONLY=-G
	    ;;
	--create-symlinks)
	    CREATE_SYMLINKS=1
	    ;;	    
	--install-patches)
	    INSTALL_PATCHES=1
	    ;;	
	--help)
	
	    print_usage;
	    exit 0;
	    ;;
	*)
    	    message "Unkonw option: $1"
	    print_usage;
	    exit 1
	;;
    esac
    shift
done
if [ "$ACCEPT_LICENSE" -ne 1 ]
then 
    print_usage;
    error "Error. You should accept license to install Sun Studio."
fi
}

load_package_list() {
    PACKAGE_LIST="SPROcc";
    if [ "$CREATE_SYMLINKS" -eq 1 ]
    then
	PACKAGE_LIST="$PACKAGE_LIST $SYMLINK_PACKAGE"
    fi
}

check_existing_sunstudio() {
    for package in $PACKAGE_LIST 
    do
	if [ "`pkginfo $ALTERNATIVE_ROOT_ARG $package 2>/dev/null`" = "" ]
	then 
	    continue
	fi
		
	version=`pkginfo -l $ALTERNATIVE_ROOT_ARG  $package | grep VERSION | sed s/' '*VERSION:' '*// | cut -f1 -d','`
	if [ "$version" = "$SOLARIS_VERSION" ]
	then
	    message "The package $package with version $version is already installed."
	fi
	
    done
    if [ "$version" = "$SOLARIS_VERSION" ]
    then
	error "The Sun Studio pacakges are found in the system. The installation could not be completed while they are not removed."
    fi	
}


create_adminfile()
{
        cat << EENNDD > $TMP_DIR/adminfile
mail=
instance=unique
partial=nocheck
runlevel=nocheck
idepend=nocheck
rdepend=nocheck
space=quit
setuid=nocheck
conflict=nocheck
action=nocheck
basedir=${BASE_DIR-default}
EENNDD
}


install_packages() {
    create_adminfile;
    for package in $PACKAGE_LIST
    do
	pkgadd -n $LOCAL_ZONE_ONLY $ALTERNATIVE_ROOT_ARG -d $PACKAGES_DIR -a $TMP_DIR/adminfile $package
    done
}


unpack() {
    echo "TODO .."
}

create_uninstaller() {
    echo \#!/bin/sh > $UNINSTALLER_SCRIPT
    echo "echo Uninstalling Sun Studio." >> $UNINSTALLER_SCRIPT
    for package in $PACKAGE_LIST 
    do
	echo "yes | pkgrm $ALTERNATIVE_ROOT_ARG  $package" >> $UNINSTALLER_SCRIPT
    done
    echo "rm $UNINSTALLER_SCRIPT" >> $UNINSTALLER_SCRIPT
    echo "echo Finished." >> $UNINSTALLER_SCRIPT
    chmod 744 $UNINSTALLER_SCRIPT
    echo "Uninstaller script was written $UNINSTALLER_SCRIPT"
}


parse_args $*;
init;
print_params;
unpack;
load_package_list;
check_existing_sunstudio;
install_packages;
create_uninstaller;
