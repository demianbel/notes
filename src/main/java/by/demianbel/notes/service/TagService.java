package by.demianbel.notes.service;

import by.demianbel.notes.converter.tag.PersistedTagTagEntityConverter;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.tag.PersistedTagDTO;
import by.demianbel.notes.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final UserService userService;
    private final PersistedTagTagEntityConverter persistedTagTagEntityConverter;


    public List<PersistedTagDTO> findTagByName(final String name) {
        final ArrayList<PersistedTagDTO> resultTags = new ArrayList<>();
        final UserEntity currentUser = userService.getCurrentUser();
        final Optional<TagEntity> equalTag = tagRepository.findFirstByUserAndActiveAndName(currentUser, true, name);
        final List<TagEntity> similarTags =
                tagRepository.findAllByUserAndActiveAndNameLike(currentUser, true, "%" + name + "%");

        equalTag.map(persistedTagTagEntityConverter::convertToDto).ifPresent(resultTags::add);
        final Long filterTagId;
        if (!resultTags.isEmpty()) {
            filterTagId = resultTags.get(0).getId();
        } else {
            filterTagId = null;
        }
        similarTags.stream().filter(tag -> !Objects.equals(tag.getId(), filterTagId))
                .map(persistedTagTagEntityConverter::convertToDto).forEach(resultTags::add);

        return resultTags;

    }

    public PersistedTagDTO createTagWithName(final String name) {
        final TagEntity tagToSave;
        final UserEntity currentUser = userService.getCurrentUser();
        final Optional<TagEntity> existingTag = tagRepository.findFirstByUserAndName(currentUser, name);
        if (existingTag.isPresent()) {
            tagToSave = existingTag.get();
        } else {
            tagToSave = new TagEntity();
            tagToSave.setName(name);
        }
        tagToSave.setActive(true);
        tagToSave.setUser(currentUser);
        final TagEntity savedTag = tagRepository.save(tagToSave);
        return persistedTagTagEntityConverter.convertToDto(savedTag);
    }

    public PersistedTagDTO findTagById(final Long id) {
        return doWithActiveTag(id, tagEntity -> tagEntity);
    }

    public PersistedTagDTO deactivateTag(final Long id) {

        return doWithTag(id, tagEntity -> {
            tagEntity.setActive(false);
            return tagRepository.save(tagEntity);
        });

    }

    public List<PersistedTagDTO> findAllTags() {
        final UserEntity currentUser = userService.getCurrentUser();
        return tagRepository.findByUserAndActive(currentUser, true).stream()
                .map(persistedTagTagEntityConverter::convertToDto).collect(Collectors.toList());
    }

    public PersistedTagDTO removeNoteLinks(final Long id) {
        return doWithTag(id, tagEntity -> {
            tagEntity.setNotes(Collections.emptySet());
            return tagRepository.save(tagEntity);
        });
    }

    private PersistedTagDTO doWithTag(final Long id, final Function<TagEntity, TagEntity> function) {
        final UserEntity currentUser = userService.getCurrentUser();
        final TagEntity tagToProcess =
                tagRepository.findByUserAndId(currentUser, id)
                        .orElseThrow(() -> new RuntimeException("Tag with id = '" + id + "' doesn't exist."));
        final TagEntity resultTag = function.apply(tagToProcess);
        return persistedTagTagEntityConverter.convertToDto(resultTag);

    }

    private PersistedTagDTO doWithActiveTag(final Long id, final Function<TagEntity, TagEntity> function) {
        final UserEntity currentUser = userService.getCurrentUser();
        final TagEntity tagToProcess =
                tagRepository.findByUserAndIdAndActive(currentUser, id, true)
                        .orElseThrow(() -> new RuntimeException("Tag with id = '" + id + "' doesn't exist."));
        final TagEntity resultTag = function.apply(tagToProcess);
        return persistedTagTagEntityConverter.convertToDto(resultTag);

    }
}