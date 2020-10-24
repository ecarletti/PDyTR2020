# gRPC
## Overview
- gRPC can use protocol buffers as both its Interface Definition Language (IDL) and as its underlying message interchange format.

- In gRPC, a client application can directly call a method on a server application on a different machine as if it were a local object, making it easier for you to create distributed applications and services.

-  As in many RPC systems, gRPC is based around the idea of defining a service, specifying the methods that can be called remotely with their parameters and return types.

- On the server side, the server implements this interface and runs a gRPC server to handle client calls. On the client side, the client has a stub (referred to as just a client in some languages) that provides the same methods as the server.

## Working with Protocol Buffers

- By default, gRPC uses Protocol Buffers

- The first step when working with protocol buffers is to define the structure for the data you want to serialize in a proto file: this is an ordinary text file with a .proto extension. 

- Protocol buffer data is structured as messages, where each message is a small logical record of information containing a series of name-value pairs called fields. Here’s a simple example:
  ```java
  message Person {
    string name = 1;
    int32 id = 2;
    bool has_ponycopter = 3;
  }
  ```
  Then, once you’ve specified your data structures, you use the protocol buffer compiler protoc to generate data access classes in your preferred language(s) from your proto definition.


`docker exec -it --user root pdytr bash`

`cd /pdytr/Practica/Practica 3/pdytr-grpc-demo/grpc-hello-server`

`mvn compile`

`mvn exec:java -Dexec.mainClass="pdytr.example.grpc.Client"`

`mvn exec:java -Dexec.mainClass="pdytr.example.grpc.App"`

