package grpc.server;


import grpc.demo.HelloWorld;
import grpc.demo.HelloWorldServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: benxiong.hu
 * @CreateAt: 2024/1/26 16:39
 * @ModifyAt: 2024/1/26 16:39
 * @Version 1.0
 */
public class HelloServer {

    private Server server;

    private void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new HelloImpl())
                .build()
                .start();
        System.out.println("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // jvm关闭前执行
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }


    /**
     * 阻塞等待主线程终止
     *
     * @throws InterruptedException
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final HelloServer server = new HelloServer();
        server.start();
        server.blockUntilShutdown();
    }


    private class HelloImpl extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {
        @Override
        public void sayHello(HelloWorld.HelloRequest request, StreamObserver<HelloWorld.HelloReply> responseObserver) {
            HelloWorld.HelloReply result = HelloWorld.HelloReply.newBuilder().setMessage("你好呀，" + request.getName()).build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        }
    }

}
