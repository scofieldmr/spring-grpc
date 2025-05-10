package com.healthcare.grpc;

import doctor.DoctorDetailsRequest;
import doctor.DoctorDetailsResponse;
import doctor.DoctorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import patient.PatientDetails;
import patient.PatientDetailsRequest;
import patient.PatientServiceGrpc;

@Configuration
public class GrpcClientConfig {

    private static Logger logger = LoggerFactory.getLogger(GrpcClientConfig.class);

    private final DoctorServiceGrpc.DoctorServiceBlockingStub doctorServiceBlockingStub;

    private final PatientServiceGrpc.PatientServiceBlockingStub patientServiceBlockingStub;

    public GrpcClientConfig(
            @Value("${billing.service.address:localhost}") String serverAddress1,
            @Value("${billing.service.grpc.port:9000}") int serverPort1,
            @Value("${billing.service.address:localhost}") String serverAddress2,
            @Value("${billing.service.grpc.port:9001}") int serverPort2) {

        logger.info("Connecting to Patient Service GRPC service at {}:{}",
                serverAddress1, serverPort1);

        logger.info("Connecting to Doctor Service GRPC service at {}:{}",
                serverAddress2, serverPort2);

        ManagedChannel channel1 = ManagedChannelBuilder.forAddress(serverAddress1,
                serverPort1).usePlaintext().build();

        patientServiceBlockingStub = PatientServiceGrpc.newBlockingStub(channel1);

        ManagedChannel channel2 = ManagedChannelBuilder.forAddress(serverAddress2,serverPort2)
                .usePlaintext().build();

        doctorServiceBlockingStub = DoctorServiceGrpc.newBlockingStub(channel2);

    }

    public PatientDetails getPatientDetails(long patientId) {

        PatientDetailsRequest request = PatientDetailsRequest.newBuilder()
                .setPatientId(patientId).build();

        PatientDetails response = patientServiceBlockingStub.getPatientDetails(request);

        logger.info("Received response from Patient service via GRPC: {}", response);
        return response;
    }

    public DoctorDetailsResponse getDoctorDetails(long doctorId) {

        DoctorDetailsRequest request = DoctorDetailsRequest.newBuilder()
                .setDoctorId(doctorId).build();

        DoctorDetailsResponse response = doctorServiceBlockingStub.getDoctorDetails(request);

        logger.info("Received response from Doctor service via GRPC: {}", response);
        return response;
    }
}
