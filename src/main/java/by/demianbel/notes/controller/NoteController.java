package by.demianbel.notes.controller;

import by.demianbel.notes.dto.note.NoteToSaveDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    @RequestMapping(method = RequestMethod.POST)
    public PersistedNoteDTO addNote(@RequestBody NoteToSaveDTO noteToSaveDto) {
        return noteService.createNote(noteToSaveDto);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PersistedNoteDTO getNoteById(Long id) {
        return noteService.getNote(id);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public PersistedNoteDTO deactivateNote(Long id) {
        return noteService.deactivateNote(id);
    }
}
