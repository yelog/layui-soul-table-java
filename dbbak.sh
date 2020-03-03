#!/bin/bash
docker exec -it mysql mysqldump -uroot -p123456 soulTable > backup/backup$(date "+%Y%m%d%H%M%S").sql
docker exec -it mysql -uroot -p123456 soulTable