package by.demianbel.notes.converter.note;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.converter.tag.PersistedTagTagEntityConverter;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dto.note.HierarchicalNoteDTO;
import by.demianbel.notes.dto.tag.PersistedTagDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HierarchicalNoteToNoteConverter implements DtoToDboConverter<HierarchicalNoteDTO, NoteEntity> {

    private final PersistedTagTagEntityConverter persistedTagTagEntityConverter;

    @Override
    public HierarchicalNoteDTO convertToDto(final NoteEntity noteEntity) {
        final HierarchicalNoteDTO hierarchicalNoteDTO = new HierarchicalNoteDTO();

        hierarchicalNoteDTO.setName(noteEntity.getName());
        hierarchicalNoteDTO.setText(noteEntity.getText());
        hierarchicalNoteDTO.setId(noteEntity.getId());

        final Set<TagEntity> tags = noteEntity.getTags();
        if (tags != null && !tags.isEmpty()) {
            final List<PersistedTagDTO> tagDTOs =
                    tags.stream().map(persistedTagTagEntityConverter::convertToDto).collect(Collectors.toList());
            hierarchicalNoteDTO.setTags(tagDTOs);
        }

        return hierarchicalNoteDTO;
    }

    @Override
    public NoteEntity convertToDbo(final HierarchicalNoteDTO hierarchicalNoteDTO) {
        throw new UnsupportedOperationException("We shouldn't convert hierarhical DTO to entity. Find by id instead.");
    }
}
