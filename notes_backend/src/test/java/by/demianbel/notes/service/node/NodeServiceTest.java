package by.demianbel.notes.service.node;

import by.demianbel.notes.converter.node.NodeToSaveNodeConverter;
import by.demianbel.notes.converter.node.PersistedNodeToNodeConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dto.node.NodeToSaveDTO;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.exception.NodeNotFoundException;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringRunner.class)
public class NodeServiceTest {

    private static final String NODE_NAME = "node name";

    @TestConfiguration
    public static class NodeServiceTestConfiguration {
        @Bean
        public NodeService nodeService(final NodeRepository nodeRepository,
                                       final UserService userService,
                                       final PersistedNodeToNodeConverter persistedNodeToNodeConverter,
                                       final NodeToSaveNodeConverter nodeToSaveNodeConverter) {
            return new NodeService(nodeRepository, userService, persistedNodeToNodeConverter, nodeToSaveNodeConverter);
        }
    }

    @MockBean
    private NodeRepository nodeRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private PersistedNodeToNodeConverter persistedNodeToNodeConverter;
    @MockBean
    private NodeToSaveNodeConverter nodeToSaveNodeConverter;

    @Autowired
    private NodeService nodeService;

    @Test
    public void findNodeById() {
        final NodeEntity nodeToFind = new NodeEntity();
        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(1L)))
                .thenReturn(Optional.of(nodeToFind));
        final PersistedNodeDTO convertedNodeToFind = new PersistedNodeDTO();
        Mockito.when(persistedNodeToNodeConverter.convertToDto(nodeToFind)).thenReturn(convertedNodeToFind);
        final PersistedNodeDTO nodeById = nodeService.findNodeById(1L);
        Assert.assertNotNull(nodeById);
        Assert.assertEquals(convertedNodeToFind, nodeById);
    }

    @Test(expected = NodeNotFoundException.class)
    public void findNodeByIdEmpty() {
        nodeService.findNodeById(1L);
    }

    @Test
    public void createNode() {
        final NodeToSaveDTO nodeToSaveDTO = new NodeToSaveDTO();

        final NodeEntity nodeEntity = new NodeEntity();
        Mockito.when(nodeToSaveNodeConverter.convertToDbo(nodeToSaveDTO)).thenReturn(nodeEntity);
        Mockito.when(nodeRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        final PersistedNodeDTO persistedNodeDTO = new PersistedNodeDTO();
        Mockito.when(persistedNodeToNodeConverter.convertToDto(nodeEntity)).thenReturn(persistedNodeDTO);

        final PersistedNodeDTO resultNodeDTO = nodeService.createNode(nodeToSaveDTO);
        Assert.assertEquals(persistedNodeDTO, resultNodeDTO);
    }

    @Test
    public void deactivateNode() {
        final long nodeId = 1L;
        final NodeEntity nodeToDeactivate = new NodeEntity();
        nodeToDeactivate.setActive(true);
        final NoteEntity noteToDeactivate = new NoteEntity();
        noteToDeactivate.setActive(true);
        nodeToDeactivate.setNotes(Set.of(noteToDeactivate));
        final NodeEntity childNodeToDeactivate = new NodeEntity();
        childNodeToDeactivate.setActive(true);
        nodeToDeactivate.setChildren(Set.of(childNodeToDeactivate));

        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(1L)))
                .thenReturn(Optional.of(nodeToDeactivate));

        Mockito.when(nodeRepository.save(nodeToDeactivate)).then(AdditionalAnswers.returnsFirstArg());

        final PersistedNodeDTO nodeDTO = new PersistedNodeDTO();
        Mockito.when(persistedNodeToNodeConverter.convertToDto(nodeToDeactivate)).thenReturn(nodeDTO);
        final PersistedNodeDTO persistedNodeDTO = nodeService.deactivateNode(nodeId);

        Assert.assertEquals(nodeDTO, persistedNodeDTO);
        Assert.assertFalse(nodeToDeactivate.isActive());
        Assert.assertFalse(noteToDeactivate.isActive());
        Assert.assertFalse(childNodeToDeactivate.isActive());

    }

    @Test
    public void changeNodeName() {

        final long nodeId = 1L;
        final NodeEntity foundNode = new NodeEntity();
        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(1L)))
                .thenReturn(Optional.of(foundNode));

        Mockito.when(nodeRepository.save(foundNode)).then(AdditionalAnswers.returnsFirstArg());

        final PersistedNodeDTO nodeDTO = new PersistedNodeDTO();
        Mockito.when(persistedNodeToNodeConverter.convertToDto(foundNode)).thenReturn(nodeDTO);

        final PersistedNodeDTO persistedNodeDTO = nodeService.changeNodeName(nodeId, NODE_NAME);

        Assert.assertEquals(nodeDTO, persistedNodeDTO);
        Assert.assertEquals(NODE_NAME, foundNode.getName());

    }

    @Test
    public void findNodeByName() {
        final NodeEntity equalNodeEntity = new NodeEntity();
        equalNodeEntity.setId(1L);
        Mockito.when(
                nodeRepository.findFirstByUserAndActiveIsTrueAndName(Mockito.any(), Mockito.eq(NODE_NAME)))
                .thenReturn(Optional.of(equalNodeEntity));

        final NodeEntity likeNodeEntity = new NodeEntity();
        likeNodeEntity.setId(2L);
        Mockito.when(nodeRepository.findAllByUserAndActiveIsTrueAndNameLike(Mockito.any(),
                                                                            Mockito.eq("%" + NODE_NAME + "%")))
                .thenReturn(List.of(likeNodeEntity));

        final PersistedNodeDTO equalPersistedNodeDTO = new PersistedNodeDTO();
        equalPersistedNodeDTO.setId(1L);
        Mockito.when(persistedNodeToNodeConverter.convertToDto(equalNodeEntity)).thenReturn(equalPersistedNodeDTO);

        final PersistedNodeDTO likePersistedNodeDTO = new PersistedNodeDTO();
        likePersistedNodeDTO.setId(2L);
        Mockito.when(persistedNodeToNodeConverter.convertToDto(likeNodeEntity)).thenReturn(likePersistedNodeDTO);

        final List<PersistedNodeDTO> nodeByName = nodeService.findNodeByName(NODE_NAME);

        Assert.assertEquals(2, nodeByName.size());
        Assert.assertEquals(Long.valueOf(1L), nodeByName.get(0).getId());
        Assert.assertEquals(Long.valueOf(2L), nodeByName.get(1).getId());
    }
}