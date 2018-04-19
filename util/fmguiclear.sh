#!/bin/bash
if [ -d ~/.Intel/FabricManagerGUI ]; then
   clear
   echo "WARNING: You are about to delete the Fabric Manager GUI database holding all of your host configuration settings!!!"
   echo -n "Do you wish to proceed? (y/n) "
   read answer
   if [ "$answer" == "y" ] || [ "$answer" == "Y" ]; then
      echo 
      echo "Deleting Fabric Manager GUI database..."
      echo "rm -f $HOME/.Intel/FabricManagerGUI/db/*"
      rm -f $HOME/.Intel/FabricManagerGUI/db/*
      echo "rm -f $HOME/.Intel/FabricManagerGUI/logs/*"
      rm -f $HOME/.Intel/FabricManagerGUI/logs/*
      echo "Fabric Manager GUI database has been deleted!" 
      echo 
      read -n1 -r -p "Press <ENTER> to continue..." answer
      #read -n1 -r -p "Fabric Manager GUI database has been deleted!" 
      #echo "Press <ENTER> to continue"
   fi
else
   echo "Fabric Manager GUI database not found for this user!"
fi
