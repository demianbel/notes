package by.demianbel.notes.service.node;

import by.demianbel.notes.converter.node.HierarchicalNodeToNodeConverter;
import by.demianbel.notes.converter.note.HierarchicalNoteToNoteConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.exception.NodeNotFoundException;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.NoteRepository;
import by.demianbel.notes.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NodeHierarchicalService {
    private final NodeRepository nodeRepository;
    private final NoteRepository noteRepository;
    private final UserService userService;
    private final HierarchicalNodeToNodeConverter hierarchicalNodeToNodeConverter;
    private final HierarchicalNoteToNoteConverter hierarchicalNoteToNoteConverter;

    @Transactional
    public List<HierarchicalNodeDTO> getAllNodesHierarchical() {
        final UserEntity currentUser = userService.getCurrentUser();
        final List<NodeEntity> rootNodes =
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

    private void populateHierarchyWithUserNode(final Map<String, HierarchicalNodeDTO> sharedNotesByUserName, final String userName, final List<NoteEntity> notes) {
        final HierarchicalNodeDTO userHierarchicalNode =
                sharedNotesByUserName.computeIfAbsent(userName, HierarchicalNodeDTO::new);

        final Map<Long, List<NoteEntity>> notesByNodeId =
                notes.stream().collect(Collectors.groupingBy(note -> note.getNode().getId()));

        notesByNodeId.forEach((nodeId, nodesNote) -> {
            HierarchicalNodeDTO sharedNode = createHierarchicalNode(nodesNote);
            userHierarchicalNode.getChildren().add(sharedNode);
        });
    }

    private HierarchicalNodeDTO createHierarchicalNode(final List<NoteEntity> nodesNote) {
        final HierarchicalNodeDTO sharedNode = new HierarchicalNodeDTO(nodesNote.get(0).getNode().getName());
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


    private boolean isNoteWithActiveNode(final NoteEntity note) {
        final NodeEntity node = note.getNode();
        return node != null && node.isActive();
    }

    @NotNull
    private String getNoteCreatorName(final NoteEntity note) {
        return note.getUser().getName();
    }

    private HierarchicalNodeDTO getHierarchicalNode(final NoteEntity note) {
        final HierarchicalNodeDTO userNode = new HierarchicalNodeDTO();
        userNode.setName(getNoteCreatorName(note));
        userNode.getNotes().add(hierarchicalNoteToNoteConverter.convertToDto(note));
        return userNode;
    }

    private HierarchicalNodeDTO mergeSharedNotes(final HierarchicalNodeDTO a, final HierarchicalNodeDTO b) {
        a.getNotes().addAll(b.getNotes());
        return a;
    }
}
