FROM 687690056036.dkr.ecr.us-west-2.amazonaws.com/wgu/base/awslinux/openjdk11:latest

ARG TARGET=target

RUN mkdir -p /srv/data
COPY ${TARGET}/ema-admin.jar /srv/data
USER wguuser
CMD ["java", "-jar", "/srv/data/ema-admin.jar"]
