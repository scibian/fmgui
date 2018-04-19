#!/bin/bash
THISDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo Copying $THISDIR/fmguiclear.sh to $HOME/.Intel/FabricManagerGUI
mkdir -p $HOME/.Intel/FabricManagerGUI
\cp -f $THISDIR/fmguiclear.sh $HOME/.Intel/FabricManagerGUI/fmguiclear.sh
echo Copying $THISDIR/ClearFMGUICache.desktop to $HOME/.local/share/applications
mkdir -p $HOME/.local/share/applications
\cp -f $THISDIR/ClearFMGUICache.desktop $HOME/.local/share/applications/ClearFMGUICache.desktop
chmod 744 $HOME/.local/share/applications/ClearFMGUICache.desktop
mkdir -p $HOME/.local/share/desktop-directories
cp -a /usr/local/share/desktop-directories/Fabric.directory $HOME/.local/share/desktop-directories/Fabric.directory
mkdir -p $HOME/.config/menus/applications-merged
cp -a /etc/xdg/menus/applications-merged/Fabric.menu $HOME/.config/menus/applications-merged/Fabric.menu



