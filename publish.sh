#!/bin/bash

SPRING_PROJECT_PATH=/home/stelylan/IdeaProjects
REPOSITORY_URL=www.lanmuyan.xin:5000

cd $SPRING_PROJECT_PATH/SpringDocker
echo 开始编译Spring项目并创建Docker镜像!
./gradlew buildDocker
echo 设置上传仓储镜像Tag
docker tag stelylan/task-manager $REPOSITORY_URL/stelylan/spring-boot-docker
echo 上传镜像到仓储
docker push $REPOSITORY_URL/stelylan/spring-boot-docker
echo 清除临时镜像
docker rmi -f $REPOSITORY_URL/stelylan/spring-boot-docker
docker rmi -f stelylan/spring-boot-docker
echo Spring项目Docker发布结束!
