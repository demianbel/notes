package by.demianbel.notes.controller;

import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dto.NoteSavingDto;
import by.demianbel.notes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(final NoteService noteService) {
        this.noteService = noteService;
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public NoteEntity addNote(NoteSavingDto noteSavingDto) {
        return noteService.createNote(noteSavingDto);
    }
}
