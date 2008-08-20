cd `dirname "$0"`

TARDIR=$OUTPUT_DIR/cache/tars
RESDIR=$OUTPUT_DIR/bundles
SRCDIR=`pwd`

# The length of register.sh is used to untar archive 
LENGTH=`wc -l installer.sh | sed s/installer.sh// | sed s/' '//g`
LENGTH=`expr $LENGTH + 1`

rm -rf build
mkdir -p $RESDIR
mkdir -p $TARDIR
for distr in $DISTRS
do
    case $distr in
    intel-S2)
	TARGET_OS=SunOS
	TARGET_PLATFORM=x86
    ;;
    sparc-S2)
	TARGET_OS=SunOS
	TARGET_PLATFORM=sparc
    ;;
    intel-Linux)
	TARGET_OS=Linux
	TARGET_PLATFORM=x86
    ;;
    esac

    DISTR_NAME="$RESDIR/sunstudio-${SS_VERSION}-toolchain-${distr}.sh"   
    echo Generating $DISTR_NAME
    
    PACKAGE_DIR="$SS_PACKAGES_DIR"
    PACKAGES=packages
    PACKAGE_LIST=""
    mkdir -p $TARDIR/$PACKAGES
    while read package
    do

	if [ "$package" = "" ]
	then 
	    continue
	fi
	if [ "$package" != "SPROsslnk" ]
	then
    	    PACKAGE_LIST="$PACKAGE_LIST $package"
        fi
	
	cp -r $PACKAGE_DIR/$package $TARDIR/$PACKAGES 
    done < package-list.$distr
    
    cd $TARDIR
    tar cvf $TARDIR/sunstudio.$distr.tar $PACKAGES -C $SRCDIR servicetag
    bzip2 $TARDIR/sunstudio.$distr.tar
    cd $SRCDIR
    DISK_SPACE=`du -sk $TARDIR/$PACKAGES | cut -f1`
    echo "Required space is $DISK_SPACE"
    cat $SRCDIR/installer.sh | sed s/__os_name/"${TARGET_OS}"/ |  sed s/__tail_length/"$LENGTH \$0"/ \
    | sed s/__package_list/"$PACKAGE_LIST"/ |  sed s/__expected_arch/"$distr"/ | sed s/__disk_space_required/"$DISK_SPACE"/  >  $DISTR_NAME
    cat $TARDIR/sunstudio.$distr.tar.bz2 >>  $DISTR_NAME
    chmod u+x  $DISTR_NAME
    rm -rf $TARDIR/$PACKAGES
done
