package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
}
