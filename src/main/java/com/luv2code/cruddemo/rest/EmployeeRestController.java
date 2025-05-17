package com.luv2code.cruddemo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.luv2code.cruddemo.entity.Employee;
import com.luv2code.cruddemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class EmployeeRestController {

    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    @Autowired
    public EmployeeRestController(EmployeeService employeeService, ObjectMapper objectMapper){
        this.employeeService = employeeService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/employees")
    public List<Employee> findAll(){
        return employeeService.findAll();
    }

    @GetMapping("/employees/{id}")
    public Employee findById(@PathVariable int id){
        Employee employee = employeeService.findById(id);
        if(employee == null){
            throw new RuntimeException("Employee ID not found: " + id);
        }
        return employee;
    }

    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee employee){
        if(employee.getId() != 0){
            employee.setId(0);
        }
        return employeeService.save(employee);
    }

    @PutMapping("/employees")
    public Employee updateEmployee(@RequestBody Employee employee){
        return employeeService.save(employee);
    }

    @DeleteMapping("employees/{id}")
    public String deleteEmployee(@PathVariable int id){
        Employee employee = employeeService.findById(id);
        if(employee == null){
            throw new RuntimeException("Not found Employee with id " + id);
        }
        employeeService.deleteById(id);
        return "deleted id: " + id;
    }

    @PatchMapping("/employees/{id}")
    public Employee updatePartEmployee(@RequestBody Map<String, Object> patchPayload, @PathVariable("id") int id){
        Employee employee = employeeService.findById(id);
        if(employee == null){
            throw new RuntimeException("Employee ID not found: " + id);
        }

        if(patchPayload.containsKey("id")){
            throw new RuntimeException("must not included id field");
        }

        Employee patchedEmployee = applyPatch(employee, patchPayload);
        return employeeService.save(patchedEmployee);
    }

    public Employee applyPatch(Employee originEmployee, Map<String, Object> patchPayload){
        ObjectNode employeeNode = objectMapper.convertValue(originEmployee, ObjectNode.class);
        ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);

        employeeNode.setAll(patchNode);
        return objectMapper.convertValue(employeeNode, Employee.class);
    }

}
