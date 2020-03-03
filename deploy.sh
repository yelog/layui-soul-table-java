#!/bin/bash
tomcat_path='/data/server/apache-tomcat-9.0.31'
package_path='/data/server/package/layui-soul-table-java.war'
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
#echo 'start unzip and move package to tomcat folder'
#unzip -oq $package_path -d $tomcat_path/webapps/ROOT/
cp $package_path $tomcat_path/webapps/
sh $tomcat_path/bin/startup.sh