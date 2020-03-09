#!/bin/bash
build_version=$(mvn -q -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.2:exec)
echo "Building Artifact version:" $build_version
mvn -B install sonar:sonar "-Drevision=$build_version" #update to use sonar
mvn deploy -B -DrepositoryId="WGU" -Durl="https://nexus.wgu.edu/content/repositories/wgu" "-Drevision=$build_version"
