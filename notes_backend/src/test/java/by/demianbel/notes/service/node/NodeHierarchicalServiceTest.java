package by.demianbel.notes.service.node;

import by.demianbel.notes.converter.node.HierarchicalNodeToNodeConverter;
import by.demianbel.notes.converter.note.HierarchicalNoteToNoteConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.note.HierarchicalNoteDTO;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.NoteRepository;
import by.demianbel.notes.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class NodeHierarchicalServiceTest {

    private static final String NOTE_NAME = "note name";
    private static final String NOTE_TEXT = "note text";
    private static final String USER_NAME = "user name";
    private static final String NOTE_WITH_NODE_NAME = "note with node name";
    private static final String NOTE_WITH_NODE_TEXT = "note with node text";

    @TestConfiguration
    public static class NodeHierarchicalServiceTestConfiguration {
        @Bean
        public NodeHierarchicalService nodeHierarchicalService(final NodeRepository nodeRepository,
                                                               final NoteRepository noteRepository,
                                                               final UserService userService,
                                                               final HierarchicalNodeToNodeConverter hierarchicalNodeToNodeConverter,
                                                               final HierarchicalNoteToNoteConverter hierarchicalNoteToNoteConverter) {
            return new NodeHierarchicalService(nodeRepository, noteRepository, userService,
                                               hierarchicalNodeToNodeConverter, hierarchicalNoteToNoteConverter);
        }
    }

    @MockBean
    private NodeRepository nodeRepository;
    @MockBean
    private NoteRepository noteRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private HierarchicalNodeToNodeConverter hierarchicalNodeToNodeConverter;
    @MockBean
    private HierarchicalNoteToNoteConverter hierarchicalNoteToNoteConverter;

    @Autowired
    private NodeHierarchicalService nodeHierarchicalService;

    @Test
    public void getAllNodesHierarchical() {
        Mockito.when(nodeRepository.findByUserAndActiveIsTrueAndParentNodeIsNull(Mockito.any()))
                .thenReturn(List.of(new NodeEntity(), new NodeEntity(), new NodeEntity()));

        Mockito.when(hierarchicalNodeToNodeConverter.convertToDto(Mockito.any())).thenReturn(new HierarchicalNodeDTO());

        final List<HierarchicalNodeDTO> allNodesHierarchical = nodeHierarchicalService.getAllNodesHierarchical();

        Assert.assertNotNull(allNodesHierarchical);
        Assert.assertEquals(3, allNodesHierarchical.size());

        Mockito.verify(nodeRepository, Mockito.times(1)).findByUserAndActiveIsTrueAndParentNodeIsNull(Mockito.any());
        Mockito.verify(hierarchicalNodeToNodeConverter, Mockito.times(3)).convertToDto(Mockito.any());
    }

    @Test
    public void getHierarchicalNode() {

        final long nodeId = 1L;

        final NodeEntity node = new NodeEntity();
        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(nodeId))).thenReturn(
                Optional.of(node));

        final HierarchicalNodeDTO nodeToReturn = new HierarchicalNodeDTO();
        Mockito.when(hierarchicalNodeToNodeConverter.convertToDto(node)).thenReturn(nodeToReturn);

        final HierarchicalNodeDTO hierarchicalNode = nodeHierarchicalService.getHierarchicalNode(nodeId);

        Assert.assertNotNull(hierarchicalNode);
        Assert.assertEquals(nodeToReturn, hierarchicalNode);


    }

    @Test
    public void getSharedNodeHierarchical() {
        final NoteEntity noteWithoutNode = new NoteEntity();

        final UserEntity user = new UserEntity();
        user.setName(USER_NAME);
        noteWithoutNode.setUser(user);
        noteWithoutNode.setId(1L);
        noteWithoutNode.setName(NOTE_NAME);
        noteWithoutNode.setText(NOTE_TEXT);

        Mockito.when(noteRepository.findAllByUsersToShareContainsAndNodeIsNullAndActiveIsTrue(Mockito.any()))
                .thenReturn(List.of(noteWithoutNode));

        final NoteEntity noteWithNode = new NoteEntity();
        noteWithNode.setUser(user);
        noteWithNode.setId(2L);
        noteWithNode.setName(NOTE_WITH_NODE_NAME);
        noteWithNode.setText(NOTE_WITH_NODE_TEXT);
        final NodeEntity node = new NodeEntity();
        node.setId(2L);
        node.setName("node name");
        node.setActive(true);
        noteWithNode.setNode(node);
        Mockito.when(noteRepository.findAllByUsersToShareContainsAndNodeIsNotNullAndActiveIsTrue(Mockito.any()))
                .thenReturn(List.of(noteWithNode));

        Mockito.when(hierarchicalNoteToNoteConverter.convertToDto(noteWithoutNode))
                .thenReturn(new HierarchicalNoteDTO());
        Mockito.when(hierarchicalNoteToNoteConverter.convertToDto(noteWithNode))
                .thenReturn(new HierarchicalNoteDTO());

        final List<HierarchicalNodeDTO> nodes = nodeHierarchicalService.getSharedNodeHierarchical();
    }
}