package by.demianbel.notes.controller;

import by.demianbel.notes.dto.tag.PersistedTagDTO;
import by.demianbel.notes.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/tag")
@PreAuthorize("hasAnyAuthority('user','admin')")
public class TagController {

    private final TagService tagService;

    @RequestMapping(value = "/search/name", method = RequestMethod.GET)
    public List<PersistedTagDTO> findTagByName(String name) {
        return tagService.findTagByName(name);
    }

    @RequestMapping(value = "/search/all", method = RequestMethod.GET)
    public List<PersistedTagDTO> findAllTags() {
        return tagService.findAllTags();
    }

    @RequestMapping(method = RequestMethod.GET)
    public PersistedTagDTO findTagById(final Long id) {
        return tagService.findTagById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public PersistedTagDTO createTagWithName(final String name) {
        return tagService.createTagWithName(name);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public PersistedTagDTO deactivateTag(final Long id) {
        return tagService.deactivateTag(id);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public PersistedTagDTO removeNoteLinks(final Long id) {
        return tagService.removeNoteLinks(id);
    }
}
