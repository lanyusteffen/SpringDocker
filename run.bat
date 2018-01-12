@echo off
set base_dir=%~dp0
%base_dir:~0,2%
pushd %base_dir%
docker run -d -p 8080:8080 --name spring_docker_api --link postgres_pg-master_1:postgres_pg-master_1 --link postgres_pg-slave_1:postgres_pg-slave_1 --link redis_docker-redis-sentinel1_1:redis_docker-redis-sentinel1_1 --link redis_docker-redis-sentinel2_1:redis_docker-redis-sentinel2_1 --link redis_docker-redis-sentinel3_1:redis_docker-redis-sentinel3_1 -lt stelylan/spring-boot-docker
popd


