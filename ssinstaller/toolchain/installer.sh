#!/bin/sh

#set -v
#
#  The names begin with __ are reserved for substitution
#



trap "on_exit; exit" 1 2 15 EXIT


PID=$$
TMP_DIR=/tmp/ssinstaller.${PID}
export TMP_DIR
mkdir -p ${TMP_DIR}
CWD=`pwd`
PATH=/usr/bin:/usr/sbin:/bin:/opt/sun/servicetag/bin
INSTALLER_NAME=`basename $0`

SOLARIS_VERSION=12.0

SUNSTUDIO_DIR_NAME=`uname | sed s/SunOS/SUNWspro/ | sed s/Linux/sunstudioceres/`

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

if [ "`id | cut -f2 -d'='| cut -f1 -d'('`" -ne 0 ]
then
    error "You should be root to install Sun Studio."
fi 


init() {
    un=`uname` 2> /dev/null    
    if [ "$un" = "SunOS" ]; then
	SYMLINK_PACKAGE=SPROsslnk
        ALTERNATIVE_ROOT_CMD="-R"
        pl=`uname -i` 2>/dev/null
        if [ "$pl" = "i86pc" ]; then
            arch=intel-S2
        else
            arch=sparc-S2
        fi
    elif [ "$un" = "Linux" ]; then
        arch=intel-Linux
	#SYMLINK_PACKAGE=
	ALTERNATIVE_ROOT_CMD="-root"
	if [ "$LOCAL_ZONE_ONLY" != "" ]
	then
	    error "Option --local-zone is not suppoted for Linux."
	fi
    else
        error "Neither SunOS nor Linux... Exiting..."
    fi
    
    if [ "$arch" != "__expected_arch" ]
    then
	error "The Sun Studio expects __expected_arch platform."
    fi

    if [ "$arch" = "intel-Linux" ]
    then
	rpm --version 2>/dev/null >/dev/null
	if [ "$?" -ne 0 ]
	then
	    error "RPM utility not found. Sun Studio can not be installed." 
	fi 
    fi
        
    PACKAGES_DIR=${TMP_DIR}/packages
     if [ "$arch" = "intel-Linux" ]; then
        install_dir="/opt/sun"
    else
        install_dir="/opt"
    fi
    INSTALLATION_DIR="${ALTERNATIVE_ROOT}${BASE_DIR-$install_dir}"
    UNINSTALLER_SCRIPT=$INSTALLATION_DIR/uninstaller.sh

    PACKAGE_LIST="__package_list";
    if [ "$CREATE_SYMLINKS" != "" ]
    then
	PACKAGE_LIST="$PACKAGE_LIST $SYMLINK_PACKAGE"
    fi
    mkdir -p $INSTALLATION_DIR || error "Unable to install Sun Studio in $INSTALLATION_DIR"
    disk_space=`df -k $INSTALLATION_DIR | tail ${tail_args} -1 | awk '{if ( $4 ~ /%/) { print $3 } else { print $4 } }'`
    if [ "$disk_space" -lt "__disk_space_required" ]; then
	printf "You have %s kBytes of Disk Free\n"  $disk_space
	printf "You will need atleast %s kBytes of Disk Free\n"  __disk_space_required
	printf "Please free up the required Disk Space and try again\n"
	exit 3
    fi

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
echo "    Install into local zone only. (Supported only for Solaris)"
echo "  --create-symlinks"
echo "    Create symlinks in /usr/bin."
#echo "  --install-patches"
#echo "    Install additional patches."
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
	    LINUX_PREFIX_ARG="--prefix ${BASE_DIR}"
	    ;;
	--alternative-root)
	    shift
	    ALTERNATIVE_ROOT="$1"
	    ALTERNATIVE_ROOT_ARG="$ALTERNATIVE_ROOT_CMD $1"
	    ;;
	--local-zone)
	    LOCAL_ZONE_ONLY=-G
	    ;;
	--create-symlinks)
	    CREATE_SYMLINKS=1
	    ;;	    
#	--install-patches)
#	    INSTALL_PATCHES=1
#	    ;;	
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
if [ "$ACCEPT_LICENSE" = "" ]
then 
    print_usage;
    error "Error. You should accept license to install Sun Studio."
fi
}

