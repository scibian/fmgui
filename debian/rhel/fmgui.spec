#
# spec file for package fmgui
#
# Copyright (c) 2015 Intel Corporation
#
%define name IntelOPA-FMGUI
%define appdir IntelOPA-FMGUI-10.7.0.0
%define appfolder fmgui
%define appjar fmgui.jar
%define _binary_payload w9.gzdio

Name: IntelOPA-FMGUI
Version: 10.7.0.0
Release: 145
Summary: Fabric Manager Graphical User Interface
Group: Applications/System
ExclusiveArch: noarch
BuildArch: noarch
Source0: IntelOPA-FMGUI-10.7.0.0.source.tar.gz 
Buildroot: %{_topdir}/%{name}-%{version}-buildroot
URL: www.intel.com
Requires: libgnome
License: BSD 3-clause "New" or "Revised" License

%description
FMGUI is the Fabric Manager Graphical User Interface.  It can be run by invoking the Bash
script fmgui.

%pre
JAVA_VER=1.7
JAVA32_URL=http://javadl.sun.com/webapps/download/AutoDL?BundleId=106358
JAVA64_URL=http://javadl.sun.com/webapps/download/AutoDL?BundleId=106360
JAVA_REQ=`echo $JAVA_VER | sed -e 's;\.;0;g'`
usrExes=`find /usr -path "*bin/java" 2>&1 | grep -v "Permission denied" | grep java$ | xargs echo`
optExes=`find /opt -path "*bin/java" 2>&1 | grep -v "Permission denied" | grep java$ | xargs echo`
allExes=( `for E in "${optExes[@]}" "${usrExes[@]}" ; do echo "$E" ; done` )
for JAVA_EXE in "${allExes[@]}"
    do
    echo Checking $JAVA_EXE
        JVER=`$JAVA_EXE -version 2>&1 | grep "java version" | awk '{print substr($3, 2, 3);}' | sed -e 's;\.;0;g'`
        JBIT=`$JAVA_EXE -version 2>&1 | grep " VM " | awk ' { if(/64-Bit/ || /64-bit/) {print "64-Bit"} else {print "32-Bit"}}'`
        if [ $JVER -ge $JAVA_REQ ]; then
        if [ "$JBIT" == "64-Bit" ]; then
              if [ -z "$JAVA64" ]; then 
                 JAVA64=$JAVA_EXE
              fi
           else
              if [ -z "$JAVA32" ]; then 
                 JAVA32=$JAVA_EXE
              fi
           fi
        fi
    done
OSBIT=`getconf LONG_BIT`
if [ -d "/var/tmp/fmgui" ]; then
   rm -rf /var/tmp/fmgui    
fi
mkdir -p /var/tmp/fmgui
if [ "$OSBIT" == "32" ]; then
   if [ -z "$JAVA32" ]; then
      echo "Java $JAVA_VER is required and could not be found in your system. Attempting to download:"
      echo "JRE $JAVA_VER 32-bit: $JAVA32_URL"
      JAVA_URL=$JAVA32_URL
   else 
      JAVA_FOLDER=$(dirname "$JAVA64")
      JAVA_FOLDER=$(dirname "$JAVA_FOLDER")
      echo "export OPA_JAVA=$JAVA_FOLDER" >> /var/tmp/fmgui/fmguivars.tmp
      echo OPA_JAVA will be set to: $JAVA_FOLDER
      exit 0
   fi
else
   if [ -z "$JAVA64" ]; then
      if [ -z "$JAVA32" ]; then
     echo "Java $JAVA_VER is required and could not be found in your system. Attempting to download:"
      else 
     echo "Java $JAVA_VER is required but only a 32-bit version was found. Attempting to download:"
      fi
      echo "JRE $JAVA_VER 64-bit: $JAVA64_URL"
      JAVA_URL=$JAVA64_URL
   else
      JAVA_FOLDER=$(dirname "$JAVA64")
      JAVA_FOLDER=$(dirname "$JAVA_FOLDER")
      echo "export OPA_JAVA=$JAVA_FOLDER" >> /var/tmp/fmgui/fmguivars.tmp
      echo OPA_JAVA will be set to: $JAVA_FOLDER
      exit 0
   fi 
fi
wget "$JAVA_URL" -O /var/tmp/fmgui/JavaRuntimeEnvironmentForFMGUI.gz -t 1
if [ $? -ne 0 ]; then
   echo "Download of Java Runtime $JAVA_VER failed"
   exit 1
fi
mkdir -p /var/tmp/fmgui/temp
cd /var/tmp/fmgui/temp
tar -xzf /var/tmp/fmgui/JavaRuntimeEnvironmentForFMGUI.gz
if [ $? -ne 0 ]; then
   echo "Unzipping of Java Runtime $JAVA_VER failed"
   exit 1
