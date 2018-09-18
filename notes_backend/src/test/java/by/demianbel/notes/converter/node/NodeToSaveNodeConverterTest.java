package by.demianbel.notes.converter.node;

import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.node.NodeToSaveDTO;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class NodeToSaveNodeConverterTest {

    private static final String TEST_NODE_NAME = "test node name";
    private static final String PARENT_NODE_NAME = "parent node name";

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserService userService;

    private NodeToSaveNodeConverter converter;

    @Before
    public void setUp() {
        converter = new NodeToSaveNodeConverter(nodeRepository, userService);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void convertToDto() {
        converter.convertToDto(null);
    }

    @Test
    public void convertToDbo() {

        final NodeEntity parentNode = new NodeEntity();
        parentNode.setId(1L);
        parentNode.setActive(true);
        parentNode.setName(PARENT_NODE_NAME);
        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(1L))).thenReturn(Optional.of(parentNode));
        Mockito.when(userService.getCurrentUser()).thenReturn(new UserEntity());

        final NodeToSaveDTO nodeToSaveDTO = new NodeToSaveDTO();
        nodeToSaveDTO.setName(TEST_NODE_NAME);
        nodeToSaveDTO.setParentNodeId(1L);

        final NodeEntity nodeEntity = converter.convertToDbo(nodeToSaveDTO);

        Assert.assertEquals(TEST_NODE_NAME, nodeEntity.getName());
        final NodeEntity convertedParentNode = nodeEntity.getParentNode();
        Assert.assertNotNull(convertedParentNode);
        Assert.assertEquals(1L, convertedParentNode.getId());
        Assert.assertEquals(PARENT_NODE_NAME, convertedParentNode.getName());
    }
}