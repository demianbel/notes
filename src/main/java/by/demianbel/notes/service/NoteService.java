package by.demianbel.notes.service;

import by.demianbel.notes.converter.note.NoteSavingToNoteConverter;
import by.demianbel.notes.converter.note.PersistedNoteToNoteConverter;
import by.demianbel.notes.dbo.AbstractEntity;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.hierarhical.HierarchicalDataResponse;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.dto.note.NoteToSaveDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.NoteRepository;
import by.demianbel.notes.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final NodeRepository nodeRepository;
    private final NoteSavingToNoteConverter noteSavingToNoteConverter;
    private final PersistedNoteToNoteConverter persistedNoteToNoteConverter;
    private final UserService userService;
    private final NodeService nodeService;

    public PersistedNoteDTO createNote(final NoteToSaveDTO noteDto) {
        final NoteEntity noteToSave = noteSavingToNoteConverter.convertToDbo(noteDto);
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

    private PersistedNoteDTO doWithActiveNote(final Long id, final Function<NoteEntity, NoteEntity> function) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NoteEntity noteToProcess =
                noteRepository.findByUserAndActiveAndId(currentUser, true, id)
                        .orElseThrow(() -> new RuntimeException("Note with id = '" + id + "' doesn't exist."));
        final NoteEntity resultNote = function.apply(noteToProcess);
        return persistedNoteToNoteConverter.convertToDto(resultNote);

    }

    public List<PersistedNoteDTO> getAllNotes() {
        return noteRepository.findByUserAndActive(userService.getCurrentUser(), true).stream()
                .map(persistedNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList());
    }

    public HierarchicalDataResponse getAllNotesHierarchical() {
        final UserEntity currentUser = userService.getCurrentUser();

        final HierarchicalDataResponse response = new HierarchicalDataResponse();
        List<PersistedNoteDTO> notesWithoutNodes =
                noteRepository.findByUserAndActiveTrueAndNodeIsNull(currentUser).stream()
                        .map(persistedNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList());
        response.setNotes(notesWithoutNodes);
        response.setNodes(nodeService.getAllNodesHierarhical());
        return response;
    }

    public List<PersistedNoteDTO> getAllNotesByTag(final Long tagId) {
        final UserEntity currentUser = userService.getCurrentUser();
        final TagEntity tagEntity = tagRepository.findByUserAndIdAndActive(currentUser, tagId, true)
                .orElseThrow(() -> new RuntimeException("Tag with id = '" + tagId + "' doesn't exist."));
        return tagEntity.getNotes().stream().filter(AbstractEntity::isActive)
                .map(persistedNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList());
    }

    public List<PersistedNoteDTO> getAllNotesByNode(final Long nodeId) {
        final UserEntity currentUser = userService.getCurrentUser();
        final NodeEntity nodeEntity = nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, nodeId)
                .orElseThrow(() -> new RuntimeException("Node with id = '" + nodeId + "' doesn't exist."));
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
}