fi
JAVA_FOLDER=$(ls)
echo Java just installed: $JAVA_FOLDER  
mv $JAVA_FOLDER /opt
if [ $? -ne 0 ]; then
   echo "Moving Java Runtime $JAVA_VER to /opt directory failed"
   exit 1
fi
echo "export OPA_JAVA=/opt/$JAVA_FOLDER" >> /var/tmp/fmgui/fmguivars.tmp
echo Environment variable OPA_JAVA will be set to: /opt/$JAVA_FOLDER
exit 0

%prep
%setup -c -q

%build
echo "JAVA_HOME:               " $JAVA_HOME
echo "ANT_HOME:                " $ANT_HOME
if [ -n "$ANT_HOME" ]; then
   PATH=$ANT_HOME/bin:$PATH
fi
if [ -n "$JAVA_HOME" ]; then
   PATH=$JAVA_HOME/bin:$PATH
fi
export PATH
echo "PATH:                    " $PATH
cd %{name}-%{version}
ant -v build
if  [ -e "%{_topdir}/%{appjar}" ]; then
    cp %{_topdir}/%{appjar} .
fi

%install
rm -rf %{buildroot}
mkdir -p %{buildroot}%{_javadir}/%{appfolder}
mkdir -p %{buildroot}%{_javadir}/%{appfolder}/lib
mkdir -p %{buildroot}%{_javadir}/%{appfolder}/help
mkdir -p %{buildroot}%{_javadir}/%{appfolder}/util
mkdir -p %{buildroot}/usr/local/bin
mkdir -p %{buildroot}/usr/local/share/applications
mkdir -p %{buildroot}/usr/local/share/desktop-directories
mkdir -p %{buildroot}/usr/local/share/icons/hicolor/48x48/apps
mkdir -p %{buildroot}/etc/xdg/menus/applications-merged
mkdir -p %{buildroot}/etc/profile.d
mkdir -p %{buildroot}/var/opt/opafm/fmgui
cp -a %{appdir}/%{appjar} %{buildroot}%{_javadir}/%{appfolder}
cp -a %{appdir}/LICENSE %{buildroot}%{_javadir}/%{appfolder}
cp -a %{appdir}/THIRD-PARTY-README %{buildroot}%{_javadir}/%{appfolder}
if [ -f %{appdir}/Pre-Release_Notice_v.2.pdf ]; then
   cp -a %{appdir}/Pre-Release_Notice_v.2.pdf %{buildroot}%{_javadir}/%{appfolder}
fi
cp -a %{appdir}/Third_Party_Copyright_Notices_and_Licenses.docx %{buildroot}%{_javadir}/%{appfolder}
cp -a %{appdir}/lib/* %{buildroot}%{_javadir}/%{appfolder}/lib
cp -a %{appdir}/help/* %{buildroot}%{_javadir}/%{appfolder}/help
cp -a %{appdir}/util/fmguiclear.sh %{buildroot}%{_javadir}/%{appfolder}/util
cp -a %{appdir}/util/postsetup.sh %{buildroot}%{_javadir}/%{appfolder}/util
cp -a %{appdir}/util/ClearFMGUICache.desktop %{buildroot}%{_javadir}/%{appfolder}/util
cp -a %{appdir}/install/fmgui.sh %{buildroot}/usr/local/bin/fmgui
cp -a %{appdir}/install/fmguivars.sh %{buildroot}/etc/profile.d
cp -a %{appdir}/install/fmgui.desktop %{buildroot}/usr/local/share/applications
cp -a %{appdir}/install/Fabric.directory %{buildroot}/usr/local/share/desktop-directories
cp -a %{appdir}/install/images/* %{buildroot}/usr/local/share/icons/hicolor
cp -a %{appdir}/install/Fabric.menu %{buildroot}/etc/xdg/menus/applications-merged

%post
cat /var/tmp/fmgui/fmguivars.tmp >> /etc/profile.d/fmguivars.sh
rm -rf /var/tmp/fmgui   

%files
%defattr(755,root,root)
%{_javadir}/%{appfolder}
%attr(755,root,root) /usr/local/bin/fmgui
%attr(644,root,root) /usr/local/share/applications/fmgui.desktop
%attr(644,root,root) /usr/local/share/desktop-directories/Fabric.directory
/usr/local/share/icons/hicolor
%attr(644,root,root) %{_sysconfdir}/xdg/menus/applications-merged/Fabric.menu
%attr(755,root,root) %{_sysconfdir}/profile.d/fmguivars.sh
%attr(751,root,root) /var/opt/opafm/fmgui
