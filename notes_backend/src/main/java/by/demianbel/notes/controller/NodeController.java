package by.demianbel.notes.controller;

import by.demianbel.notes.dto.node.NodeToSaveDTO;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.service.node.NodeService;
import by.demianbel.notes.service.node.NodeSharingService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("notes/rest/node")
@PreAuthorize("hasAnyAuthority('user','admin')")
public class NodeController {

    private final NodeService nodeService;
    private final NodeSharingService nodeSharingService;

    @RequestMapping(method = RequestMethod.GET)
    public PersistedNodeDTO findNodeById(final Long id) {
        return nodeService.findNodeById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public PersistedNodeDTO createNode(@RequestBody final NodeToSaveDTO nodeToSaveDTO) {
        return nodeService.createNode(nodeToSaveDTO);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public PersistedNodeDTO deactivateNode(final Long id) {
        return nodeService.deactivateNode(id);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public PersistedNodeDTO changeNodeName(final Long id, final String name) {
        return nodeService.changeNodeName(id, name);
    }

    @RequestMapping(value = "/search/name", method = RequestMethod.GET)
    public List<PersistedNodeDTO> findNodeByName(final String name) {
        return nodeService.findNodeByName(name);
    }

    @RequestMapping(value = "/share/adduser", method = RequestMethod.PUT)
    public List<PersistedNoteDTO> shareNodeWithUser(final Long userId, final Long nodeId) {
        return nodeSharingService.shareNodeWithUser(userId, nodeId);
    }

    @RequestMapping(value = "/share/removeuser", method = RequestMethod.PUT)
    public List<PersistedNoteDTO> unshareNodeWithUser(final Long userId, final Long nodeId) {
        return nodeSharingService.unshareNodeWithUser(userId, nodeId);
    }
}
