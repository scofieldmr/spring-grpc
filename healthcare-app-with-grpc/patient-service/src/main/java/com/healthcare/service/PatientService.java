package com.healthcare.service;

import com.healthcare.entity.Patient;
import com.healthcare.repository.PatientRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import patient.*;

@GrpcService
public class PatientService extends PatientServiceGrpc.PatientServiceImplBase {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void registerNewPatient(PatientRegistrationRequest request, StreamObserver<PatientRegistrationResponse> responseObserver) {

        Patient patient = patientRepository.findByEmail(request.getEmail());

        if (patient == null) {
            patient = new Patient();

            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setEmail(request.getEmail());
            patient.setPhone(request.getPhone());
            patient.setAddress(request.getAddress());

            Patient savedPatient = patientRepository.save(patient);

            PatientRegistrationResponse response = PatientRegistrationResponse.newBuilder()
                    .setPatientId(savedPatient.getId())
                    .setMessage("Patient Registration successful with the email " + request.getEmail())
                    .build();
            responseObserver.onNext(response);

            responseObserver.onCompleted();
        }
        else{
            responseObserver.onError(Status.ALREADY_EXISTS.withDescription("Patient with the email "+ request.getEmail() +
                    "already exists").asRuntimeException());
        }
    }

    @Override
    public void getPatientDetails(PatientDetailsRequest request, StreamObserver<PatientDetails> responseObserver) {
        var patient = patientRepository.findById(request.getPatientId());
        if(patient.isPresent()) {
           PatientDetails details = PatientDetails.newBuilder()
                   .setPatientId(patient.get().getId())
                   .setFirstName(patient.get().getFirstName())
                   .setLastName(patient.get().getLastName())
                   .setEmail(patient.get().getEmail())
                   .setPhone(patient.get().getPhone())
                   .setAddress(patient.get().getAddress())
                   .build();

           responseObserver.onNext(details);
            responseObserver.onCompleted();
        }
        else {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Patient not found").asRuntimeException());
        }

    }

    @Override
    public StreamObserver<PatientRegistrationRequest> streamPatients(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<PatientRegistrationRequest>() {

            @Override
            public void onNext(PatientRegistrationRequest patientRegistrationRequest) {
                Patient patient = patientRepository.findByEmail(patientRegistrationRequest.getEmail());

                if (patient == null) {
                    patient = new Patient();
                    patient.setFirstName(patientRegistrationRequest.getFirstName());
                    patient.setLastName(patientRegistrationRequest.getLastName());
                    patient.setEmail(patientRegistrationRequest.getEmail());
                    patient.setPhone(patientRegistrationRequest.getPhone());
                    patient.setAddress(patientRegistrationRequest.getAddress());
                    Patient savedPatient = patientRepository.save(patient);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                  responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted(){
                responseObserver.onNext(Empty.newBuilder().build());
                responseObserver.onCompleted();
            }
        };
    }
}
