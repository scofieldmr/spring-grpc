package com.healthcare.service;

import com.healthcare.entity.Doctor;
import com.healthcare.repository.DoctorRepository;
import doctor.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;

@GrpcService
public class DoctorGrpcService extends DoctorServiceGrpc.DoctorServiceImplBase {

    private final DoctorRepository doctorRepository;

    public DoctorGrpcService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }


    @Override
    public void registerDoctor(DoctorRegistrationRequest request, StreamObserver<DoctorRegistrationResponse> responseObserver) {

        Doctor doctor = doctorRepository.findByEmail(request.getEmail());
        if (doctor == null) {
            doctor = new Doctor();

            doctor.setFirstName(request.getFirstName());
            doctor.setLastName(request.getLastName());
            doctor.setEmail(request.getEmail());
            doctor.setPhone(request.getPhone());
            doctor.setSpecialty(request.getSpecialty());
            doctor.setCentreName(request.getCentreName());
            doctor.setLocation(request.getLocation());

            Doctor savedDoctor = doctorRepository.save(doctor);

            DoctorRegistrationResponse response = DoctorRegistrationResponse.newBuilder()
                    .setDoctorId(savedDoctor.getId())
                    .setMessage("Doctor Details registered successfully...")
                    .build();
            responseObserver.onNext(response);
        }
        else{
            responseObserver.onError(Status.ALREADY_EXISTS.withDescription("Doctor with the email "+ request.getEmail() +
                    "already exists").asRuntimeException());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getDoctorDetails(DoctorDetailsRequest request, StreamObserver<DoctorDetailsResponse> responseObserver) {

        Optional<Doctor> doctor = doctorRepository.findById(request.getDoctorId());

        if(doctor.isPresent()){

            var getDoctorDetails = doctor.get();

            DoctorDetailsResponse response = DoctorDetailsResponse.newBuilder()
                    .setDoctorId(getDoctorDetails.getId())
                    .setFirstName(getDoctorDetails.getFirstName())
                    .setLastName(getDoctorDetails.getLastName())
                    .setEmail(getDoctorDetails.getEmail())
                    .setPhone(getDoctorDetails.getPhone())
                    .setSpecialty(getDoctorDetails.getSpecialty())
                    .setCentreName(getDoctorDetails.getCentreName())
                    .setLocation(getDoctorDetails.getLocation())
                    .build();
            responseObserver.onNext(response);
        }
        else{
            responseObserver.onError(Status.NOT_FOUND.withDescription("Doctor details not found with the ID :"
                + request.getDoctorId()).asRuntimeException());
        }

        responseObserver.onCompleted();

    }

}
