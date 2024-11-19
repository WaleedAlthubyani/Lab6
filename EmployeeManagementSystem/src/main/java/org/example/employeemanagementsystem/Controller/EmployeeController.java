package org.example.employeemanagementsystem.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.example.employeemanagementsystem.ApiResponse.ApiResponse;
import org.example.employeemanagementsystem.Model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/employee-management-system")
public class EmployeeController {
    ArrayList<Employee> employees = new ArrayList<>();

    @GetMapping("/get")
    public ResponseEntity getEmployees(){
        return ResponseEntity.status(200).body(employees);
    }

    @PostMapping("/add")
    public ResponseEntity addEmployee(@RequestBody @Valid Employee employee, Errors error){
        if (error.hasErrors()){
            return ResponseEntity.status(400).body(Objects.requireNonNull(error.getFieldError()).getDefaultMessage());
        }

        employees.add(employee);
        return ResponseEntity.status(200).body(new ApiResponse("Employee added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateEmployee(@PathVariable String id,@RequestBody @Valid Employee employee,Errors error){
        if (error.hasErrors()){
            return ResponseEntity.status(400).body(Objects.requireNonNull(error.getFieldError()).getDefaultMessage());
        }

        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId().equals(id)){
                employees.set(i,employee);
                return ResponseEntity.status(200).body(new ApiResponse("Employee updated successfully"));
            }
        }
        return ResponseEntity.status(404).body(new ApiResponse("There is no employee matching the id provided"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteEmployee(@PathVariable String id){
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId().equals(id)){
                employees.remove(i);
                return ResponseEntity.status(200).body(new ApiResponse("Employee deleted successfully"));
            }
        }
        return ResponseEntity.status(404).body(new ApiResponse("Employee not found"));
    }

    @GetMapping("search-by-position/{position}")
    public ResponseEntity searchByPosition(@PathVariable @Pattern(regexp = "(supervisor|coordinator)") String position){
        ArrayList<Employee> samePosition = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.getPosition().equals(position)) {
                samePosition.add(employee);
            }
        }

        return ResponseEntity.status(200).body(samePosition);
    }

    @GetMapping("employees-by-age-range/{minAge}/{maxAge}")
    public ResponseEntity getEmployeesByAgeRange(@PathVariable int minAge,@PathVariable int maxAge){
        ArrayList<Employee> ageRangeList = new ArrayList<>();

        for (Employee employee: employees){
            if (employee.getAge()>=minAge && employee.getAge()<=maxAge){
                ageRangeList.add(employee);
            }
        }

        return ResponseEntity.status(200).body(ageRangeList);
    }

    @PutMapping("/apply-for-leave/{id}")
    public ResponseEntity applyForAnnualLeave(@PathVariable String id){
        for (Employee employee : employees) {
            if (employee.getId().equals(id)) {
                if (!employee.isOnLeave()) {
                    if (employee.getAnnualLeave() > 0) {
                        employee.setAnnualLeave(employee.getAnnualLeave() - 1);
                        employee.setOnLeave(true);
                        return ResponseEntity.status(200).body(new ApiResponse("Employee " + employee.getName() + " is now on leave"));
                    }
                        return ResponseEntity.status(400).body(new ApiResponse("Employee " + employee.getName() + " doesn't have enough annual leaves"));
                }
                return ResponseEntity.status(400).body(new ApiResponse("Employee " + employee.getName() + " is already on leave"));
            }
        }
        return ResponseEntity.status(404).body(new ApiResponse("Employee not found"));
    }

    @GetMapping("/get-employees-with-no-annual-leaves")
    public ResponseEntity getEmployeesWithNoAnnualLeaves(){
        ArrayList<Employee> noAnnualLeaves = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.getAnnualLeave() == 0)
                noAnnualLeaves.add(employee);
        }

        return ResponseEntity.status(200).body(noAnnualLeaves);
    }

    @PutMapping("/promote/{supervisorID}/{employeeID}")
    public ResponseEntity promoteEmployee(@PathVariable String supervisorID, @PathVariable String employeeID){
        boolean found=false;
        for (Employee employee : employees) {
            if (employee.getId().equals(supervisorID)) {
                found=true;
                if (!employee.getPosition().equals("supervisor")) {
                    return ResponseEntity.status(403).body(new ApiResponse("You are not authorized to do this action"));
                } else
                    break;
            }
        }

        if (!found){
            return ResponseEntity.status(403).body(new ApiResponse("You are not authorized to do this action"));
        }

        for (Employee employee: employees){
            if (employee.getId().equals(employeeID)){
                if (employee.getPosition().equals("supervisor")){
                    return ResponseEntity.status(400).body(new ApiResponse("Employee is already a supervisor"));}
                if (employee.getAge()>=30){
                    if (!employee.isOnLeave()){
                        employee.setPosition("supervisor");
                        return ResponseEntity.status(200).body(new ApiResponse("Employee "+employee.getName()+" promoted successfully"));
                    }else {
                        return ResponseEntity.status(400).body(new ApiResponse("Employee can't be promoted while on leave"));
                    }
                }else {
                    return ResponseEntity.status(400).body(new ApiResponse("The employee is too young to be a supervisor"));
                }
            }
        }

        return ResponseEntity.status(404).body(new ApiResponse("Employee not found"));
    }
}
