cd `dirname "$0"`
TARDIR=`pwd`/build/tars
RESDIR=`pwd`/build/result
SRCDIR=`pwd`

# The length of register.sh is used to untar archive 
LENGTH=`wc -l register.sh | sed s/register.sh// | sed s/' '//g`
LENGTH=`expr $LENGTH + 1`

DISTRS="intel-S2 sparc-S2 intel-Linux"

# The images of Sun Studio to create distribution
IMAGES_DIR=/shared/dp/sstrunk/biweekly/inst
BUILD_NUMBER=`ls -lA /shared/dp/sstrunk/biweekly | sed s/.*' '//`

rm -rf build
mkdir -p $RESDIR
mkdir -p $TARDIR
for distr in $DISTRS
do
    DISTR_NAME="$RESDIR/sunstudio-$BUILD_NUMBER-express-$distr.sh"   
    echo Generating $DISTR_NAME
    case $distr in
    intel-S2)
	TARGET_OS=SunOS
	IMAGE_SUB=opt
    ;;
    sparc-S2)
	TARGET_OS=SunOS
	IMAGE_SUB=opt
    ;;
    intel-Linux)
	TARGET_OS=Linux
	IMAGE_SUB=opt/sun
    ;;
    esac
    
    DIRS=`ls $IMAGES_DIR/$distr.inst/$IMAGE_SUB`
    ARGS=""
    for dir in $DIRS
    do 
	ARGS="$ARGS -C $IMAGES_DIR/$distr.inst/$IMAGE_SUB $dir" 
    done
    tar cf $TARDIR/sunstudio.$distr.tar  -C $SRCDIR servicetag $ARGS 
    bzip2 $TARDIR/sunstudio.$distr.tar
    cat $SRCDIR/register.sh | sed s/__os_name/"${TARGET_OS}"/ |  sed s/__tail_length/"$LENGTH \$0"/ >  $DISTR_NAME
    cat $TARDIR/sunstudio.$distr.tar.bz2 >>  $DISTR_NAME
    chmod u+x  $DISTR_NAME
    echo
done