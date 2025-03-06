package it.moneyverse.core.runtime.server;

import io.grpc.Server;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GrpcServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);

  protected Integer port;
  protected Server server;

  public void start() throws IOException {
    server.start();
    LOGGER.info("gRPC Server started, listening on {}", port);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.err.println("*** shutting down gRPC server since JVM is shutting down");
                  try {
                    this.stop();
                  } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                  }
                  System.err.println("*** server shut down");
                }));
  }

  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }
}
