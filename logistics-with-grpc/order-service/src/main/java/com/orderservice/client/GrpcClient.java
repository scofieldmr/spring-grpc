package com.orderservice.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import product.ProductDetailsRequest;
import product.ProductResponse;
import product.ProductServiceGrpc;

@Service
public class GrpcClient {

    private final ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    public GrpcClient(){
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext().build();

        blockingStub = ProductServiceGrpc.newBlockingStub(channel);
    }

    public ProductResponse getProductDetails(long ProductId) {
        ProductResponse response = blockingStub.getProduct(ProductDetailsRequest.newBuilder().setId(ProductId).build());
        return response;
    }

}
