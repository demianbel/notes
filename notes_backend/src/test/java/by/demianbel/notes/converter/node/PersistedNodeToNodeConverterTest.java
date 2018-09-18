package by.demianbel.notes.converter.node;

import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PersistedNodeToNodeConverterTest {

    private static final String TEST_NODE_NAME = "test node name";

    private PersistedNodeToNodeConverter converter;

    @Before
    public void setUp() {
        converter = new PersistedNodeToNodeConverter();
    }

    @Test
    public void convertToDto() {
        final NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setId(1L);
        nodeEntity.setName(TEST_NODE_NAME);

        final NodeEntity parentNode = new NodeEntity();
        parentNode.setId(2L);
        nodeEntity.setParentNode(parentNode);

        final PersistedNodeDTO persistedNodeDTO = converter.convertToDto(nodeEntity);
        Assert.assertEquals(Long.valueOf(1L), persistedNodeDTO.getId());
        Assert.assertEquals(TEST_NODE_NAME, persistedNodeDTO.getName());
        Assert.assertEquals(Long.valueOf(2L), persistedNodeDTO.getParentNodeId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void convertToDbo() {
        converter.convertToDbo(null);
    }
}