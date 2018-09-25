package by.demianbel.notes.service;

import by.demianbel.notes.converter.note.NoteToSaveNoteConverter;
import by.demianbel.notes.converter.note.PersistedNoteToNoteConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.hierarhical.HierarchicalDataResponse;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.note.NoteToSaveDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.exception.NoteNotFoundException;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.NoteRepository;
import by.demianbel.notes.repository.TagRepository;
import by.demianbel.notes.repository.UserRepository;
import by.demianbel.notes.service.node.NodeHierarchicalService;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringRunner.class)
public class NoteServiceTest {

    public static final String NOTE_TEXT = "note text";
    public static final String NOTE_NAME = "note name";

    @TestConfiguration
    public static class NoteServiceTestConfiguration {
        @Bean
        public NoteService noteService(final NoteRepository noteRepository,
                                       final TagRepository tagRepository,
                                       final UserRepository userRepository,
                                       final NodeRepository nodeRepository,
                                       final NoteToSaveNoteConverter noteToSaveNoteConverter,
                                       final PersistedNoteToNoteConverter persistedNoteToNoteConverter,
                                       final UserService userService,
                                       final NodeHierarchicalService nodeHierarchicalService) {
            return new NoteService(noteRepository, tagRepository, userRepository, nodeRepository,
                                   noteToSaveNoteConverter, persistedNoteToNoteConverter, userService,
                                   nodeHierarchicalService);
        }
    }

    @MockBean
    private NoteRepository noteRepository;
    @MockBean
    private TagRepository tagRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private NodeRepository nodeRepository;
    @MockBean
    private NoteToSaveNoteConverter noteToSaveNoteConverter;
    @MockBean
    private PersistedNoteToNoteConverter persistedNoteToNoteConverter;
    @MockBean
    private UserService userService;
    @MockBean
    private NodeHierarchicalService nodeHierarchicalService;

    @Autowired
    private NoteService noteService;

