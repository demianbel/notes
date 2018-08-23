package by.demianbel.notes.service;

import by.demianbel.notes.converter.node.HierarchicalNodeToNodeConverter;
import by.demianbel.notes.converter.node.NodeToSaveNodeConverter;
import by.demianbel.notes.converter.node.PersistedNodeToNodeConverter;
import by.demianbel.notes.converter.note.HierarchicalNoteToNoteConverter;
import by.demianbel.notes.converter.note.PersistedNoteToNoteConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.node.NodeToSaveDTO;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.NoteRepository;
import by.demianbel.notes.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PersistedNodeToNodeConverter persistedNodeToNodeConverter;
    private final PersistedNoteToNoteConverter persistedNoteToNoteConverter;
    private final NodeToSaveNodeConverter nodeToSaveNodeConverter;
    private final HierarchicalNodeToNodeConverter hierarchicalNodeToNodeConverter;
    private final HierarchicalNoteToNoteConverter hierarchicalNoteToNoteConverter;

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

    public HierarchicalNodeDTO getHierarchicalNode(final Long nodeId) {
        final UserEntity currentUser = userService.getCurrentUser();
        return nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .map(hierarchicalNodeToNodeConverter::convertToDto)
                .orElseThrow(() -> new RuntimeException("Node with id = '" + nodeId + "' doesn't exist."));
    }

    @Transactional
    public List<HierarchicalNodeDTO> getSharedNodeHierarchical() {

        final Map<String, HierarchicalNodeDTO> sharedNotesByUserName =
                noteRepository.findAllByUsersToShareContainsAndNodeIsNullAndActiveIsTrue(userService.getCurrentUser())
                        .stream().collect(
                        Collectors.toMap(note -> note.getUser().getName(), note -> {
                            final HierarchicalNodeDTO userNode = new HierarchicalNodeDTO();
                            userNode.setName(note.getUser().getName());
                            userNode.getNotes().add(hierarchicalNoteToNoteConverter.convertToDto(note));
                            return userNode;
                        }, (a, b) -> {
                            a.getNotes().addAll(b.getNotes());
                            return a;
                        }, HashMap::new));

        final Map<String, List<NoteEntity>> notesWithNodesByUserNames = noteRepository
                .findAllByUsersToShareContainsAndNodeIsNotNullAndActiveIsTrue(userService.getCurrentUser())
                .stream().filter(note -> {
                    NodeEntity node = note.getNode();
                    return node != null && node.isActive();
                }).collect(Collectors.groupingBy(note -> note.getUser().getName()));

        notesWithNodesByUserNames.forEach((key, value) -> {
            final HierarchicalNodeDTO userHierarchicalNode =
                    sharedNotesByUserName.computeIfAbsent(key, HierarchicalNodeDTO::new);
            Map<Long, List<NoteEntity>> notesByNodeId =
                    value.stream().collect(Collectors.groupingBy(note -> note.getNode().getId()));
            notesByNodeId.forEach((k, v) -> {
                HierarchicalNodeDTO sharedNode = new HierarchicalNodeDTO(v.get(0).getNode().getName());
                sharedNode.setNotes(v.stream().map(hierarchicalNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList()));
                userHierarchicalNode.getChildren().add(sharedNode);
            });
        });

        return new ArrayList<>(sharedNotesByUserName.values());
    }


    public List<PersistedNodeDTO> findNodeByName(final String name) {
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

    @Transactional
    public List<PersistedNoteDTO> shareNodeWithUser(final Long userId, final Long nodeId) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity nodeToShare = nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .orElseThrow(() -> new RuntimeException("Node with id = '" + nodeId + "' doesn't exist."));
        final UserEntity userToShare = userRepository.findByIdAndActiveIsTrue(userId)
                .orElseThrow(() -> new RuntimeException("User with id = '" + userId + "' doesn't exist."));

        final Set<NoteEntity> notesToShare = getNotesFromNode(nodeToShare);
        notesToShare.forEach(noteEntity -> noteEntity.getUsersToShare().add(userToShare));
        noteRepository.saveAll(notesToShare);

        return notesToShare.stream().map(persistedNoteToNoteConverter::convertToDto).collect(Collectors.toList());

    }

    @Transactional
    public List<PersistedNoteDTO> unshareNodeWithUser(final Long userId, final Long nodeId) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity nodeToShare = nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .orElseThrow(() -> new RuntimeException("Node with id = '" + nodeId + "' doesn't exist."));

        final Set<NoteEntity> notesToShare = getNotesFromNode(nodeToShare);
        notesToShare.forEach(noteEntity -> {
            final Set<UserEntity> usersToShare = noteEntity.getUsersToShare();
            usersToShare.stream().filter(userEntity -> userEntity.getId() == userId).findAny()
                    .ifPresent(userToUnshare -> usersToShare.remove(userToUnshare));
        });
        noteRepository.saveAll(notesToShare);

        return notesToShare.stream().map(persistedNoteToNoteConverter::convertToDto).collect(Collectors.toList());

    }

    private Set<NoteEntity> getNotesFromNode(NodeEntity nodeEntity) {

        final HashSet<NoteEntity> collectedNotes =
                new HashSet<NoteEntity>(Optional.ofNullable(nodeEntity.getNotes()).orElse(
                        Collections.emptySet()));

        Optional.ofNullable(nodeEntity.getChildren()).orElse(Collections.emptySet()).stream()
                .flatMap(node -> getNotesFromNode(node).stream()).forEach(
                collectedNotes::add);

        return collectedNotes;
    }
}
