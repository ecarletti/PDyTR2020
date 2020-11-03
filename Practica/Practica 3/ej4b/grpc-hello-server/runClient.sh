#!/bin/bash

mvn -DskipTests exec:java -Dexec.mainClass=pdytr.example.grpc.ClientA -q &
mvn -DskipTests exec:java -Dexec.mainClass=pdytr.example.grpc.ClientB -q &