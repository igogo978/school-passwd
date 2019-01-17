#!/bin/bash

username="$1"
password="$2"
role="$3"
home="$4"
isSyncSmb="$5"

if [ $isSyncSmb == "syncsmb" ]; then
	echo -e "$password\n$password" | /usr/sbin/smbldap-passwd $username
	echo "update password ok."
fi

ledDir=$(printf '%s/%s' "/home/public/led" "$username")
if [ ! -d "$ledDir" ]; then
        echo "create led directory"
        /bin/mkdir -p $ledDir
	/bin/chown $username:leduser $ledDir
        /bin/chmod 755 $ledDir
fi


#create home direcotry if no exists
if [ ! -d "$home" ]; then
	echo "create home"
	/bin/mkdir -p $home
	/bin/chown $username:$role $home
	/bin/chmod 755 $home
fi

