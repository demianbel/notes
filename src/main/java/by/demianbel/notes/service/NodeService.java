package by.demianbel.notes.service;

import by.demianbel.notes.converter.node.HierarchicalNodeToNodeConverter;
import by.demianbel.notes.converter.node.NodeToSaveNodeConverter;
import by.demianbel.notes.converter.node.PersistedNodeToNodeConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.node.NodeToSaveDTO;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.repository.NodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;
    private final UserService userService;
    private final PersistedNodeToNodeConverter persistedNodeToNodeConverter;
    private final NodeToSaveNodeConverter nodeToSaveNodeConverter;
    private final HierarchicalNodeToNodeConverter hierarchicalNodeToNodeConverter;

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

    public PersistedNodeDTO changeNodeName(final Long id, final String name) {
        return doWithActiveNode(id, nodeEntity -> {
            nodeEntity.setName(name);
            return nodeRepository.save(nodeEntity);
        });
    }

    private PersistedNodeDTO doWithActiveNode(final Long id, final Function<NodeEntity, NodeEntity> function) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity tagToProcess =
                nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, id)
                        .orElseThrow(() -> new RuntimeException("Node with id = '" + id + "' doesn't exist."));
        final NodeEntity resultTag = function.apply(tagToProcess);
        return persistedNodeToNodeConverter.convertToDto(resultTag);

    }

    @Transactional
    public List<HierarchicalNodeDTO> getAllNodesHierarhical() {
        final UserEntity currentUser = userService.getCurrentUser();
        List<NodeEntity> rootNodes =
                nodeRepository.findByUserAndActiveIsTrueAndParentNodeIsNull(currentUser);
        return rootNodes.stream().map(hierarchicalNodeToNodeConverter::convertToDto).collect(Collectors.toList());
    }

    public List<PersistedNodeDTO> findTagByName(final String name) {
        final List<PersistedNodeDTO> resultTags = new ArrayList<>();
        final UserEntity currentUser = userService.getCurrentUser();
        final Optional<NodeEntity> equalNode = nodeRepository.findFirstByUserAndActiveIsTrueAndName(currentUser, name);
        final List<NodeEntity> similarNodes =
                nodeRepository.findAllByUserAndActiveIsTrueAndNameLike(currentUser, "%" + name + "%");

        equalNode.map(persistedNodeToNodeConverter::convertToDto).ifPresent(resultTags::add);
        final Long filterTagId;
        if (!resultTags.isEmpty()) {
            filterTagId = resultTags.get(0).getId();
        } else {
            filterTagId = null;
        }
        similarNodes.stream().filter(tag -> !Objects.equals(tag.getId(), filterTagId))
                .map(persistedNodeToNodeConverter::convertToDto).forEach(resultTags::add);

        return resultTags;
    }

    public HierarchicalNodeDTO getHierarchicalNode(final Long nodeId) {
        final UserEntity currentUser = userService.getCurrentUser();
        return nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .map(hierarchicalNodeToNodeConverter::convertToDto)
                .orElseThrow(() -> new RuntimeException("Node with id = '" + nodeId + "' doesn't exist."));
    }
}
