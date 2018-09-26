package by.demianbel.notes.service.node;

import by.demianbel.notes.converter.note.PersistedNoteToNoteConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.NoteRepository;
import by.demianbel.notes.repository.UserRepository;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringRunner.class)
public class NodeSharingServiceTest {

    @TestConfiguration
    public static class NodeSharingServiceTestConfiguration {
        @Bean
        public NodeSharingService nodeSharingService(final NodeRepository nodeRepository,
                                                     final NoteRepository noteRepository,
                                                     final UserRepository userRepository,
                                                     final UserService userService,
                                                     final PersistedNoteToNoteConverter persistedNoteToNoteConverter) {
            return new NodeSharingService(nodeRepository, noteRepository, userRepository, userService,
                                          persistedNoteToNoteConverter);
        }
    }

    @MockBean
    private NodeRepository nodeRepository;
    @MockBean
    private NoteRepository noteRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private PersistedNoteToNoteConverter persistedNoteToNoteConverter;

    @Autowired
    private NodeSharingService nodeSharingService;

    @Test
    public void shareNodeWithUser() {

        final long nodeId = 1L;
        final long userId = 10L;

        final NodeEntity nodeToShare = new NodeEntity();
        final NoteEntity noteToShare = new NoteEntity();
        nodeToShare.setNotes(Set.of(noteToShare));
        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(nodeId))).thenReturn(
                Optional.of(nodeToShare));

        final UserEntity userToShare = new UserEntity();
        Mockito.when(userRepository.findByIdAndActiveIsTrue(userId)).thenReturn(Optional.of(userToShare));

        nodeSharingService.shareNodeWithUser(userId, nodeId);

        final Set<UserEntity> usersToShare = noteToShare.getUsersToShare();

        Assert.assertEquals(1, usersToShare.size());
        Assert.assertTrue(usersToShare.contains(userToShare));


    }

    @Test
    public void unshareNodeWithUser() {
        final long nodeId = 1L;
        final long userId = 10L;

        final NodeEntity nodeToShare = new NodeEntity();
        final NoteEntity noteToShare = new NoteEntity();
        final UserEntity userToShare = new UserEntity();
        userToShare.setId(userId);
        final HashSet<UserEntity> usersToShare = new HashSet<>();
        noteToShare.setUsersToShare(usersToShare);
        nodeToShare.setNotes(Set.of(noteToShare));

        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(nodeId))).thenReturn(
                Optional.of(nodeToShare));

        nodeSharingService.unshareNodeWithUser(userId, nodeId);

        Assert.assertTrue(usersToShare.isEmpty());
    }
}