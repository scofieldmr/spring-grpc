package com.productservice.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import order.OrderRegistrationRequest;
import order.OrderRegistrationResponse;
import order.OrderServiceGrpc;
import org.springframework.stereotype.Service;


@Service
public class GrpcClient {

    private final OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    public GrpcClient() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9091).usePlaintext().build();

        orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
    }

    public OrderRegistrationResponse placeOrder(OrderRegistrationRequest orderRequest) {
        OrderRegistrationResponse response = orderServiceBlockingStub
                .placeOrder(OrderRegistrationRequest.newBuilder().setProductId(orderRequest.getProductId())
                        .setQuantity(orderRequest.getQuantity()).build());
        return response;
    }
}
