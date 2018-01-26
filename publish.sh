#!/bin/bash

SPRING_PROJECT_PATH=/home/stelylan/IdeaProjects
REPOSITORY_URL=www.lanmuyan.xin:5000
SSH_URL=www.lanmuyan.xin
DOCKER=stelylan/spring-boot-docker

cd $SPRING_PROJECT_PATH/SpringDocker
echo 开始编译Spring项目并创建Docker镜像!
./gradlew buildDocker
echo 设置上传仓储镜像Tag
docker tag $DOCKER $REPOSITORY_URL/$DOCKER
echo 上传镜像到仓储
docker push $REPOSITORY_URL/$DOCKER
echo 清除临时镜像
docker rmi -f $REPOSITORY_URL/$DOCKER
docker rmi -f $DOCKER
echo 开始SSH连接远程服务器
sudo ssh -l root $SSH_URL << remotessh
docker stop spring_docker_api
docker rm spring_docker_api
docker rmi -f $DOCKER
docker pull 127.0.0.1:5000/$DOCKER
docker tag 127.0.0.1:5000/$DOCKER $DOCKER
docker run -d -p 8080:8080 --link redis_docker-redis-sentinel1_1:redis_docker-redis-sentinel1_1 --link redis_docker-redis-sentinel2_1:redis_docker-redis-sentinel2_1 --link redis_docker-redis-sentinel3_1:redis_docker-redis-sentinel3_1 --name=spring_docker_api $DOCKER
docker rmi -f 127.0.0.1:5000/$DOCKER
exit
remotessh
echo Spring项目Docker发布结束!
