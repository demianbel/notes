package by.demianbel.notes.converter.note;

import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.note.NoteToSaveDTO;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.TagRepository;
import by.demianbel.notes.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@RunWith(MockitoJUnitRunner.class)
public class NoteToSaveNoteConverterTest {

    private static final String NOTE_NAME = "note name";
    private static final String NOTE_TEXT = "note text";
    private static final String NODE_NAME = "Node name";
    private static final String FIRST_TAG_NAME = "first tag";
    private static final String SECOND_TAG_NAME = "second tag";

    @Mock
    private TagRepository tagRepository;
    @Mock
    private NodeRepository nodeRepository;
    @Mock
    private UserService userService;

    private NoteToSaveNoteConverter converter;

    @Before
    public void setUp() {
        converter = new NoteToSaveNoteConverter(tagRepository, nodeRepository, userService);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void convertToDto() {
        converter.convertToDto(new NoteEntity());
    }

    @Test
    public void convertToDbo() {

        Mockito.when(userService.getCurrentUser()).thenReturn(new UserEntity());

        final NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setId(1L);
        nodeEntity.setName(NODE_NAME);
        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(1L))).thenReturn(Optional.of(nodeEntity));

        final TagEntity firstTag = createTag(10L, FIRST_TAG_NAME);
        final TagEntity secondTag = createTag(11L, SECOND_TAG_NAME);
        Mockito.when(tagRepository.findByUserAndActiveAndIdIn(Mockito.any(), Mockito.eq(true), Mockito.eq(List.of(10L, 11L))))
                .thenReturn(List.of(firstTag, secondTag));

        final NoteToSaveDTO noteToSaveDto = new NoteToSaveDTO();

        noteToSaveDto.setName(NOTE_NAME);
        noteToSaveDto.setText(NOTE_TEXT);
        noteToSaveDto.setNodeId(1L);
        noteToSaveDto.setTagIds(List.of(10L, 11L));

        final NoteEntity noteEntity = converter.convertToDbo(noteToSaveDto);

        Assert.assertEquals(NOTE_NAME, noteEntity.getName());
        Assert.assertEquals(NOTE_TEXT, noteEntity.getText());

        final Set<TagEntity> convertedTags = noteEntity.getTags();
        Assert.assertNotNull(convertedTags);
        Assert.assertEquals(2, convertedTags.size());
        Assert.assertTrue(convertedTags.stream().anyMatch(tag -> FIRST_TAG_NAME.equals(tag.getName())));
        Assert.assertTrue(convertedTags.stream().anyMatch(tag -> SECOND_TAG_NAME.equals(tag.getName())));

        final NodeEntity convertedNode = noteEntity.getNode();
        Assert.assertEquals(NODE_NAME, convertedNode.getName());

    }

    private TagEntity createTag(long l, String firstTagName) {
        final TagEntity firstTag = new TagEntity();
        firstTag.setId(l);
        firstTag.setName(firstTagName);
        return firstTag;
    }
}