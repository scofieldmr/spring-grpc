package come.codewiz.repository;

import com.codewiz.model.Patient;
import org.springframework.data.repository.CrudRepository;

public interface PatientRepository extends CrudRepository<Patient, Long> {
}
