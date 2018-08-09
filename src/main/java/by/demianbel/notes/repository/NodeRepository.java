package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<NodeEntity, Long> {
}
