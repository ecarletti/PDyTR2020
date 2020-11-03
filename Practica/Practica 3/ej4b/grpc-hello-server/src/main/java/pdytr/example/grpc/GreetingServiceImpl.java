package pdytr.example.grpc;

import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
  @Override
  public void greeting(GreetingServiceOuterClass.HelloRequest request,
        StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver) {
    // HelloRequest has toString auto-generated.
    System.out.println("Parametro(request)" + request);

    // You must use a builder to construct a new Protobuffer object
    GreetingServiceOuterClass.HelloResponse response = GreetingServiceOuterClass.HelloResponse.newBuilder()
      .setGreeting("Hello there, " + request.getName())
      .build();


      
      System.out.println("Consultando");
      for (int i = 0; i < 100000; i++) {
        System.out.println("Consulta de:" + request.getName());
      }
      System.out.println("Fin");


    // Use responseObserver to send a single response back
    responseObserver.onNext(response);

    
    // When you are done, you must call onCompleted.
    responseObserver.onCompleted();
  }
}