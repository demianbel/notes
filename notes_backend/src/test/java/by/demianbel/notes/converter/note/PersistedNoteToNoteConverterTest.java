package by.demianbel.notes.converter.note;

import by.demianbel.notes.converter.node.PersistedNodeToNodeConverter;
import by.demianbel.notes.converter.tag.PersistedTagTagEntityConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.dto.tag.PersistedTagDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class PersistedNoteToNoteConverterTest {

    private static final String NODE_NAME = "node name";
    private static final String NOTE_NAME = "note name";
    private static final String NOTE_TEXT = "note text";
    private static final String FIRST_TAG_NAME = "first tag name";
    private static final String SECOND_TAG_NAME = "second tag name";

    private PersistedNoteToNoteConverter converter;

    @Before
    public void setUp() {
        converter = new PersistedNoteToNoteConverter(new PersistedTagTagEntityConverter(), new PersistedNodeToNodeConverter());
    }

    @Test
    public void convertToDto() {

        final NoteEntity noteEntity = new NoteEntity();
        noteEntity.setId(1L);
        noteEntity.setName(NOTE_NAME);
        noteEntity.setText(NOTE_TEXT);

        final NodeEntity node = new NodeEntity();
        node.setId(2L);
        node.setName(NODE_NAME);
        noteEntity.setNode(node);

        final TagEntity firstTag = new TagEntity();
        firstTag.setName(FIRST_TAG_NAME);

        final TagEntity secondTag = new TagEntity();
        secondTag.setName(SECOND_TAG_NAME);

        noteEntity.setTags(Set.of(firstTag, secondTag));

        final PersistedNoteDTO persistedNoteDTO = converter.convertToDto(noteEntity);

        Assert.assertEquals(Long.valueOf(1L), persistedNoteDTO.getId());
        Assert.assertEquals(NOTE_NAME, persistedNoteDTO.getName());
        Assert.assertEquals(NOTE_TEXT, persistedNoteDTO.getText());

        final PersistedNodeDTO convertedNode = persistedNoteDTO.getNode();
        Assert.assertEquals(NODE_NAME, convertedNode.getName());

        final List<PersistedTagDTO> tags = persistedNoteDTO.getTags();
        Assert.assertEquals(2, tags.size());
        Assert.assertTrue(tags.stream().anyMatch(tag -> FIRST_TAG_NAME.equals(tag.getName())));
        Assert.assertTrue(tags.stream().anyMatch(tag -> SECOND_TAG_NAME.equals(tag.getName())));

    }

    @Test(expected = UnsupportedOperationException.class)
    public void convertToDbo() {
        converter.convertToDbo(new PersistedNoteDTO());
    }
}