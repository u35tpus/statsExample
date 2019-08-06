#!/bin/sh
mvn clean package && java -jar target/tst-1.0-SNAPSHOT.jar
