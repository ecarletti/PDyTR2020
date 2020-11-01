package pdytr.example.grpc;
import io.grpc.*;

import java.util.concurrent.TimeUnit;

public class ClientC
{
    public static void main( String[] args ) throws Exception
    {
      // Channel is the abstraction to connect to a service endpoint
      // Let's use plaintext communication because we don't have certs
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
        .usePlaintext(true)
        .build();

      // It is up to the client to determine whether to block the call
      // Here we create a blocking stub, but an async stub,
      // or an async stub with Future are always possible.
      GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
      GreetingServiceOuterClass.HelloRequest request =
        GreetingServiceOuterClass.HelloRequest.newBuilder()
          .setName("Ray")
          .build();
      //deadline en ms
      int dl = 1000000;
      System.out.println("Timeout de: " + dl + " microsegundos");

      try {
        // Finally, make the call using the stub
        GreetingServiceOuterClass.HelloResponse response = stub.withDeadlineAfter(dl, TimeUnit.MICROSECONDS).greeting(request);
      } catch (Exception e) {
      e.printStackTrace();
      }

      // A Channel should be shutdown before stopping the process.
      channel.shutdownNow();
    }
}