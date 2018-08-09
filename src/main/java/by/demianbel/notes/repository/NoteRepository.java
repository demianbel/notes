package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    List<NoteEntity> findByNameLike(String lastName);
}