check_existing_sunstudio() {
    for package in $PACKAGE_LIST 
    do
	if [ "$arch" = "intel-Linux" ]
	then
	    package=`echo $package | sed s/.rpm//`
	    rpm -q $package 2>/dev/null >/dev/null
	    result=$?
	    if [ "$result" -eq 0 ]
	    then
    		message "The package $package is already installed."
		installed=true
	    fi
	    continue;
	fi	
	if [ "`pkginfo $ALTERNATIVE_ROOT_ARG $package 2>/dev/null`" = "" ]
	then 
	    continue
	fi
	installed_packages=`pkginfo -x | grep $package\. | cut -f1 -d ' '`
	for installed_package in $installed_packages
	do
	    #echo "Check $installed_package"
	    version=`pkginfo -l $ALTERNATIVE_ROOT_ARG  $installed_package | grep VERSION | sed s/' '*VERSION:' '*// | cut -f1 -d','`
	    if [ "$version" = "$SOLARIS_VERSION" ]
	    then
		message "The package $package with same version $version is already installed with name $installed_package."
		installed=true
	    fi
	done	
    done
    
    if [ "$installed" = "true" ]
    then
	error "The Sun Studio packages are found in the system. The installation could not be completed while they are not removed."
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
	if [ "$arch" = "intel-Linux" ] 
	then
	    rpm -i --nodeps $ALTERNATIVE_ROOT_ARG $LINUX_PREFIX_ARG  $PACKAGES_DIR/$package 2>/dev/null >/dev/null
	    result=$?
	    if [ "$result" -eq 0 ]
	    then
    		PACKAGE_LIST_INSTALLED="`echo $package | sed s/.rpm//` $PACKAGE_LIST_INSTALLED"
	    fi
	else
	    response=`pkgadd -n $LOCAL_ZONE_ONLY $ALTERNATIVE_ROOT_ARG -d $PACKAGES_DIR -a $TMP_DIR/adminfile $package 2>&1`
	    result=$?
	    #echo "Result : $result"
	    #echo "RESPONSE ============================"
	    #echo "$response"
	    name=`echo $response | grep 'Installation' | cut -f2 -d '<' | cut -f1 -d '>'`
	    if [ "$result" -eq 0 ]
	    then
    		#echo "Package $name was installed"
		PACKAGE_LIST_INSTALLED="$name $PACKAGE_LIST_INSTALLED"
	    else
		message "The error was occured during package $name installation."
	    fi
	fi
    done
}


unpack() {
    message "Please wait while Sun Studio files are unpacking into temporary directory."
    tail +__tail_length > ${TMP_DIR}/sunstudio.tar.bz2
    cd ${TMP_DIR}
    bzcat sunstudio.tar.bz2 | tar -xf - || error "Sun Studio unpacking failed."
    cd ${CWD}    
}

create_uninstaller() {
    echo \#!/bin/sh > $UNINSTALLER_SCRIPT
    echo "echo Uninstalling Sun Studio." >> $UNINSTALLER_SCRIPT
    for package in $PACKAGE_LIST_INSTALLED
    do
	if [ "$arch" = "intel-Linux" ] 
	then
	    echo "rpm -e --nodeps $ALTERNATIVE_ROOT_ARG $package" >> $UNINSTALLER_SCRIPT
	else
	    echo "yes | pkgrm $ALTERNATIVE_ROOT_ARG  $package" >> $UNINSTALLER_SCRIPT
	fi
    done
    if [ "$STSUPPORTED" -eq 1 ]
    then 
	REGISTRATION_UIN=`cat $REGISTRATION_DIR/servicetag.xml  | grep instance | cut -f2 -d'<' | cut -f2 -d'>'`
	echo "`which stclient` -d -i $REGISTRATION_UIN 2>/dev/null >/dev/null" >> $UNINSTALLER_SCRIPT
    fi
    echo "rm $UNINSTALLER_SCRIPT" >> $UNINSTALLER_SCRIPT
    echo "echo Finished." >> $UNINSTALLER_SCRIPT
    chmod 744 $UNINSTALLER_SCRIPT
    echo "Uninstaller script was written $UNINSTALLER_SCRIPT"
}



parse_args $*;
init;
print_params;
check_existing_sunstudio;
unpack;
install_packages;
REGISTRATION_DIR=$INSTALLATION_DIR/$SUNSTUDIO_DIR_NAME/prod/lib/condev
cd $TMP_DIR/servicetag/
. ./register.sh 
create_uninstaller;

exit 0;

###### Archive starts here ##########