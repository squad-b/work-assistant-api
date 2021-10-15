REPOSITORY_DIR=/home/ubuntu/app/deploy

if [ -d $REPOSITORY_DIR ]; then
    sudo rm -rf $REPOSITORY_DIR
fi
sudo mkdir -vp $REPOSITORY_DIR