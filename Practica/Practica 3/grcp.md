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

## RPC life cycle

- **Unary RPC:** simplest type of RPC where the client sends a single request and gets back a single response.

- **Server streaming RPC:** similar to a unary RPC, except that the server returns a stream of messages in response to a client’s request.

- **Client streaming RPC:** similar to a unary RPC, except that the client sends a stream of messages to the server instead of a single message.

- **Bidirectional streaming RPC:** the call is initiated by the client invoking the method and the server receiving the client metadata, method name, and deadline.

  Client- and server-side stream processing is application specific. Since the two streams are independent, the client and server can read and write messages in any order. For example, a server can wait until it has received all of a client’s messages before writing its messages, or the server and client can play “ping-pong” – the server gets a request, then sends back a response, then the client sends another request based on the response, and so on.

- **Deadlines/Timeouts:** gRPC allows clients to specify how long they are willing to wait for an RPC to complete before the RPC is terminated with a DEADLINE_EXCEEDED error. On the server side, the server can query to see if a particular RPC has timed out, or how much time is left to complete the RPC.

  Specifying a deadline or timeout is language specific: some language APIs work in terms of timeouts (durations of time), and some language APIs work in terms of a deadline (a fixed point in time) and may or maynot have a default deadline.

## Useful commands

`docker exec -it --user root pdytr bash`

`cd /pdytr/Practica/Practica\ 3/pdytr-grpc-demo/grpc-hello-server/`

`mvn compile`

`mvn exec:java -Dexec.mainClass="pdytr.grpc.UploadFileClient" -Dexec.args="IMG-20180106-WA0085.jpg"`

`mvn exec:java -Dexec.mainClass="pdytr.grpc.UploadFileServer"`

