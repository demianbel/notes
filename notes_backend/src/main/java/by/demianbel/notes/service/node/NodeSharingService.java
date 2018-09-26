package by.demianbel.notes.service.node;

import by.demianbel.notes.converter.note.PersistedNoteToNoteConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.exception.NodeNotFoundException;
import by.demianbel.notes.exception.UserNotFoundException;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.NoteRepository;
import by.demianbel.notes.repository.UserRepository;
import by.demianbel.notes.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NodeSharingService {

    private final NodeRepository nodeRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PersistedNoteToNoteConverter persistedNoteToNoteConverter;

    @Transactional
    public List<PersistedNoteDTO> shareNodeWithUser(final Long userId, final Long nodeId)
            throws UserNotFoundException, NodeNotFoundException {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity nodeToShare = nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .orElseThrow(() -> new NodeNotFoundException("Node with id = '" + nodeId + "' doesn't exist."));
        final UserEntity userToShare = userRepository.findByIdAndActiveIsTrue(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = '" + userId + "' doesn't exist."));

        final Set<NoteEntity> notesToShare = getNotesFromNode(nodeToShare);
        notesToShare.forEach(noteEntity -> {
            final Set<UserEntity> usersToShare = noteEntity.getUsersToShare();
            if (usersToShare != null) {
                usersToShare.add(userToShare);
            } else {
                final HashSet<UserEntity> createdUsersToShare = new HashSet<>();
                createdUsersToShare.add(userToShare);
                noteEntity.setUsersToShare(createdUsersToShare);
            }
        });
        noteRepository.saveAll(notesToShare);

        return notesToShare.stream().map(persistedNoteToNoteConverter::convertToDto).collect(Collectors.toList());

    }

    @Transactional
    public List<PersistedNoteDTO> unshareNodeWithUser(final Long userId, final Long nodeId)
            throws UserNotFoundException, NodeNotFoundException {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity nodeToShare = nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .orElseThrow(() -> new NodeNotFoundException("Node with id = '" + nodeId + "' doesn't exist."));

        final Set<NoteEntity> notesToShare = getNotesFromNode(nodeToShare);
        notesToShare.forEach(noteEntity -> Optional.ofNullable(noteEntity.getUsersToShare())
                .ifPresent(users -> removeUserWithIdFromCollection(userId, users)));
        noteRepository.saveAll(notesToShare);

        return notesToShare.stream().map(persistedNoteToNoteConverter::convertToDto).collect(Collectors.toList());

    }

    private void removeUserWithIdFromCollection(final Long userId, final Set<UserEntity> users) {
        users.stream().filter(userEntity -> userEntity.getId() == userId).findAny().ifPresent(users::remove);
    }

    private Set<NoteEntity> getNotesFromNode(final NodeEntity nodeEntity) {

        final HashSet<NoteEntity> collectedNotes =
                new HashSet<>(Optional.ofNullable(nodeEntity.getNotes()).orElse(
                        Collections.emptySet()));

        Optional.ofNullable(nodeEntity.getChildren()).orElse(Collections.emptySet()).stream()
                .flatMap(node -> getNotesFromNode(node).stream()).forEach(
                collectedNotes::add);

        return collectedNotes;
    }
}
