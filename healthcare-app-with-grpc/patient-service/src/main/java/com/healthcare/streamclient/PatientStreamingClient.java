package com.healthcare.streamclient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import patient.PatientRegistrationRequest;
import patient.PatientServiceGrpc;
import patient.Empty;

import java.util.List;

public class PatientStreamingClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9000)
                .usePlaintext()
                .build();

        PatientServiceGrpc.PatientServiceStub asyncStub = PatientServiceGrpc.newStub(channel);

        StreamObserver<Empty> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(Empty empty) {
                System.out.println("Patients saved successfully.");
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error during streaming: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Streaming completed.");
            }
        };

        StreamObserver<PatientRegistrationRequest> requestObserver = asyncStub.streamPatients(responseObserver);

        List<PatientRegistrationRequest> patients = List.of(
                PatientRegistrationRequest.newBuilder()
                        .setFirstName("Alice")
                        .setLastName("Smith")
                        .setEmail("alice@example.com")
                        .setPhone("1234567890")
                        .setAddress("123 Main St")
                        .build(),

                PatientRegistrationRequest.newBuilder()
                        .setFirstName("Bob")
                        .setLastName("Johnson")
                        .setEmail("bob@example.com")
                        .setPhone("9876543210")
                        .setAddress("456 Oak St")
                        .build(),

                PatientRegistrationRequest.newBuilder()
                        .setFirstName("Charlie")
                        .setLastName("Brown")
                        .setEmail("charlie@example.com")
                        .setPhone("5555555555")
                        .setAddress("789 Pine St")
                        .build()
        );

        // Send the patient stream
        for (PatientRegistrationRequest request : patients) {
            requestObserver.onNext(request);
        }

        // Complete the stream
        requestObserver.onCompleted();

        // Wait briefly to allow async processing to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        channel.shutdown();
    }
}
