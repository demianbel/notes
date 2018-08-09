package by.demianbel.notes.service;

import by.demianbel.notes.converter.NoteConverter;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dto.NoteSavingDto;
import by.demianbel.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteConverter noteConverter;

    @Autowired
    public NoteService(NoteRepository noteRepository, NoteConverter noteConverter) {
        this.noteRepository = noteRepository;
        this.noteConverter = noteConverter;
    }

    public NoteEntity createNote(final NoteSavingDto noteDto) {
        return noteRepository.save(noteConverter.convertToDbo(noteDto));
    }
}