    @Test
    public void createNote() {
        final NoteToSaveDTO noteDto = new NoteToSaveDTO();

        final NoteEntity note = new NoteEntity();
        note.setText(NOTE_TEXT);
        note.setName(NOTE_NAME);
        note.setActive(true);
        Mockito.when(noteToSaveNoteConverter.convertToDbo(noteDto)).thenReturn(note);
        final UserEntity user = new UserEntity();
        Mockito.when(userService.getCurrentUser()).thenReturn(user);
        Mockito.when(noteRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        noteService.createNote(noteDto);

        Assert.assertNotNull(note);
        Assert.assertEquals(NOTE_TEXT, note.getText());
        Assert.assertEquals(NOTE_NAME, note.getName());
        Assert.assertEquals(user, note.getUser());
        Assert.assertTrue(note.isActive());
    }

    @Test
    public void getNote() {

        final long noteId = 1L;
        final UserEntity user = new UserEntity();
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        final NoteEntity foundNote = new NoteEntity();
        foundNote.setId(noteId);
        foundNote.setName(NOTE_NAME);
        foundNote.setText(NOTE_TEXT);

        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.eq(user), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(foundNote));

        final PersistedNoteDTO persistedNoteDTO = new PersistedNoteDTO();
        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.eq(foundNote))).thenReturn(
                persistedNoteDTO);

        final PersistedNoteDTO note = noteService.getNote(noteId);

        Assert.assertEquals(persistedNoteDTO, note);
    }

    @Test(expected = NoteNotFoundException.class)
    public void getNoteEmpty() {

        final long noteId = 1L;
        final UserEntity user = new UserEntity();

        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.eq(user), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.empty());

        noteService.getNote(noteId);

    }

    @Test
    public void deactivateNote() {
        final long noteId = 1L;
        final UserEntity user = new UserEntity();
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        final NoteEntity foundNote = new NoteEntity();
        foundNote.setId(noteId);
        foundNote.setActive(true);

        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.eq(user), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(foundNote));

        final PersistedNoteDTO persistedNoteDTO = new PersistedNoteDTO();
        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.eq(foundNote))).thenReturn(
                persistedNoteDTO);

        noteService.deactivateNote(noteId);

        Assert.assertFalse(foundNote.isActive());


    }

    @Test
    public void getAllNotes() {
        Mockito.when(noteRepository.findByUserAndActive(Mockito.any(), Mockito.eq(true)))
                .thenReturn(List.of(new NoteEntity(), new NoteEntity(), new NoteEntity()));
        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.any())).thenReturn(new PersistedNoteDTO());
        final List<PersistedNoteDTO> allNotes = noteService.getAllNotes();
        Assert.assertEquals(3, allNotes.size());
        Mockito.verify(noteRepository, Mockito.times(1)).findByUserAndActive(Mockito.any(), Mockito.eq(true));
        Mockito.verify(persistedNoteToNoteConverter, Mockito.times(3)).convertToDto(Mockito.any());
    }

    @Test
    public void getAllNotesHierarchical() {
        Mockito.when(noteRepository.findByUserAndActiveTrueAndNodeIsNull(Mockito.any()))
                .thenReturn(List.of(new NoteEntity(), new NoteEntity(), new NoteEntity()));
        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.any())).thenReturn(new PersistedNoteDTO());
        final ArrayList<HierarchicalNodeDTO> hierarchicalNodeDTOS = new ArrayList<>();
        Mockito.when(nodeHierarchicalService.getAllNodesHierarchical()).thenReturn(hierarchicalNodeDTOS);
        final ArrayList<HierarchicalNodeDTO> sharedNodeDTOS = new ArrayList<>();
        Mockito.when(nodeHierarchicalService.getSharedNodeHierarchical()).thenReturn(sharedNodeDTOS);
        final HierarchicalDataResponse allNotesHierarchical = noteService.getAllNotesHierarchical();
        final List<PersistedNoteDTO> notes = allNotesHierarchical.getNotes();
        Assert.assertEquals(3, notes.size());
        Assert.assertSame(hierarchicalNodeDTOS, allNotesHierarchical.getNodes());
        Assert.assertSame(sharedNodeDTOS, allNotesHierarchical.getSharedNodes());
    }

    @Test
    public void getAllNotesByTag() {
        final long tagId = 1L;
        final TagEntity foundTag = new TagEntity();
        final NoteEntity activeNote = new NoteEntity();
        activeNote.setId(1L);
        activeNote.setActive(true);

        foundTag.setNotes(Set.of(new NoteEntity(), activeNote));

        Mockito.when(tagRepository.findByUserAndIdAndActive(Mockito.any(), Mockito.eq(tagId), Mockito.eq(true)))
                .thenReturn(Optional.of(foundTag));
        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.any())).thenReturn(new PersistedNoteDTO());

        final List<PersistedNoteDTO> allNotes = noteService.getAllNotesByTag(tagId);
        Assert.assertEquals(1, allNotes.size());
        Mockito.verify(tagRepository, Mockito.times(1))
                .findByUserAndIdAndActive(Mockito.any(), Mockito.eq(tagId), Mockito.eq(true));
        Mockito.verify(persistedNoteToNoteConverter, Mockito.times(1)).convertToDto(Mockito.any());
    }


    @Test
    public void getAllNotesByNode() {
        final long nodeId = 1L;
        final NodeEntity nodeEntity = new NodeEntity();

        final NoteEntity activeNote = new NoteEntity();
        activeNote.setId(1L);
        activeNote.setActive(true);

        nodeEntity.setNotes(Set.of(new NoteEntity(), activeNote));

        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(nodeId)))
                .thenReturn(Optional.of(nodeEntity));

        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.any())).thenReturn(new PersistedNoteDTO());

        final List<PersistedNoteDTO> allNotes = noteService.getAllNotesByNode(nodeId);
        Assert.assertEquals(1, allNotes.size());

        Mockito.verify(nodeRepository, Mockito.times(1))
                .findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(nodeId));
        Mockito.verify(persistedNoteToNoteConverter, Mockito.times(1)).convertToDto(Mockito.any());
    }

    @Test
    public void findByName() {
        final NoteEntity equalNote = new NoteEntity();
        equalNote.setId(1L);
        Mockito.when(noteRepository.findFirstByUserAndActiveIsTrueAndName(Mockito.any(), Mockito.eq(NOTE_NAME)))
                .thenReturn(Optional.of(equalNote));

        final NoteEntity similarNote = new NoteEntity();
        similarNote.setId(2L);
        Mockito.when(noteRepository
                             .findAllByUserAndActiveIsTrueAndNameLike(Mockito.any(), Mockito.eq("%" + NOTE_NAME + "%")))
                .thenReturn(List.of(similarNote, equalNote));

        final PersistedNoteDTO similarDTO = new PersistedNoteDTO();
        similarDTO.setId(2L);
        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.eq(similarNote))).thenReturn(
                similarDTO);

        final PersistedNoteDTO equalDTO = new PersistedNoteDTO();
        equalDTO.setId(1L);
        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.eq(equalNote))).thenReturn(
                equalDTO);

        final List<PersistedNoteDTO> foundNotes = noteService.findByName(NOTE_NAME);

        Mockito.verify(persistedNoteToNoteConverter, Mockito.times(1)).convertToDto(Mockito.eq(equalNote));
        Mockito.verify(persistedNoteToNoteConverter, Mockito.times(1)).convertToDto(Mockito.eq(similarNote));
        Assert.assertEquals(2, foundNotes.size());
    }

    @Test
    public void findByText() {
        final NoteEntity equalNote = new NoteEntity();
        equalNote.setId(1L);
        Mockito.when(noteRepository.findFirstByUserAndActiveIsTrueAndText(Mockito.any(), Mockito.eq(NOTE_TEXT)))
                .thenReturn(Optional.of(equalNote));

        final NoteEntity similarNote = new NoteEntity();
        similarNote.setId(2L);
        Mockito.when(noteRepository
                             .findAllByUserAndActiveIsTrueAndTextLike(Mockito.any(), Mockito.eq("%" + NOTE_TEXT + "%")))
                .thenReturn(List.of(similarNote, equalNote));

        final PersistedNoteDTO similarDTO = new PersistedNoteDTO();
        similarDTO.setId(2L);
        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.eq(similarNote))).thenReturn(
                similarDTO);

        final PersistedNoteDTO equalDTO = new PersistedNoteDTO();
        equalDTO.setId(1L);
        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.eq(equalNote))).thenReturn(
                equalDTO);

        final List<PersistedNoteDTO> foundNotes = noteService.findByText(NOTE_TEXT);

        Mockito.verify(persistedNoteToNoteConverter, Mockito.times(1)).convertToDto(Mockito.eq(equalNote));
        Mockito.verify(persistedNoteToNoteConverter, Mockito.times(1)).convertToDto(Mockito.eq(similarNote));
        Assert.assertEquals(2, foundNotes.size());
    }

    @Test
    public void addTagToNote() {
        final TagEntity tag = new TagEntity();
        final long tagId = 1L;
        Mockito.when(tagRepository.findByUserAndIdAndActive(Mockito.any(), Mockito.eq(tagId), Mockito.eq(true)))
                .thenReturn(Optional.of(tag));

        final NoteEntity noteEntity = new NoteEntity();
        final long noteId = 2L;
        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.any(), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(noteEntity));

        Mockito.when(noteRepository.save(Mockito.eq(noteEntity))).then(AdditionalAnswers.returnsFirstArg());

        noteService.addTagToNote(tagId, noteId);

        final Set<TagEntity> tags = noteEntity.getTags();
        Assert.assertEquals(1, tags.size());
        Assert.assertTrue(tags.contains(tag));

    }

    @Test
    public void removeTagFromNote() {
        final long tagId = 1L;
        final long noteId = 2L;

        final NoteEntity note = new NoteEntity();
        final HashSet<TagEntity> tags = new HashSet<>();

        final TagEntity tagEntity = new TagEntity();
        tagEntity.setId(tagId);

        final TagEntity otherTagEntity = new TagEntity();
        otherTagEntity.setId(3L);

        tags.add(tagEntity);
        tags.add(otherTagEntity);

        note.setTags(tags);
        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.any(), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(note));

        noteService.removeTagFromNote(tagId, noteId);

        final Set<TagEntity> tagsAfterDelete = note.getTags();
        Assert.assertEquals(1, tagsAfterDelete.size());
        Assert.assertTrue(tagsAfterDelete.contains(otherTagEntity));
    }

    @Test
    public void changeName() {
        final NoteEntity note = new NoteEntity();
        final long noteId = 1L;
        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.any(), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(note));
        noteService.changeName(NOTE_NAME, noteId);
        Assert.assertEquals(NOTE_NAME, note.getName());
    }

    @Test
    public void changeText() {
        final NoteEntity note = new NoteEntity();
        final long noteId = 1L;
        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.any(), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(note));
        noteService.changeText(NOTE_TEXT, noteId);
        Assert.assertEquals(NOTE_TEXT, note.getText());
    }

    @Test
    public void moveNoteToNode() {
        final long nodeId = 1L;
        final long noteId = 2L;

        final NodeEntity node = new NodeEntity();
        Mockito.when(nodeRepository.findByUserAndIdAndActiveIsTrue(Mockito.any(), Mockito.eq(1L)))
                .thenReturn(Optional.of(node));

        final NoteEntity note = new NoteEntity();
        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.any(), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(note));

        noteService.moveNoteToNode(nodeId, noteId);
        Assert.assertEquals(node, note.getNode());
    }

    @Test
    public void detachNoteFromNode() {
        final long noteId = 2L;

        final NoteEntity note = new NoteEntity();
        note.setNode(new NodeEntity());
        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.any(), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(note));

        noteService.detachNoteFromNode(noteId);
        Assert.assertNull(note.getNode());
    }

    @Test
    public void shareNoteWithUser() {
        final long noteId = 2L;
        final long userId = 3L;

        final NoteEntity note = new NoteEntity();
        note.setNode(new NodeEntity());
        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.any(), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(note));

        final UserEntity user = new UserEntity();
        Mockito.when(userRepository.findByIdAndActiveIsTrue(Mockito.eq(userId)))
                .thenReturn(Optional.of(user));

        noteService.shareNoteWithUser(userId, noteId);

        final Set<UserEntity> usersToShare = note.getUsersToShare();
        Assert.assertEquals(1, usersToShare.size());
        Assert.assertTrue(usersToShare.contains(user));

    }

    @Test
    public void unshareNoteWithUser() {
        final long noteId = 2L;
        final long userId = 3L;

        final NoteEntity note = new NoteEntity();
        final HashSet<UserEntity> usersToShare = new HashSet<>();
        final UserEntity userToDelete = new UserEntity();
        userToDelete.setId(userId);

        final UserEntity otherUser = new UserEntity();
        otherUser.setId(1L);

        usersToShare.add(userToDelete);
        usersToShare.add(otherUser);

        note.setUsersToShare(usersToShare);
        note.setNode(new NodeEntity());

        Mockito.when(noteRepository.findByUserAndActiveAndId(Mockito.any(), Mockito.eq(true), Mockito.eq(noteId)))
                .thenReturn(Optional.of(note));

        noteService.unshareNoteWithUser(userId, noteId);

        Assert.assertEquals(1, usersToShare.size());
        Assert.assertTrue(usersToShare.contains(otherUser));

    }

    @Test
    public void getSharedNotes() {
        final UserEntity currentUser = new UserEntity();
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        Mockito.when(noteRepository.findAllByUsersToShareContainsAndActiveIsTrue(Mockito.eq(currentUser)))
                .thenReturn(List.of(new NoteEntity(), new NoteEntity(), new NoteEntity()));

        Mockito.when(persistedNoteToNoteConverter.convertToDto(Mockito.any())).thenReturn(new PersistedNoteDTO());

        final List<PersistedNoteDTO> allNotes = noteService.getSharedNotes();
        Assert.assertEquals(3, allNotes.size());

        Mockito.verify(noteRepository, Mockito.times(1))
                .findAllByUsersToShareContainsAndActiveIsTrue(Mockito.eq(currentUser));
        Mockito.verify(persistedNoteToNoteConverter, Mockito.times(3)).convertToDto(Mockito.any());
    }
}