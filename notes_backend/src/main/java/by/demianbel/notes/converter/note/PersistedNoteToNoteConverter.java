package by.demianbel.notes.converter.note;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.converter.node.PersistedNodeToNodeConverter;
import by.demianbel.notes.converter.tag.PersistedTagTagEntityConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import by.demianbel.notes.dto.tag.PersistedTagDTO;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.repository.TagRepository;
import by.demianbel.notes.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PersistedNoteToNoteConverter implements DtoToDboConverter<PersistedNoteDTO, NoteEntity> {

    private final PersistedTagTagEntityConverter persistedTagTagEntityConverter;
    private final PersistedNodeToNodeConverter persistedNodeToNodeConverter;

    @Override
    public PersistedNoteDTO convertToDto(final NoteEntity noteEntity) {
        final PersistedNoteDTO persistedNoteDTO = new PersistedNoteDTO();

        persistedNoteDTO.setName(noteEntity.getName());
        persistedNoteDTO.setText(noteEntity.getText());
        persistedNoteDTO.setId(noteEntity.getId());

        final NodeEntity node = noteEntity.getNode();
        if (node != null) {
            persistedNoteDTO.setNode(persistedNodeToNodeConverter.convertToDto(node));
        }

        final Set<TagEntity> tags = noteEntity.getTags();
        if (tags != null && !tags.isEmpty()) {
            final List<PersistedTagDTO> tagDTOs =
                    tags.stream().map(persistedTagTagEntityConverter::convertToDto).collect(Collectors.toList());
            persistedNoteDTO.setTags(tagDTOs);
        }

        return persistedNoteDTO;
    }

    @Override
    public NoteEntity convertToDbo(final PersistedNoteDTO persistedNoteDTO) {
        throw new UnsupportedOperationException("We shouldn't convert persisted dto to dbo");
    }
}
