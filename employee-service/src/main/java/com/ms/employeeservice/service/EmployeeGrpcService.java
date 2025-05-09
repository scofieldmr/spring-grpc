package com.ms.employeeservice.service;

import com.ms.employeeservice.entity.Employee;
import com.ms.employeeservice.repository.EmployeeRepository;
import employee.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class EmployeeGrpcService extends EmployeeServiceGrpc.EmployeeServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeGrpcService.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeGrpcService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        logger.info("Received request: " + request);
        HelloResponse response = HelloResponse.newBuilder()
                .setMessage("Hello " + request.getName() + "Welcome to the GRPC").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        logger.info("Completed response: " + response);
    }

    @Override
    public void createEmployee(EmployeeRequest request, StreamObserver<EmployeeResponse> responseObserver) {

        logger.info("Received Employee request: " + request);

        Employee newEmployee = new Employee();
        newEmployee.setName(request.getName());
        newEmployee.setEmail(request.getEmail());
        newEmployee.setDept(request.getDept());
        newEmployee.setAge(request.getAge());
        newEmployee.setSalary(request.getSalary());

        Employee savedEmployee = employeeRepository.save(newEmployee);

        EmployeeResponse response = EmployeeResponse.newBuilder()
                .setName(savedEmployee.getName())
                .setDept(savedEmployee.getDept())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("Created Employee details : " + response);
    }

    @Override
    public void getEmployeeById(EmployeeIdRequest request, StreamObserver<EmployeeFullResponse> responseObserver) {

        logger.info("Received request Id: " + request);

        Employee getEmployee = employeeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getId()));

        EmployeeFullResponse response = EmployeeFullResponse.newBuilder()
                .setId(getEmployee.getId())
                .setName(getEmployee.getName())
                .setDept(getEmployee.getDept())
                .setAge(getEmployee.getAge())
                .setEmail(getEmployee.getEmail())
                .setSalary(getEmployee.getSalary())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("Completed response for the requested Id: " + response);
    }

    @Override
    public void getAllEmployeeDetails(Empty request, StreamObserver<EmployeeList> responseObserver) {

        logger.info("Received request Id: " + request);

        List<Employee> employees = employeeRepository.findAll();

        if (employees.size() == 0) {
            responseObserver.onNext(EmployeeList.newBuilder().build());
            responseObserver.onCompleted();
        }

        List<EmployeeFullResponse> employeeFullResponses = new ArrayList<>();

        for (Employee emp : employees) {
            EmployeeFullResponse employeeFullResponse = EmployeeFullResponse.newBuilder()
                    .setId(emp.getId())
                    .setName(emp.getName())
                    .setEmail(emp.getEmail())
                    .setDept(emp.getDept())
                    .setAge(emp.getAge())
                    .setSalary(emp.getSalary())
                    .build();

            employeeFullResponses.add(employeeFullResponse);
        }

        EmployeeList list = EmployeeList.newBuilder()
                        .addAllEmployees(employeeFullResponses).build();

        responseObserver.onNext(list);
        responseObserver.onCompleted();

    }

    @Override
    public void updateEmployeeDetails(EmployeeUpdateRequest request, StreamObserver<EmployeeFullResponse> responseObserver) {
        logger.info("Received request to update Employee Id: " + request);

        Employee employee = employeeRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getId()));

        employee.setDept(request.getDept());
        employee.setAge(request.getAge());
        employee.setSalary(request.getSalary());

        Employee updatedEmployee = employeeRepository.save(employee);

        EmployeeFullResponse employeeFullResponse = EmployeeFullResponse.newBuilder()
                .setId(updatedEmployee.getId()).setName(updatedEmployee.getName())
                .setEmail(updatedEmployee.getEmail()).setDept(updatedEmployee.getDept())
                .setAge(updatedEmployee.getAge()).setSalary(updatedEmployee.getSalary()).build();

        responseObserver.onNext(employeeFullResponse);
        responseObserver.onCompleted();

        logger.info("Updated Employee response: " + employeeFullResponse);
    }

    @Override
    public void deleteEmployeeDetails(EmployeeIdRequest request, StreamObserver<EmployeeDeleteResponse> responseObserver) {

        logger.info("Received request to delete the Employee Id: " + request);

        employeeRepository.deleteById(request.getId());

        EmployeeDeleteResponse employeeDeleteResponse = EmployeeDeleteResponse.newBuilder()
                        .setMessage("Employee deleted successfully with the ID : "+ request.getId()).build();

        responseObserver.onNext(employeeDeleteResponse);
        responseObserver.onCompleted();
        logger.info("Deleted the Employee Details for the requested Id: " + request);
    }
}
