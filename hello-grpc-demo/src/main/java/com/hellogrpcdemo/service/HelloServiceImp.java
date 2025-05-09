package com.hellogrpcdemo.service;

import hellogrpcdemo.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class HelloServiceImp extends HelloServiceGrpc.HelloServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImp.class);

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

        logger.info("Received request: " + request);

        HelloResponse response = HelloResponse.newBuilder()
                .setResponse("Hello " + request.getName() + " , Welcome to GRPC Demo..")
                .build();

        logger.info("Received response: " + response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createNewPerson(PersonRequest request, StreamObserver<PersonResponse> responseObserver) {
        logger.info("Received request: " + request);

        PersonResponse response = PersonResponse.newBuilder()
                .setName(request.getName()).setEmail(request.getEmail())
                .build();

        logger.info("Received response: " + response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
