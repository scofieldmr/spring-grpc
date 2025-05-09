package com.productservice.service;

import com.productservice.entity.Products;
import com.productservice.repository.ProductRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import product.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@GrpcService
public class ProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
            List<ProductResponse> productResponses = new ArrayList<>();
            for (Products product : products) {
                ProductResponse productResponse = ProductResponse.newBuilder()
                        .setId(product.getId()).setName(product.getName()).setCategory(product.getCategory())
                        .setDescription(product.getDescription()).setPrice(product.getPrice()).build();
                productResponses.add(productResponse);
            }

            responseObserver.onNext();
        }
    }

    @Override
    public StreamObserver<ProductRegistrationRequest> addProducts(StreamObserver<ProductRegistrationResponse> responseObserver) {
        return super.addProducts(responseObserver);
    }

    @Override
    public StreamObserver<OrderRequest> trackOrders(StreamObserver<OrderResponse> responseObserver) {
        return super.trackOrders(responseObserver);
    }
}

