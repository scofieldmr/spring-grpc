package com.productservice.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import order.OrderServiceGrpc;
import product.*;

import java.util.concurrent.CountDownLatch;

public class GrpcTestClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090).usePlaintext().build();

        ManagedChannel channel1 = ManagedChannelBuilder
                .forAddress("localhost", 9091).usePlaintext().build();

        ProductServiceGrpc.ProductServiceStub asyncStub = ProductServiceGrpc.newStub(channel);

        OrderServiceGrpc.OrderServiceStub asyncStub2 = OrderServiceGrpc.newStub(channel1);

        addProducts(asyncStub);

        trackOrders(asyncStub);
    }

    private static void trackOrders(ProductServiceGrpc.ProductServiceStub stub) {

        System.out.println("Place order request");

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<OrderResponse> responseStreamObserver = new StreamObserver<>() {

            @Override
            public void onNext(OrderResponse orderResponse) {
               System.out.println("Server Response : " +orderResponse.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Order completed");
                latch.countDown();
            }
        };

        StreamObserver<OrderRequest> requestStreamObserver = stub.trackOrders(responseStreamObserver);
        requestStreamObserver.onNext(OrderRequest.newBuilder()
                .setProductId(1).setQuantity(5).build());
        requestStreamObserver.onNext(OrderRequest.newBuilder()
                .setProductId(2).setQuantity(4).build());
        requestStreamObserver.onNext(OrderRequest.newBuilder()
                .setProductId(3).setQuantity(5).build());

        requestStreamObserver.onCompleted();

        try {
            latch.await(); // 👈 Wait for server to respond
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private static void addProducts(ProductServiceGrpc.ProductServiceStub stub) {

        System.out.println("Creating products...");

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ProductStreamRegistrationResponse> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(ProductStreamRegistrationResponse value) {
                System.out.println("Server response: " + value.getMessage() + " | Count: " + value.getCount());
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error during AddProducts: " + throwable.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Finished AddProducts stream.");
                latch.countDown();
            }
        };

        StreamObserver<ProductRegistrationRequest> requestStreamObserver = stub.addProducts(responseObserver);

        requestStreamObserver.onNext(ProductRegistrationRequest.newBuilder()
                .setName("Dell Mouse 1").setCategory("Mouse").setDescription("Wireless Mouse")
                .setPrice(1500).build());

        requestStreamObserver.onNext(ProductRegistrationRequest.newBuilder()
                .setName("Dell Mouse 2").setCategory("Mouse").setDescription("Wireless Mouse")
                .setPrice(1500).build());

        requestStreamObserver.onNext(ProductRegistrationRequest.newBuilder()
                .setName("Dell Mouse 3").setCategory("Mouse").setDescription("Wireless Mouse")
                .setPrice(1500).build());

        requestStreamObserver.onCompleted();

        try {
            latch.await(); // 👈 Wait for server to respond
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
