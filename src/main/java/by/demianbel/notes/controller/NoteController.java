package by.demianbel.notes.controller;

import by.demianbel.notes.dto.hierarhical.HierarchicalDataResponse;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.note.NoteToSaveDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.service.NodeService;
import by.demianbel.notes.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;
    private final NodeService nodeService;

    @RequestMapping(method = RequestMethod.POST)
    public PersistedNoteDTO addNote(@RequestBody NoteToSaveDTO noteToSaveDto) {
        return noteService.createNote(noteToSaveDto);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PersistedNoteDTO getNoteById(Long id) {
        return noteService.getNote(id);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<PersistedNoteDTO> getAllNotes() {
        return noteService.getAllNotes();
    }

    @RequestMapping(value = "/hierarсhy", method = RequestMethod.GET)
    public HierarchicalDataResponse getAllNotesHierarchical() {
        return noteService.getAllNotesHierarchical();
    }

    @RequestMapping(value = "/hierarсhy/node", method = RequestMethod.GET)
    public HierarchicalNodeDTO getNotesHierarchicalByNode(Long nodeId) {
        return nodeService.getHierarchicalNode(nodeId);
    }

    @RequestMapping(value = "/find/tag", method = RequestMethod.GET)
    public List<PersistedNoteDTO> getAllNotesByTag(Long tagId) {
        return noteService.getAllNotesByTag(tagId);
    }

    @RequestMapping(value = "/find/node", method = RequestMethod.GET)
    public List<PersistedNoteDTO> getAllNotesByNode(Long nodeId) {
        return noteService.getAllNotesByNode(nodeId);
    }

    @RequestMapping(value = "/find/name", method = RequestMethod.GET)
    public List<PersistedNoteDTO> findNotesByName(String name) {
        return noteService.findByName(name);
    }

    @RequestMapping(value = "/find/text", method = RequestMethod.GET)
    public List<PersistedNoteDTO> findNotesByText(String text) {
        return noteService.findByText(text);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public PersistedNoteDTO deactivateNote(Long id) {
        return noteService.deactivateNote(id);
    }
}
