package csv_in_employees.spring_employees_csv.batch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Tag(name = "Reports")
@RestController
@RequestMapping("/api/reports")
public class EmployeeExportController {

    private final JobLauncher jobLauncher;
    private final Job exportEmployeesJob;

    public EmployeeExportController(JobLauncher jobLauncher, Job exportEmployeesJob) {
        this.jobLauncher = jobLauncher;
        this.exportEmployeesJob = exportEmployeesJob;
    }

    @Operation(summary = "Export all employees to CSV",
            description = "Generates a CSV file. Provide `outputFile` path or it will default to ./employees-YYYYMMdd-HHmmss.csv")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/employees/export")
    public String exportEmployees(@RequestParam(required = false) String outputFile) throws Exception {
        String defaultName = "employees-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".csv";
        String target = (outputFile == null || outputFile.isBlank()) ? "./" + defaultName : outputFile;

        JobParameters params = new JobParametersBuilder()
                .addString("outputFile", target)
                .addLong("ts", System.currentTimeMillis()) // uniqueness
                .toJobParameters();

        jobLauncher.run(exportEmployeesJob, params);
        return "Export started. Writing to: " + target;
    }
}
