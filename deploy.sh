#!/bin/bash
tomcat_path='/usr/local/apache-tomcat-8.5.27'
package_path='/usr/dev/layui-soul-table/package/*.war'
TomcatID=$(ps -ef |grep tomcat |grep -w $tomcat_path|grep -v 'grep'|awk '{print $2}')
if [ -n "$TomcatID" ];

       then
                echo "$TomcatID tomcat is starting ................."
                kill -9 $TomcatID
                echo 'stop tomcat'
        else
                echo "tomcat not start ============================="
fi
echo 'rewrite application============='
rm -rf $tomcat_path/webapps/ROOT/*
echo 'start unzip and move package to tomcat folder'
unzip -oq $package_path -d $tomcat_path/webapps/ROOT/
sh $tomcat_path/bin/startup.sh