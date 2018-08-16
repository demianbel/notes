package by.demianbel.notes.service;

import by.demianbel.notes.converter.node.NodeToSaveNodeConverter;
import by.demianbel.notes.converter.node.PersistedNodeToNodeConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.node.NodeToSaveDTO;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.repository.NodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;
    private final UserService userService;
    private final PersistedNodeToNodeConverter persistedNodeToNodeConverter;
    private final NodeToSaveNodeConverter nodeToSaveNodeConverter;

    public PersistedNodeDTO findNodeById(final Long id) {
        return doWithActiveNode(id, tagEntity -> tagEntity);
    }

    public PersistedNodeDTO createNode(final NodeToSaveDTO nodeToSaveDTO) {
        final NodeEntity nodeToSave = nodeToSaveNodeConverter.convertToDbo(nodeToSaveDTO);
        final NodeEntity savedNote = nodeRepository.save(nodeToSave);
        return persistedNodeToNodeConverter.convertToDto(savedNote);
    }

    public PersistedNodeDTO deactivateNode(final Long id) {
        return doWithActiveNode(id, nodeEntity -> {
            nodeEntity.setActive(false);
            return nodeRepository.save(nodeEntity);
        });
    }

    public PersistedNodeDTO changeNodeName(final Long id, final String name) {
        return doWithActiveNode(id, nodeEntity -> {
            nodeEntity.setName(name);
            return nodeRepository.save(nodeEntity);
        });
    }

    private PersistedNodeDTO doWithActiveNode(final Long id, final Function<NodeEntity, NodeEntity> function) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity tagToProcess =
                nodeRepository.findByUserAndIdAndActive(currentUser, id, true)
                        .orElseThrow(() -> new RuntimeException("Node with id = '" + id + "' doesn't exist."));
        final NodeEntity resultTag = function.apply(tagToProcess);
        return persistedNodeToNodeConverter.convertToDto(resultTag);

    }
}
