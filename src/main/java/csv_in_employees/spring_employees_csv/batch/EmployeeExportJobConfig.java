package csv_in_employees.spring_employees_csv.batch;

import csv_in_employees.spring_employees_csv.model.Employee;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmployeeExportJobConfig {

    @Bean
    public JpaPagingItemReader<csv_in_employees.spring_employees_csv.model.Employee> employeeReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<csv_in_employees.spring_employees_csv.model.Employee>()
                .name("employeeReader")
                .entityManagerFactory(emf)
                .queryString("SELECT e FROM Employee e ORDER BY e.id")
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<csv_in_employees.spring_employees_csv.model.Employee, csv_in_employees.spring_employees_csv.model.Employee> employeeProcessor() {
        return item -> item; // no processing, just pass through
    }

    @Bean
    public FlatFileItemWriter<csv_in_employees.spring_employees_csv.model.Employee> employeeWriter() {
        FieldExtractor<csv_in_employees.spring_employees_csv.model.Employee> fe = item -> new Object[]{
                item.getId(), item.getName(), item.getEmail(), item.getDepartment()
        };

        DelimitedLineAggregator<csv_in_employees.spring_employees_csv.model.Employee> agg = new DelimitedLineAggregator<>();
        agg.setDelimiter(",");
        agg.setFieldExtractor(fe);

        return new FlatFileItemWriterBuilder<csv_in_employees.spring_employees_csv.model.Employee>()
                .name("employeeWriter")
                .resource(new FileSystemResource("employees.csv"))
                .lineAggregator(agg)
                .headerCallback(w -> w.write("id,name,email,department"))
                .append(false)
                .build();
    }

    @Bean
    public Step exportEmployeesStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    JpaPagingItemReader<csv_in_employees.spring_employees_csv.model.Employee> reader,
                                    ItemProcessor<csv_in_employees.spring_employees_csv.model.Employee, csv_in_employees.spring_employees_csv.model.Employee> processor,
                                    FlatFileItemWriter<csv_in_employees.spring_employees_csv.model.Employee> writer) {

        return new StepBuilder("exportEmployeesStep", jobRepository)
                .<csv_in_employees.spring_employees_csv.model.Employee, csv_in_employees.spring_employees_csv.model.Employee>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job exportEmployeesJob(JobRepository jobRepository, Step exportEmployeesStep) {
        return new JobBuilder("exportEmployeesJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(exportEmployeesStep)
                .build();
    }
}
