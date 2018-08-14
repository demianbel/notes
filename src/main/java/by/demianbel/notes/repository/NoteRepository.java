package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    List<NoteEntity> findByNameLike(String lastName);

    Optional<NoteEntity> findByUserAndActiveAndId(UserEntity user, boolean active, Long id);
}
