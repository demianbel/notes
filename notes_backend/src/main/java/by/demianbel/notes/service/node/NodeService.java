package by.demianbel.notes.service.node;

import by.demianbel.notes.converter.node.NodeToSaveNodeConverter;
import by.demianbel.notes.converter.node.PersistedNodeToNodeConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.node.NodeToSaveDTO;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.exception.NodeNotFoundException;
import by.demianbel.notes.exception.UserNotFoundException;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Transactional
    public PersistedNodeDTO deactivateNode(final Long id) {
        return doWithActiveNode(id, nodeEntity -> {
            nodeEntity.setActive(false);
            nodeEntity.getNotes().forEach(note -> note.setActive(false));
            nodeEntity.getChildren().forEach(node -> {
                node.setActive(false);
                node.getNotes().forEach(note -> note.setActive(false));
            });
            return nodeRepository.save(nodeEntity);
        });
    }

    public PersistedNodeDTO changeNodeName(final Long id, final String name)  {
        return doWithActiveNode(id, nodeEntity -> {
            nodeEntity.setName(name);
            return nodeRepository.save(nodeEntity);
        });
    }

    private PersistedNodeDTO doWithActiveNode(final Long id, final Function<NodeEntity, NodeEntity> function)  {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity tagToProcess =
                nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, id)
                        .orElseThrow(() -> new NodeNotFoundException("Node with id = '" + id + "' doesn't exist."));
        final NodeEntity resultTag = function.apply(tagToProcess);
        return persistedNodeToNodeConverter.convertToDto(resultTag);

    }

    public List<PersistedNodeDTO> findNodeByName(final String name) throws UserNotFoundException {
        final List<PersistedNodeDTO> resultNodes = new ArrayList<>();
        final UserEntity currentUser = userService.getCurrentUser();
        final Optional<NodeEntity> equalNode = nodeRepository.findFirstByUserAndActiveIsTrueAndName(currentUser, name);
        final List<NodeEntity> similarNodes =
                nodeRepository.findAllByUserAndActiveIsTrueAndNameLike(currentUser, "%" + name + "%");

        equalNode.map(persistedNodeToNodeConverter::convertToDto).ifPresent(resultNodes::add);
        final Long filterTagId;
        if (!resultNodes.isEmpty()) {
            filterTagId = resultNodes.get(0).getId();
        } else {
            filterTagId = null;
        }
        similarNodes.stream().filter(tag -> !Objects.equals(tag.getId(), filterTagId))
                .map(persistedNodeToNodeConverter::convertToDto).forEach(resultNodes::add);

        return resultNodes;
    }

}
