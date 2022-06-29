package be.ucll.tweedehandsbackend.Repositories;

import be.ucll.tweedehandsbackend.Models.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    Program findProgramById(long id);

    Program findProgramByUuid(UUID programUuid);
}
