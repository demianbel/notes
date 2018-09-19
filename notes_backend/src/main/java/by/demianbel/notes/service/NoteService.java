package by.demianbel.notes.service;

import by.demianbel.notes.converter.note.NoteToSaveNoteConverter;
import by.demianbel.notes.converter.note.PersistedNoteToNoteConverter;
import by.demianbel.notes.dbo.*;
import by.demianbel.notes.dto.hierarhical.HierarchicalDataResponse;
import by.demianbel.notes.dto.note.NoteToSaveDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.exception.NodeNotFoundException;
import by.demianbel.notes.exception.NoteNotFoundException;
import by.demianbel.notes.exception.TagNotFoundException;
import by.demianbel.notes.exception.UserNotFoundException;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.NoteRepository;
import by.demianbel.notes.repository.TagRepository;
import by.demianbel.notes.repository.UserRepository;
import by.demianbel.notes.service.node.NodeHierarchicalService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final NoteToSaveNoteConverter noteToSaveNoteConverter;
    private final PersistedNoteToNoteConverter persistedNoteToNoteConverter;
    private final UserService userService;
    private final NodeHierarchicalService nodeHierarchicalService;

    public PersistedNoteDTO createNote(final NoteToSaveDTO noteDto) {
        final NoteEntity noteToSave = noteToSaveNoteConverter.convertToDbo(noteDto);
        final UserEntity currentUser = userService.getCurrentUser();
        noteToSave.setUser(currentUser);
        final NoteEntity savedNote = noteRepository.save(noteToSave);
        return persistedNoteToNoteConverter.convertToDto(savedNote);
    }

    public PersistedNoteDTO getNote(final Long id) {
        return doWithActiveNote(id, noteEntity -> noteEntity);
    }

    public PersistedNoteDTO deactivateNote(final Long id) {
        return doWithActiveNote(id, noteEntity -> {
            noteEntity.setActive(false);
            return noteRepository.save(noteEntity);
        });
    }

    public List<PersistedNoteDTO> getAllNotes() {
        return noteRepository.findByUserAndActive(userService.getCurrentUser(), true).stream()
                .map(persistedNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList());
    }

    public HierarchicalDataResponse getAllNotesHierarchical() {
        final UserEntity currentUser = userService.getCurrentUser();

        final HierarchicalDataResponse response = new HierarchicalDataResponse();
        final List<PersistedNoteDTO> notesWithoutNodes =
                noteRepository.findByUserAndActiveTrueAndNodeIsNull(currentUser).stream()
                        .map(persistedNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList());
        response.setNotes(notesWithoutNodes);
        response.setNodes(nodeHierarchicalService.getAllNodesHierarchical());
        response.setSharedNodes(nodeHierarchicalService.getSharedNodeHierarchical());
        return response;
    }

    public List<PersistedNoteDTO> getAllNotesByTag(final Long tagId) {
        final UserEntity currentUser = userService.getCurrentUser();
        final TagEntity tagEntity = tagRepository.findByUserAndIdAndActive(currentUser, tagId, true)
                .orElseThrow(() -> new TagNotFoundException("Tag with id = '" + tagId + "' doesn't exist."));
        return tagEntity.getNotes().stream().filter(AbstractEntity::isActive)
                .map(persistedNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList());
    }

    public List<PersistedNoteDTO> getAllNotesByNode(final Long nodeId) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity nodeEntity = nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .orElseThrow(() -> new NodeNotFoundException("Node with id = '" + nodeId + "' doesn't exist."));
        return nodeEntity.getNotes().stream().filter(AbstractEntity::isActive)
                .map(persistedNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList());
    }

    public List<PersistedNoteDTO> findByName(final String name) {
        final List<PersistedNoteDTO> resultNotes = new ArrayList<>();
        final UserEntity currentUser = userService.getCurrentUser();
        final Optional<NoteEntity> equalNote = noteRepository.findFirstByUserAndActiveIsTrueAndName(currentUser, name);
        final List<NoteEntity> similarNotes =
                noteRepository.findAllByUserAndActiveIsTrueAndNameLike(currentUser, "%" + name + "%");

        equalNote.map(persistedNoteToNoteConverter::convertToDto).ifPresent(resultNotes::add);
        final Long equalNoteId;
        if (!resultNotes.isEmpty()) {
            equalNoteId = resultNotes.get(0).getId();
        } else {
            equalNoteId = null;
        }
        similarNotes.stream().filter(tag -> !Objects.equals(tag.getId(), equalNoteId))
                .map(persistedNoteToNoteConverter::convertToDto).forEach(resultNotes::add);

        return resultNotes;
    }

    public List<PersistedNoteDTO> findByText(final String text) {
        final List<PersistedNoteDTO> resultNotes = new ArrayList<>();
        final UserEntity currentUser = userService.getCurrentUser();
        final Optional<NoteEntity> equalNote = noteRepository.findFirstByUserAndActiveIsTrueAndText(currentUser, text);
        final List<NoteEntity> similarNotes =
                noteRepository.findAllByUserAndActiveIsTrueAndTextLike(currentUser, "%" + text + "%");

        equalNote.map(persistedNoteToNoteConverter::convertToDto).ifPresent(resultNotes::add);
        final Long equalNoteId;
        if (!resultNotes.isEmpty()) {
            equalNoteId = resultNotes.get(0).getId();
        } else {
            equalNoteId = null;
        }
        similarNotes.stream().filter(tag -> !Objects.equals(tag.getId(), equalNoteId))
                .map(persistedNoteToNoteConverter::convertToDto).forEach(resultNotes::add);

        return resultNotes;
    }

    public PersistedNoteDTO addTagToNote(final Long tagId, final Long noteId) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NoteEntity noteToAddTag =
                noteRepository.findByUserAndActiveAndId(currentUser, true, noteId)
                        .orElseThrow(() -> new RuntimeException("Note with id = '" + noteId + "' doesn't exist."));
        final TagEntity tagToAddToNote = tagRepository.findByUserAndIdAndActive(currentUser, tagId, true)
                .orElseThrow(() -> new TagNotFoundException("Tag with id = '" + tagId + "' doesn't exist."));
        noteToAddTag.getTags().add(tagToAddToNote);
        final NoteEntity resultNote = noteRepository.save(noteToAddTag);
        return persistedNoteToNoteConverter.convertToDto(resultNote);
    }

    public PersistedNoteDTO removeTagFromNote(final Long tagId, final Long noteId) {
        return doWithActiveNote(noteId, noteEntity -> {
            final Set<TagEntity> noteTags = noteEntity.getTags();
            noteTags.stream().filter(tag -> tag.getId() == tagId).findAny()
                    .ifPresent(noteTags::remove);
            return noteRepository.save(noteEntity);
        });
    }

    public PersistedNoteDTO changeName(final String name, final Long noteId) {
        return doWithActiveNote(noteId, noteEntity -> {
            noteEntity.setName(name);
            return noteRepository.save(noteEntity);
        });
    }

    public PersistedNoteDTO changeText(final String text, final Long noteId) {
        return doWithActiveNote(noteId, noteEntity -> {
            noteEntity.setText(text);
            return noteRepository.save(noteEntity);
        });
    }

    public PersistedNoteDTO moveNoteToNode(final Long nodeId, final Long noteId) {
        final UserEntity currentUser = userService.getCurrentUser();

        final NoteEntity note =
                noteRepository.findByUserAndActiveAndId(currentUser, true, noteId)
                        .orElseThrow(() -> new NoteNotFoundException("Note with id = '" + noteId + "' doesn't exist."));

        final NodeEntity node = nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .orElseThrow(() -> new NodeNotFoundException("Node with id = '" + nodeId + "' doesn't exist."));
        note.setNode(node);

        final NoteEntity savedNote = noteRepository.save(note);
        return persistedNoteToNoteConverter.convertToDto(savedNote);
    }

    public PersistedNoteDTO detachNoteFromNode(final Long noteId) {
        return doWithActiveNote(noteId, noteEntity -> {
            noteEntity.setNode(null);
            return noteRepository.save(noteEntity);
        });
    }


    private PersistedNoteDTO doWithActiveNote(final Long id, final Function<NoteEntity, NoteEntity> function) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NoteEntity noteToProcess =
                noteRepository.findByUserAndActiveAndId(currentUser, true, id)
                        .orElseThrow(() -> new NoteNotFoundException("Note with id = '" + id + "' doesn't exist."));
        final NoteEntity resultNote = function.apply(noteToProcess);
        return persistedNoteToNoteConverter.convertToDto(resultNote);

    }

    public PersistedNoteDTO shareNoteWithUser(final Long userId, final Long noteId) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NoteEntity noteToProcess =
                noteRepository.findByUserAndActiveAndId(currentUser, true, noteId)
                        .orElseThrow(() -> new NoteNotFoundException("Note with id = '" + noteId + "' doesn't exist."));
        final UserEntity userToShare = userRepository.findByIdAndActiveIsTrue(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = '" + userId + "' doesn't exist."));
        noteToProcess.getUsersToShare().add(userToShare);
        final NoteEntity savedEntity = noteRepository.save(noteToProcess);
        return persistedNoteToNoteConverter.convertToDto(savedEntity);
    }

    public PersistedNoteDTO unshareNoteWithUser(final Long userId, final Long noteId) {
        return doWithActiveNote(noteId, noteEntity -> {
            final Set<UserEntity> noteUsers = noteEntity.getUsersToShare();
            noteUsers.stream().filter(user -> user.getId() == userId).findAny()
                    .ifPresent(noteUsers::remove);
            return noteRepository.save(noteEntity);
        });
    }

    public List<PersistedNoteDTO> getSharedNotes() {
        return noteRepository.findAllByUsersToShareContainsAndActiveIsTrue(userService.getCurrentUser()).stream()
                .map(persistedNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList());
    }
}
