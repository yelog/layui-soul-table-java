#!/bin/bash
scp target/layui-soul-table-java.war root@server35:/data/server/package
ssh root@server35 'sh /data/script/deploy.sh'
