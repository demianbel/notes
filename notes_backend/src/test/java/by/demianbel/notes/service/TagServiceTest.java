package by.demianbel.notes.service;

import by.demianbel.notes.converter.tag.PersistedTagTagEntityConverter;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dto.tag.PersistedTagDTO;
import by.demianbel.notes.dto.tag.TagNameDTO;
import by.demianbel.notes.exception.TagNotFoundException;
import by.demianbel.notes.repository.TagRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class TagServiceTest {

    private static final String TAG_NAME = "name";

    @TestConfiguration
    static class TagServiceTestConfiguration {
        @Bean
        public TagService tagService(final TagRepository tagRepository, final UserService userService,
                                     final PersistedTagTagEntityConverter persistedTagTagEntityConverter) {
            return new TagService(tagRepository, userService, persistedTagTagEntityConverter);
        }

        @Bean
        public PersistedTagTagEntityConverter persistedTagTagEntityConverter() {
            return new PersistedTagTagEntityConverter();
        }
    }

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Test
    public void findTagByName() {
        final TagEntity equalTagEntity = new TagEntity();
        equalTagEntity.setId(1L);
        Mockito.when(tagRepository.findFirstByUserAndActiveAndName(Mockito.any(), Mockito.eq(true), Mockito.eq(TAG_NAME)))
                .thenReturn(Optional.of(equalTagEntity));

        final TagEntity likeTagEntity = new TagEntity();
        likeTagEntity.setId(2L);
        Mockito.when(tagRepository.findAllByUserAndActiveAndNameLike(Mockito.any(), Mockito.eq(true), Mockito.eq(TAG_NAME)))
                .thenReturn(Collections.singletonList(likeTagEntity));

        final TagNameDTO tagNameDTO = new TagNameDTO();
        tagNameDTO.setName(TAG_NAME);
        final List<PersistedTagDTO> tagByName = tagService.findTagByName(tagNameDTO);

        Assert.assertEquals(2, tagByName.size());
        Assert.assertEquals(Long.valueOf(1L), tagByName.get(0).getId());
        Assert.assertEquals(Long.valueOf(2L), tagByName.get(1).getId());

    }

    @Test
    public void createTagWithName() {

        final TagEntity savedTag = new TagEntity();
        savedTag.setName(TAG_NAME);
        savedTag.setId(1L);

        Mockito.when(tagRepository.save(Mockito.any())).thenReturn(savedTag);

        final TagNameDTO tagNameDTO = new TagNameDTO();
        tagNameDTO.setName(TAG_NAME);
        final PersistedTagDTO persistedTagDTO = tagService.createTagWithName(tagNameDTO);

        Assert.assertNotNull(persistedTagDTO);
        Assert.assertEquals(Long.valueOf(1L), persistedTagDTO.getId());
        Assert.assertEquals(TAG_NAME, persistedTagDTO.getName());

    }

    @Test(expected = TagNotFoundException.class)
    public void findTagByIdNotExist() {
        tagService.findTagById(1L);
    }

    @Test
    public void findTagById() {
        Mockito.when(tagRepository.findByUserAndIdAndActive(Mockito.any(), Mockito.eq(1L), Mockito.eq(true)))
                .thenReturn(Optional.of(new TagEntity()));
        final PersistedTagDTO foundTag = tagService.findTagById(1L);
        Assert.assertNotNull(foundTag);
    }

    @Test
    public void deactivateTag() {
        final TagEntity tag = new TagEntity();
        tag.setId(1L);

        Mockito.when(tagRepository.findByUserAndId(Mockito.any(), Mockito.eq(1L)))
                .thenReturn(Optional.of(tag));

        Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tag);

        final PersistedTagDTO persistedTagDTO = tagService.deactivateTag(1L);

        Assert.assertNotNull(persistedTagDTO);
        Assert.assertEquals(Long.valueOf(1L), persistedTagDTO.getId());
    }

    @Test
    public void findAllTags() {
        Mockito.when(tagRepository.findByUserAndActive(Mockito.any(), Mockito.eq(true)))
                .thenReturn(List.of(new TagEntity(), new TagEntity(), new TagEntity()));
        final List<PersistedTagDTO> allTags = tagService.findAllTags();
        Assert.assertEquals(3, allTags.size());
    }

    @Test
    public void removeNoteLinks() {
        final TagEntity tag = new TagEntity();
        tag.setId(1L);

        Mockito.when(tagRepository.findByUserAndId(Mockito.any(), Mockito.eq(1L)))
                .thenReturn(Optional.of(tag));

        Mockito.when(tagRepository.save(Mockito.any())).thenReturn(tag);

        final PersistedTagDTO persistedTagDTO = tagService.removeNoteLinks(1L);

        Assert.assertNotNull(persistedTagDTO);
        Assert.assertEquals(Long.valueOf(1L), persistedTagDTO.getId());
    }
}