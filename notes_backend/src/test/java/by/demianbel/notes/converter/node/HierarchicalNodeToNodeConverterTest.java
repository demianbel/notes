package by.demianbel.notes.converter.node;

import by.demianbel.notes.converter.note.HierarchicalNoteToNoteConverter;
import by.demianbel.notes.converter.tag.PersistedTagTagEntityConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.note.HierarchicalNoteDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class HierarchicalNodeToNodeConverterTest {

    private static final String ROOT_NOTE_TEXT = "root note text";
    private static final String ROOT_NOTE_NAME = "root note name";
    private static final String ROOT_NODE_NAME = "root node name";

    private static final String LEAF_NOTE_TEXT = "leaf note text";
    private static final String LEAF_NOTE_NAME = "leaf note name";
    private static final String LEAF_NODE_NAME = "leaf node name";

    private HierarchicalNodeToNodeConverter hierarchicalNodeToNodeConverter;

    @Before
    public void setUp() throws Exception {
        hierarchicalNodeToNodeConverter =
                new HierarchicalNodeToNodeConverter(new HierarchicalNoteToNoteConverter(new PersistedTagTagEntityConverter()));

    }

    @Test
    public void convertToDto() {

        final NodeEntity rootNodeEntity = createNodeEntity(ROOT_NODE_NAME);

        final NoteEntity rootNoteEntity = createNoteEntity(1L, ROOT_NOTE_TEXT, ROOT_NOTE_NAME);

        rootNoteEntity.setNode(rootNodeEntity);
        rootNodeEntity.setNotes(Collections.singleton(rootNoteEntity));

        final NodeEntity childrenNodeEntity = createNodeEntity(LEAF_NODE_NAME);

        final NoteEntity childrenNoteEntity = createNoteEntity(2L, LEAF_NOTE_TEXT, LEAF_NOTE_NAME);

        childrenNoteEntity.setNode(childrenNodeEntity);
        childrenNodeEntity.setNotes(Collections.singleton(childrenNoteEntity));

        childrenNodeEntity.setParentNode(rootNodeEntity);
        rootNodeEntity.setChildren(Collections.singleton(childrenNodeEntity));

        final HierarchicalNodeToNodeConverter hierarchicalNodeToNodeConverter =
                new HierarchicalNodeToNodeConverter(new HierarchicalNoteToNoteConverter(new PersistedTagTagEntityConverter()));

        final HierarchicalNodeDTO rootNodeDTO = hierarchicalNodeToNodeConverter.convertToDto(rootNodeEntity);
        Assert.assertEquals(ROOT_NODE_NAME, rootNodeDTO.getName());

        final List<HierarchicalNoteDTO> dtoNotes = rootNodeDTO.getNotes();
        Assert.assertEquals(1L, dtoNotes.size());

        final HierarchicalNoteDTO rootNoteDTO = dtoNotes.get(0);
        checkNoteDTO(rootNoteDTO, ROOT_NOTE_NAME, ROOT_NOTE_TEXT, 1L);

        final List<HierarchicalNodeDTO> leafNodes = rootNodeDTO.getChildren();
        Assert.assertEquals(1L, leafNodes.size());

        final HierarchicalNodeDTO leafNodeDTO = leafNodes.get(0);
        Assert.assertEquals(LEAF_NODE_NAME, leafNodeDTO.getName());

        final List<HierarchicalNoteDTO> leafNotes = leafNodeDTO.getNotes();
        Assert.assertEquals(1L, leafNotes.size());

        final HierarchicalNoteDTO leafNoteDTO = leafNotes.get(0);
        checkNoteDTO(leafNoteDTO, LEAF_NOTE_NAME, LEAF_NOTE_TEXT, 2L);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void convertToDbo() {
        hierarchicalNodeToNodeConverter.convertToDbo(new HierarchicalNodeDTO());
    }

    private void checkNoteDTO(HierarchicalNoteDTO rootNoteDto, String noteName, String noteText, long id) {
        Assert.assertEquals(noteName, rootNoteDto.getName());
        Assert.assertEquals(noteText, rootNoteDto.getText());
        Assert.assertEquals(Long.valueOf(id), rootNoteDto.getId());
    }

    private NodeEntity createNodeEntity(String nodeName) {
        final NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setActive(true);
        nodeEntity.setName(nodeName);
        return nodeEntity;
    }

    private NoteEntity createNoteEntity(long id, String noteText, String noteName) {
        final NoteEntity noteEntity = new NoteEntity();

        noteEntity.setActive(true);
        noteEntity.setId(id);
        noteEntity.setText(noteText);
        noteEntity.setName(noteName);

        return noteEntity;
    }
}