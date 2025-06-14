#!/bin/bash

echo "## write application.properties from environment variable" >> /home/ec2-user/action/spring-deploy.log

mkdir -p /home/ec2-user/config

# GitHub Actions에서 환경변수로 전달된 내용을 파일로 저장
echo "$PROPERTIES_SECRET" > /home/ec2-user/config/application.properties

echo "## application.properties created" >> /home/ec2-user/action/spring-deploy.log
