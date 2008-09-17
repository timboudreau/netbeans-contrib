[ "$#" -eq 1 ] || exit 65
DEST_DIR=$1

PLATFORMS="$DISTRS"

for platform in $PLATFORMS
do




mkdir -p $DEST_DIR
touch $DEST_DIR/dummy

DEST_PACKAGES=$DEST_DIR/$platform
DEST_NB=$DEST_DIR/nb

echo "Copy packages $platform in $DEST_PACKAGES from $SS_PACKAGES_DIR"

[ ! -d $SS_PACKAGES_DIR ] && echo "There is no directory $SS_PACKAGES_DIR" &&  exit 1


rm -rf $DEST_PACKAGES/*
mkdir -p $DEST_PACKAGES
cp -r $SS_PACKAGES_DIR/* $DEST_PACKAGES

if [ $platform = "intel-Linux" ]
then 
    continue
fi

cd $DEST_PACKAGES
for pkgname in `ls`
do
    pkgtrans -s . $pkgname.1 $pkgname
    rm -rf $pkgname
    mv $pkgname.1 $pkgname
done

echo "Generating $platform in $DEST_PACKAGES Finished"

done

rm -rf $DEST_NB
mkdir -p $DEST_NB
cp $NB_ARCHIVE_DIR/* $DEST_NB
cd $DEST_NB
mv atd-cluster*.zip atd-cluster.zip   
mv netbeans-6.1*.zip netbeans-6.1.zip
unzip netbeans-6.1.zip
cd netbeans
zip -r netbeans-6.1.zip *
mv netbeans-6.1.zip ..
cd ..
rm -rf netbeans
