package com.codewiz.config;

import com.codewiz.doctor.DoctorServiceGrpc;
import com.codewiz.patient.PatientServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcStub {

    @Bean
    DoctorServiceGrpc.DoctorServiceBlockingStub doctorServiceBlockingStub(GrpcChannelFactory channels) {
        return DoctorServiceGrpc.newBlockingStub(channels.createChannel("doctorService"));
    }

    @Bean
    PatientServiceGrpc.PatientServiceBlockingStub patientServiceBlockingStub(GrpcChannelFactory channels) {
        return PatientServiceGrpc.newBlockingStub(channels.createChannel("patientService"));
    }
}
