package csv_in_employees.spring_employees_csv.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import csv_in_employees.spring_employees_csv.model.Employee;
import csv_in_employees.spring_employees_csv.repository.EmployeeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Employees", description = "CRUD APIs + Report Export")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository repo;
    private final JobLauncher jobLauncher;
    private final Job employeeExportJob;

    public EmployeeController(EmployeeRepository repo, JobLauncher jobLauncher, Job employeeExportJob) {
        this.repo = repo;
        this.jobLauncher = jobLauncher;
        this.employeeExportJob = employeeExportJob;
    }

    // ✅ CRUD
    @Operation(summary = "List employees")
    @GetMapping
    public List<Employee> all() {
        return repo.findAll();
    }

    @Operation(summary = "Get employee by id")
    @GetMapping("/{id}")
    public Employee one(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    @Operation(summary = "Create employee")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Employee create(@Valid @RequestBody Employee e) {
        return repo.save(e);
    }

    @Operation(summary = "Update employee")
    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @Valid @RequestBody Employee e) {
        Employee existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        existing.setName(e.getName());
        existing.setEmail(e.getEmail());
        existing.setDepartment(e.getDepartment());
        return repo.save(existing);
    }

    @Operation(summary = "Delete employee")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    // ✅ Trigger CSV Export
    @Operation(summary = "Export employees to CSV")
    @PostMapping("/export")
    public String export(@RequestParam(defaultValue = "./employees.csv") String outputFile) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("outputFile", outputFile)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(employeeExportJob, params);
        return "Export started! Check file: " + outputFile;
    }

    // ✅ Download CSV
    @Operation(summary = "Download employees CSV file")
    @GetMapping("/download")
    public ResponseEntity<Resource> download(
            @RequestParam(defaultValue = "./employees.csv") String filePath) throws IOException {
        FileSystemResource file = new FileSystemResource(filePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"employees.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }
}
