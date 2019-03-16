#!/bin/bash
scp target/layui-soul-table-java.war root@server63:/usr/dev/layui-soul-table/package
ssh root@server63 'sh /usr/dev/layui-soul-table/script/deploy.sh'