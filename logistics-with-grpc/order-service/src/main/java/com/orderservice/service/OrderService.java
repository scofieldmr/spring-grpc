package com.orderservice.service;

import com.orderservice.client.GrpcClient;
import com.orderservice.entity.Orders;
import com.orderservice.repository.OrderRepository;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.criteria.Order;
import net.devh.boot.grpc.server.service.GrpcService;
import order.OrderRegistrationRequest;
import order.OrderRegistrationResponse;
import order.OrderServiceGrpc;

@GrpcService
public class OrderService extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderRepository repository;
    private final GrpcClient grpcClient;

    public OrderService(OrderRepository repository, GrpcClient grpcClient) {
        this.repository = repository;
        this.grpcClient = grpcClient;
    }

    @Override
    public void placeOrder(OrderRegistrationRequest request, StreamObserver<OrderRegistrationResponse> responseObserver) {
        var product = grpcClient.getProductDetails(request.getProductId());

        Orders orders = new Orders();
        orders.setProductId(product.getId());
        orders.setQuantity(request.getQuantity());
        orders.setProductName(product.getName());
        orders.setTotalPrice(product.getPrice()*request.getQuantity());
        Orders newOrder = repository.save(orders);

        OrderRegistrationResponse response = OrderRegistrationResponse.newBuilder()
                .setOrderId(newOrder.getId()).setMessage("Order Placed Sucessfully for the Product :"+ newOrder.getProductName()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
