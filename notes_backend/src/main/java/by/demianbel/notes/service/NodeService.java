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
import by.demianbel.notes.exception.NodeNotFoundException;
import by.demianbel.notes.exception.UserNotFoundException;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.NoteRepository;
import by.demianbel.notes.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;
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

    @Transactional
    public List<HierarchicalNodeDTO> getAllNodesHierarchical()  {
        final UserEntity currentUser = userService.getCurrentUser();
        List<NodeEntity> rootNodes =
                nodeRepository.findByUserAndActiveIsTrueAndParentNodeIsNull(currentUser);
        return rootNodes.stream().map(hierarchicalNodeToNodeConverter::convertToDto).collect(Collectors.toList());
    }

    public HierarchicalNodeDTO getHierarchicalNode(final Long nodeId) {
        final UserEntity currentUser = userService.getCurrentUser();
        return nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .map(hierarchicalNodeToNodeConverter::convertToDto)
                .orElseThrow(() -> new NodeNotFoundException("Node with id = '" + nodeId + "' doesn't exist."));
    }

    @Transactional
    public List<HierarchicalNodeDTO> getSharedNodeHierarchical() {

        final Map<String, HierarchicalNodeDTO> sharedNotesByUserName = getSharedNotesWithoutNodesByUserName();

        final Map<String, List<NoteEntity>> notesWithNodesByUserNames = getSharedNotesWithNodesByUserName();

        notesWithNodesByUserNames.forEach((userName, notes) -> populateHierarchyWithUserNode(sharedNotesByUserName, userName, notes));

        return new ArrayList<>(sharedNotesByUserName.values());
    }

    private void populateHierarchyWithUserNode(Map<String, HierarchicalNodeDTO> sharedNotesByUserName, String userName, List<NoteEntity> notes) {
        final HierarchicalNodeDTO userHierarchicalNode =
                sharedNotesByUserName.computeIfAbsent(userName, HierarchicalNodeDTO::new);

        final Map<Long, List<NoteEntity>> notesByNodeId =
                notes.stream().collect(Collectors.groupingBy(note -> note.getNode().getId()));

        notesByNodeId.forEach((nodeId, nodesNote) -> {
            HierarchicalNodeDTO sharedNode = createHierarchicalNode(nodesNote);
            userHierarchicalNode.getChildren().add(sharedNode);
        });
    }

    private HierarchicalNodeDTO createHierarchicalNode(List<NoteEntity> nodesNote) {
        HierarchicalNodeDTO sharedNode = new HierarchicalNodeDTO(nodesNote.get(0).getNode().getName());
        sharedNode.setNotes(nodesNote.stream().map(hierarchicalNoteToNoteConverter::convertToDto).collect(
                Collectors.toList()));
        return sharedNode;
    }

    private Map<String, List<NoteEntity>> getSharedNotesWithNodesByUserName() {
        return noteRepository
                .findAllByUsersToShareContainsAndNodeIsNotNullAndActiveIsTrue(userService.getCurrentUser())
                .stream().filter(this::isNoteWithActiveNode).collect(Collectors.groupingBy(this::getNoteCreatorName));
    }

    private HashMap<String, HierarchicalNodeDTO> getSharedNotesWithoutNodesByUserName() {
        return noteRepository.findAllByUsersToShareContainsAndNodeIsNullAndActiveIsTrue(userService.getCurrentUser())
                .stream().collect(
                        Collectors.toMap(this::getNoteCreatorName, this::getHierarchicalNode, this::mergeSharedNotes, HashMap::new));
    }

    private boolean isNoteWithActiveNode(NoteEntity note) {
        NodeEntity node = note.getNode();
        return node != null && node.isActive();
    }

    @NotNull
    private String getNoteCreatorName(NoteEntity note) {
        return note.getUser().getName();
    }

    private HierarchicalNodeDTO getHierarchicalNode(NoteEntity note) {
        final HierarchicalNodeDTO userNode = new HierarchicalNodeDTO();
        userNode.setName(getNoteCreatorName(note));
        userNode.getNotes().add(hierarchicalNoteToNoteConverter.convertToDto(note));
        return userNode;
    }

    private HierarchicalNodeDTO mergeSharedNotes(HierarchicalNodeDTO a, HierarchicalNodeDTO b) {
        a.getNotes().addAll(b.getNotes());
        return a;
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

    @Transactional
    public List<PersistedNoteDTO> shareNodeWithUser(final Long userId, final Long nodeId) throws UserNotFoundException, NodeNotFoundException {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity nodeToShare = nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .orElseThrow(() -> new NodeNotFoundException("Node with id = '" + nodeId + "' doesn't exist."));
        final UserEntity userToShare = userRepository.findByIdAndActiveIsTrue(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = '" + userId + "' doesn't exist."));

        final Set<NoteEntity> notesToShare = getNotesFromNode(nodeToShare);
        notesToShare.forEach(noteEntity -> noteEntity.getUsersToShare().add(userToShare));
        noteRepository.saveAll(notesToShare);

        return notesToShare.stream().map(persistedNoteToNoteConverter::convertToDto).collect(Collectors.toList());

    }

    @Transactional
    public List<PersistedNoteDTO> unshareNodeWithUser(final Long userId, final Long nodeId) throws UserNotFoundException, NodeNotFoundException {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity nodeToShare = nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .orElseThrow(() -> new NodeNotFoundException("Node with id = '" + nodeId + "' doesn't exist."));

        final Set<NoteEntity> notesToShare = getNotesFromNode(nodeToShare);
        notesToShare.forEach(noteEntity -> {
            final Set<UserEntity> usersToShare = noteEntity.getUsersToShare();
            usersToShare.stream().filter(userEntity -> userEntity.getId() == userId).findAny()
                    .ifPresent(usersToShare::remove);
        });
        noteRepository.saveAll(notesToShare);

        return notesToShare.stream().map(persistedNoteToNoteConverter::convertToDto).collect(Collectors.toList());

    }

    private Set<NoteEntity> getNotesFromNode(NodeEntity nodeEntity) {

        final HashSet<NoteEntity> collectedNotes =
                new HashSet<>(Optional.ofNullable(nodeEntity.getNotes()).orElse(
                        Collections.emptySet()));

        Optional.ofNullable(nodeEntity.getChildren()).orElse(Collections.emptySet()).stream()
                .flatMap(node -> getNotesFromNode(node).stream()).forEach(
                collectedNotes::add);

        return collectedNotes;
    }
}
