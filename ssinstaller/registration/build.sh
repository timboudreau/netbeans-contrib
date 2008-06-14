cd `dirname "$0"`
TARDIR=`pwd`/build/tars
RESDIR=`pwd`/build/result
SRCDIR=`pwd`
LENGTH=`wc -l register.sh | sed s/register.sh// | sed s/' '//g`
LENGTH=`expr $LENGTH + 1`
echo $LENGTH
DISTRS="intel-S2 sparc-S2 intel-Linux"
#DISTRS="intel-Linux"
#rm -rf $TARS 
IMAGES_DIR=/shared/dp/sstrunk/biweekly/output/image_tars
#BUILD_NUMBER=`ls -all /shared/dp/sstrunk/biweekly | sed s/.*' '//  | tr . _`
#IMAGES_DIR=/shared/dp/sstrunk

rm -rf build
mkdir -p $RESDIR
for distr in $DISTRS
do
    echo Generating $distr
    mkdir -p $TARDIR/$distr
    cd $TARDIR/$distr
    case $distr in
    intel-S2)
	TARGET_OS=SunOS
    ;;
    sparc-S2)
	TARGET_OS=SunOS
    ;;
    intel-Linux)
	TARGET_OS=Linux
    ;;
    esac
    DISTR_NAME=$RESDIR/sunstudio-$distr.sh
    
    #bzcat $IMAGES_DIR/*.$distr.tar.bz2 | /usr/sfw/bin/gtar -xf - 
    cp -r $SRCDIR/servicetag $TARDIR/$distr
    tar cf $TARDIR/sunstudio.$distr.tar *
    bzip2 $TARDIR/sunstudio.$distr.tar
    cat $SRCDIR/register.sh | sed s/__os_name/"${TARGET_OS}"/ |  sed s/__tail_length/"$LENGTH \$0"/ >  $DISTR_NAME
    cat $TARDIR/sunstudio.$distr.tar.bz2 >>  $DISTR_NAME
    chmod u+x  $DISTR_NAME
    echo
done