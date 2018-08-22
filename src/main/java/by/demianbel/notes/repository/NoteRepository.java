package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    List<NoteEntity> findByNameLike(String lastName);

    List<NoteEntity> findByUserAndActiveTrueAndNodeIsNull(UserEntity userEntity);

    List<NoteEntity> findByUserAndActive(UserEntity user, boolean active);

    Optional<NoteEntity> findByUserAndActiveAndId(UserEntity user, boolean active, Long id);

    Optional<NoteEntity> findFirstByUserAndActiveIsTrueAndName(UserEntity currentUser, String name);

    List<NoteEntity> findAllByUserAndActiveIsTrueAndNameLike(UserEntity currentUser, String name);

    List<NoteEntity> findAllByUserAndActiveIsTrueAndTextLike(UserEntity currentUser, String text);

    Optional<NoteEntity> findFirstByUserAndActiveIsTrueAndText(UserEntity currentUser, String text);
}
