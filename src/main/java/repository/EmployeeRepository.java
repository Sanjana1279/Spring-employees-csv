package csv_in_employees.spring_employees_csv.repository;


import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<csv_in_employees.spring_employees_csv.model.Employee, Long> {}
