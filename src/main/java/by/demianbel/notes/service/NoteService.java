package by.demianbel.notes.service;

import by.demianbel.notes.converter.note.NoteSavingToNoteConverter;
import by.demianbel.notes.converter.note.PersistedNoteToNoteConverter;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.note.NoteToSaveDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.repository.NoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteSavingToNoteConverter noteSavingToNoteConverter;
    private final PersistedNoteToNoteConverter persistedNoteToNoteConverter;
    private final UserService userService;

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
}
