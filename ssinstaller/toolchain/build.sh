cd `dirname "$0"`
TARDIR=`pwd`/build/tars
RESDIR=`pwd`/build/result
SRCDIR=`pwd`

# The length of register.sh is used to untar archive 
LENGTH=`wc -l installer.sh | sed s/installer.sh// | sed s/' '//g`
LENGTH=`expr $LENGTH + 1`

DISTRS="intel-S2"
DISTRS="intel-S2 sparc-S2 intel-Linux"


# The images of Sun Studio to create distribution
BUILD_NUMBER=`ls -lA /shared/dp/sstrunk/biweekly | sed s/.*' '//`
#IMAGE_DIR=/shared/dp/sstrunk/latest/builds/intel-S2/c_installers/dvd_image_universal/install-intel-S2/packages-intel-S2
BUILD_DATE=`ls -lA /shared/dp/sstrunk/$BUILD_NUMBER | sed s/.*' '//`

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

    IMAGES_DIR=/shared/dp/sstrunk/latest/builds/$distr/c_installers/dvd_image_universal/install-$distr/packages-$distr


    DISTR_NAME="$RESDIR/Studio-toolchain-${TARGET_OS}-${TARGET_PLATFORM}-${BUILD_DATE}.sh"   
    echo Generating $DISTR_NAME
    
    PACKAGE_DIR="$IMAGES_DIR"
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
    done < package-list.$distr.real
    
    #cp -r $PACKAGE_DIR/$package $TARDIR/$PACKAGES 
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