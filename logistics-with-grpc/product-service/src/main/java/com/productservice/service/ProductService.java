package com.productservice.service;

import com.productservice.client.GrpcClient;
import com.productservice.client.GrpcTestClient;
import com.productservice.entity.Products;
import com.productservice.repository.ProductRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import order.OrderRegistrationRequest;
import order.OrderRegistrationResponse;
import product.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@GrpcService
public class ProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductRepository productRepository;

    private final GrpcClient grpcClient;

    public ProductService(ProductRepository productRepository, GrpcClient grpcClient) {
        this.productRepository = productRepository;
        this.grpcClient = grpcClient;
    }


    @Override
    public void getProduct(ProductDetailsRequest request, StreamObserver<ProductResponse> responseObserver) {

        Optional<Products> getProductById = productRepository.findById(request.getId());
        if (getProductById.isPresent()) {
            var product = getProductById.get();

            ProductResponse response = ProductResponse.newBuilder()
                    .setId(product.getId()).setName(product.getName()).setCategory(product.getCategory())
                    .setDescription(product.getDescription()).setPrice(product.getPrice()).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        else{
            responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Product not found with the ID : "+ request.getId()).asRuntimeException());
        }

    }

    @Override
    public void createProduct(ProductRegistrationRequest request, StreamObserver<ProductRegistrationResponse> responseObserver) {

        Optional<Products> productExists = productRepository.findByName(request.getName());
        if (productExists.isPresent()) {
            responseObserver.onError(io.grpc.Status.ALREADY_EXISTS.withDescription("Product already exists").asRuntimeException());
        }
        else {
            Products products = new Products();
            products.setName(request.getName());
            products.setCategory(request.getCategory());
            products.setDescription(request.getDescription());
            products.setPrice(request.getPrice());
            productRepository.save(products);

            ProductRegistrationResponse response = ProductRegistrationResponse.newBuilder()
                     .setId(products.getId()).setName(request.getName())
                      .setMessage("Product created successfully").build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }



    }

    @Override
    public void listProducts(Empty request, StreamObserver<ProductResponse> responseObserver) {

        List<Products> products = productRepository.findAll();
        if (products.isEmpty()) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("No products found").asRuntimeException());
        }
        else {
            for (Products product : products) {
                ProductResponse productResponse = ProductResponse.newBuilder()
                        .setId(product.getId()).setName(product.getName()).setCategory(product.getCategory())
                        .setDescription(product.getDescription()).setPrice(product.getPrice()).build();
                responseObserver.onNext(productResponse);
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ProductRegistrationRequest> addProducts(StreamObserver<ProductStreamRegistrationResponse> responseObserver) {

        return new StreamObserver<ProductRegistrationRequest>() {
            private int productCount = 0;
            @Override
            public void onNext(ProductRegistrationRequest productRegistrationRequest) {
                Optional<Products> products = productRepository.findByName(productRegistrationRequest.getName());

                if (products.isEmpty()) {
                    var p = new Products();
                    p.setName(productRegistrationRequest.getName());
                    p.setCategory(productRegistrationRequest.getCategory());
                    p.setDescription(productRegistrationRequest.getDescription());
                    p.setPrice(productRegistrationRequest.getPrice());
                    productRepository.save(p);
                    System.out.println("Successfully added Product : " + p.getName());
                    productCount++;
                }
                else{
                    System.out.println("Product already exists : " + products.get().getName());
                }
            }

            @Override
            public void onError(Throwable throwable) {
               System.out.println("Error while adding products"+throwable.getMessage());
               responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                ProductStreamRegistrationResponse response = ProductStreamRegistrationResponse.newBuilder()
                        .setCount(productCount)
                        .setMessage("Product added successfully").build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<OrderRequest> trackOrders(StreamObserver<OrderResponse> responseObserver) {
        return new StreamObserver<OrderRequest>() {

            @Override
            public void onNext(OrderRequest request) {
                OrderRegistrationRequest registrationRequest = OrderRegistrationRequest.newBuilder()
                        .setProductId(request.getProductId()).setQuantity(request.getQuantity()).build();

                OrderRegistrationResponse response = grpcClient.placeOrder(registrationRequest);

                OrderResponse orderResponse = OrderResponse.newBuilder()
                        .setOrderId(response.getOrderId()).setMessage(response.getMessage()).build();
                responseObserver.onNext(orderResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error while tracking orders"+throwable.getMessage());
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                 responseObserver.onCompleted();
            }
        };
    }
}

