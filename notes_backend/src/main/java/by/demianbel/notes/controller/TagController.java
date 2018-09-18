package by.demianbel.notes.controller;

import by.demianbel.notes.dto.tag.PersistedTagDTO;
import by.demianbel.notes.dto.tag.TagNameDTO;
import by.demianbel.notes.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("notes/rest/tag")
@PreAuthorize("hasAnyAuthority('user','admin')")
public class TagController {

    private final TagService tagService;

    @RequestMapping(value = "/search/name", method = RequestMethod.GET)
    public List<PersistedTagDTO> findTagByName(@RequestBody TagNameDTO tagNameDTO) {
        return tagService.findTagByName(tagNameDTO);
    }

    @RequestMapping(value = "/search/all", method = RequestMethod.GET)
    public List<PersistedTagDTO> findAllTags() {
        return tagService.findAllTags();
    }

    @RequestMapping(method = RequestMethod.GET)
    public PersistedTagDTO findTagById(@RequestParam final Long id) {
        return tagService.findTagById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public PersistedTagDTO createTagWithName(@RequestBody final TagNameDTO tagNameDTO) {
        return tagService.createTagWithName(tagNameDTO);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public PersistedTagDTO deactivateTag(@RequestParam final Long id) {
        return tagService.deactivateTag(id);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public PersistedTagDTO removeNoteLinks(@RequestParam final Long id) {
        return tagService.removeNoteLinks(id);
    }
}
