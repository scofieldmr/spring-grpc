package com.healthcare.service;

import appointment.*;
import com.healthcare.entity.Appointment;
import com.healthcare.grpc.GrpcClientConfig;
import com.healthcare.repository.AppointmentRepository;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Slf4j
@GrpcService
public class AppointmentGrpcService extends AppointmentServiceGrpc.AppointmentServiceImplBase {

    private final AppointmentRepository appointmentRepository;

    private final GrpcClientConfig clientConfig;

    public AppointmentGrpcService(AppointmentRepository appointmentRepository, GrpcClientConfig clientConfig) {
        this.appointmentRepository = appointmentRepository;
        this.clientConfig = clientConfig;
    }

    @Override
    public void bookAppointment(BookAppointmentRequest request, StreamObserver<BookAppointmentResponse> responseObserver) {
        try{

            var doctorResponse = clientConfig.getDoctorDetails(request.getDoctorId());

            log.info(doctorResponse.toString());
            var patientResponse = clientConfig.getPatientDetails(request.getPatientId());

            var appointment = new Appointment(
                    null,
                    request.getPatientId(),
                    patientResponse.getFirstName() + " " + patientResponse.getLastName(),
                    request.getDoctorId(),
                    doctorResponse.getFirstName() + " " + doctorResponse.getLastName(),
                    doctorResponse.getLocation(),
                    LocalDate.parse(request.getAppointmentDate()),
                    LocalTime.parse(request.getAppointmentTime()),
                    request.getReason()
            );
            appointment = appointmentRepository.save(appointment);

            BookAppointmentResponse response = BookAppointmentResponse.newBuilder()
                    .setAppointmentId(appointment.getId()).setMessage("Appointment Booked Successfully").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (io.grpc.StatusRuntimeException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAppointmentAvailability(AppointmentAvailabilityRequest request, StreamObserver<AppointmentAvailabilityResponse> responseObserver) {
        List<LocalDateTime> hardcodedAppointments = Arrays.asList(
                LocalDateTime.of(2025, 1, 7, 9, 0),
                LocalDateTime.of(2025, 1, 8, 9, 30),
                LocalDateTime.of(2025, 1, 8, 10, 0),
                LocalDateTime.of(2025, 1, 8, 10, 30),
                LocalDateTime.of(2025, 1, 9, 11, 0),
                LocalDateTime.of(2025, 1, 11, 11, 30),
                LocalDateTime.of(2025, 1, 11, 13, 0),
                LocalDateTime.of(2025, 1, 12, 13, 30),
                LocalDateTime.of(2025, 1, 12, 14, 0),
                LocalDateTime.of(2025, 1, 13, 14, 30)
        );
        Random random = new Random();
        int i=0;
        while(i<10){
            Collections.shuffle(hardcodedAppointments,random);
            var slots =   hardcodedAppointments.stream()
                    .limit(2)
                    .map(dateTime -> AppointmentSlot.newBuilder()
                            .setAppointmentDate(dateTime.toLocalDate().toString())
                            .setAppointmentTime(dateTime.toLocalTime().toString())
                            .build())
                    .collect(Collectors.toList());
            var response = AppointmentAvailabilityResponse.newBuilder()
                    .addAllResponses(slots)
                    .setAvailabilityAsOf(LocalDateTime.now().toString())
                    .build();
            responseObserver.onNext(response);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        responseObserver.onCompleted();

    }
}
