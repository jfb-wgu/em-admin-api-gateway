FROM 687690056036.dkr.ecr.us-west-2.amazonaws.com/wgu/base/openjdk11:latest

ARG TARGET=target

RUN mkdir -p /srv/data
COPY ${TARGET}/admin-service.jar /srv/data
USER wguuser
CMD ["java", "-jar", "/srv/data/admin-service.jar"]
