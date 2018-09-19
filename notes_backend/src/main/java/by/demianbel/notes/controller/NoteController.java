package by.demianbel.notes.controller;

import by.demianbel.notes.dto.hierarhical.HierarchicalDataResponse;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.note.NoteToSaveDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.service.NoteService;
import by.demianbel.notes.service.node.NodeHierarchicalService;
import by.demianbel.notes.service.node.NodeService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("notes/rest/note")
@PreAuthorize("hasAnyAuthority('user','admin')")
public class NoteController {

    private final NoteService noteService;
    private final NodeService nodeService;
    private final NodeHierarchicalService nodeHierarchicalService;

    @RequestMapping(method = RequestMethod.POST)
    public PersistedNoteDTO addNote(@RequestBody final NoteToSaveDTO noteToSaveDto) {
        return noteService.createNote(noteToSaveDto);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PersistedNoteDTO getNoteById(final Long id) {
        return noteService.getNote(id);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<PersistedNoteDTO> getAllNotes() {
        return noteService.getAllNotes();
    }

    @RequestMapping(value = "/hierarchy", method = RequestMethod.GET)
    public HierarchicalDataResponse getAllNotesHierarchical() {
        return noteService.getAllNotesHierarchical();
    }

    @RequestMapping(value = "/hierarchy/node", method = RequestMethod.GET)
    public HierarchicalNodeDTO getNotesHierarchicalByNode(final Long nodeId) {
        return nodeHierarchicalService.getHierarchicalNode(nodeId);
    }

    @RequestMapping(value = "/find/tag", method = RequestMethod.GET)
    public List<PersistedNoteDTO> getAllNotesByTag(final Long tagId) {
        return noteService.getAllNotesByTag(tagId);
    }

    @RequestMapping(value = "/find/node", method = RequestMethod.GET)
    public List<PersistedNoteDTO> getAllNotesByNode(final Long nodeId) {
        return noteService.getAllNotesByNode(nodeId);
    }

    @RequestMapping(value = "/find/name", method = RequestMethod.GET)
    public List<PersistedNoteDTO> findNotesByName(final String name) {
        return noteService.findByName(name);
    }

    @RequestMapping(value = "/find/text", method = RequestMethod.GET)
    public List<PersistedNoteDTO> findNotesByText(final String text) {
        return noteService.findByText(text);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public PersistedNoteDTO deactivateNote(final Long id) {
        return noteService.deactivateNote(id);
    }

    @RequestMapping(value = "/update/tag/add", method = RequestMethod.PUT)
    public PersistedNoteDTO addTagToNote(final Long tagId, final Long noteId) {
        return noteService.addTagToNote(tagId, noteId);
    }

    @RequestMapping(value = "/update/tag/delete", method = RequestMethod.PUT)
    public PersistedNoteDTO removeTagFromNote(final Long tagId, final Long noteId) {
        return noteService.removeTagFromNote(tagId, noteId);
    }

    @RequestMapping(value = "/update/node/move", method = RequestMethod.PUT)
    public PersistedNoteDTO moveNoteToNode(final Long nodeId, final Long noteId) {
        return noteService.moveNoteToNode(nodeId, noteId);
    }

    @RequestMapping(value = "/update/node/detach", method = RequestMethod.PUT)
    public PersistedNoteDTO detachNoteFromNode(final Long noteId) {
        return noteService.detachNoteFromNode(noteId);
    }

    @RequestMapping(value = "/update/name", method = RequestMethod.PUT)
    public PersistedNoteDTO changeName(final String name, final Long noteId) {
        return noteService.changeName(name, noteId);
    }

    @RequestMapping(value = "/update/text", method = RequestMethod.PUT)
    public PersistedNoteDTO changeText(final String text, final Long noteId) {
        return noteService.changeText(text, noteId);
    }

    @RequestMapping(value = "/share/adduser", method = RequestMethod.PUT)
    public PersistedNoteDTO shareNoteWithUser(final Long userId, final Long noteId) {
        return noteService.shareNoteWithUser(userId, noteId);
    }

    @RequestMapping(value = "/share/removeuser", method = RequestMethod.PUT)
    public PersistedNoteDTO unshareNoteWithUser(final Long userId, final Long noteId) {
        return noteService.unshareNoteWithUser(userId, noteId);
    }

    @RequestMapping(value = "/share", method = RequestMethod.GET)
    public List<PersistedNoteDTO> getSharedNotes() {
        return noteService.getSharedNotes();
    }
}
