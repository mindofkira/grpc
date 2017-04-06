package com.bgautam.grpc.test;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gautam on 06/04/17.
 */
public class GrpcClient {
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public GrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example
                // we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true));
    }

    /**
     * Construct client for accessing RouteGuide server using the existing
     * channel.
     */
    GrpcClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name
     * to use in the greeting.
     */
    public static void main(String[] args) throws Exception {
        GrpcClient client = new GrpcClient("localhost", 8080);
        try {
            /* Access a service running on the local machine on port 50051 */
            String user = "world";
            if (args.length > 0) {
                user = args[0]; /*
								 * Use the arg as the name to greet if provided
								 */
            }
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Say hello to server.
     */
    public void greet(String name) {
        int count = 0;
        long startTime = System.currentTimeMillis();

        while (true) {
            if (count++ == 10000) {
                break;
            }
            HelloRequest request = HelloRequest.newBuilder().setName(name).build();
            try {
                HelloReply response = blockingStub.sayHello(request);
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return;
            }

        }
        long stopTime = System.currentTimeMillis();

        logger.info("Total Time : " + (stopTime - startTime) + " Ms");
    }
}
