package by.demianbel.notes.converter.note;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.note.NoteToSaveDTO;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.TagRepository;
import by.demianbel.notes.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class NoteSavingToNoteConverter implements DtoToDboConverter<NoteToSaveDTO, NoteEntity> {

    private final TagRepository tagRepository;
    private final NodeRepository nodeRepository;
    private final UserService userService;

    @Override
    public NoteToSaveDTO convertToDto(final NoteEntity noteEntity) {
        final NoteToSaveDTO noteToSaveDto = new NoteToSaveDTO();

        noteToSaveDto.setName(noteEntity.getName());
        noteToSaveDto.setText(noteEntity.getText());

        return noteToSaveDto;
    }

    @Override
    public NoteEntity convertToDbo(final NoteToSaveDTO noteToSaveDto) {
        final NoteEntity noteEntity = new NoteEntity();
        final UserEntity currentUser = userService.getCurrentUser();
        final Long nodeId = noteToSaveDto.getNodeId();
        if (nodeId != null) {
            final NodeEntity node = nodeRepository.findByUserAndIdAndActive(currentUser, nodeId, true)
                    .orElseThrow(() -> new RuntimeException("Node with id = '" + nodeId + "' doesn't exist."));
            noteEntity.setNode(node);
        }
        final List<Long> tagIds = noteToSaveDto.getTagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            final List<TagEntity> tags = tagRepository.findByUserAndActiveAndIdIn(currentUser, true, tagIds);
            if (tags.size() == tagIds.size()) {
                noteEntity.setTags(new HashSet<>(tags));
            } else {
                throw new RuntimeException("Some tag ids has not found.");
            }
        }
        noteEntity.setName(noteToSaveDto.getName());
        noteEntity.setText(noteToSaveDto.getText());
        noteEntity.setActive(true);

        return noteEntity;
    }
}
