package by.demianbel.notes.converter.note;

import by.demianbel.notes.converter.tag.PersistedTagTagEntityConverter;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dto.note.HierarchicalNoteDTO;
import by.demianbel.notes.dto.tag.PersistedTagDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HierarchicalNoteToNoteConverterTest {

    private static final String NOTE_NAME = "note name";
    private static final String NOTE_TEXT = "note text";
    private static final String FIRST_TAG_NAME = "first tag name";
    private static final String SECOND_TAG_NAME = "second tag name";

    private HierarchicalNoteToNoteConverter converter;

    @Before
    public void setUp() {
        converter = new HierarchicalNoteToNoteConverter(new PersistedTagTagEntityConverter());
    }

    @Test
    public void convertToDto() {
        final NoteEntity noteEntity = createNoteEntity(NOTE_NAME, NOTE_TEXT, 1L);

        final Set<TagEntity> tags = new HashSet<>();
        tags.add(createTag(FIRST_TAG_NAME));
        tags.add(createTag(SECOND_TAG_NAME));

        noteEntity.setTags(tags);

        final HierarchicalNoteDTO hierarchicalNoteDTO = converter.convertToDto(noteEntity);
        Assert.assertEquals(NOTE_NAME, hierarchicalNoteDTO.getName());
        Assert.assertEquals(NOTE_TEXT, hierarchicalNoteDTO.getText());
        Assert.assertEquals(Long.valueOf(1L), hierarchicalNoteDTO.getId());

        final List<PersistedTagDTO> convertedTags = hierarchicalNoteDTO.getTags();
        Assert.assertNotNull(convertedTags);
        Assert.assertEquals(2, convertedTags.size());
        Assert.assertTrue(convertedTags.stream().anyMatch(tag -> FIRST_TAG_NAME.equals(tag.getName())));
        Assert.assertTrue(convertedTags.stream().anyMatch(tag -> SECOND_TAG_NAME.equals(tag.getName())));
    }

    private NoteEntity createNoteEntity(String name, String text, long id) {
        final NoteEntity noteEntity = new NoteEntity();

        noteEntity.setActive(true);
        noteEntity.setName(name);
        noteEntity.setText(text);
        noteEntity.setId(id);

        return noteEntity;
    }

    private TagEntity createTag(String firstTagName) {
        final TagEntity firstTag = new TagEntity();
        firstTag.setName(firstTagName);
        return firstTag;
    }

    @Test(expected = UnsupportedOperationException.class)
    public void convertToDbo() {
        converter.convertToDbo(new HierarchicalNoteDTO());
    }
}