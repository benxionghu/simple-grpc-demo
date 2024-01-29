package grpc.client;

import grpc.demo.HelloWorld;
import grpc.demo.HelloWorldServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @Author: benxiong.hu
 * @CreateAt: 2024/1/26 16:51
 * @ModifyAt: 2024/1/26 16:51
 * @Version 1.0
 */
public class HelloClient {

    private final ManagedChannel channel;
    /**
     * 远程服务存根
     */
    private final HelloWorldServiceGrpc.HelloWorldServiceBlockingStub stub;

    public HelloClient(String host,Integer port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        // 初始化远程服务存根
        stub = HelloWorldServiceGrpc.newBlockingStub(channel);

    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public String sayHello(String name) {
        // 构造服务调用参数对象
        HelloWorld.HelloRequest request = HelloWorld.HelloRequest.newBuilder().setName(name).build();
        // 调用远程服务方法
        HelloWorld.HelloReply response = stub.sayHello(request);
        return response.getMessage();
    }

    public static void main(String[] args) throws InterruptedException {
        HelloClient client = new HelloClient("127.0.0.1", 50051);
        String content = client.sayHello("Java client");
        System.out.println(content);
        client.shutdown();
    }
}
