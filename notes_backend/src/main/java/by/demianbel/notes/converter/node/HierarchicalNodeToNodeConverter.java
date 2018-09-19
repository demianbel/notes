package by.demianbel.notes.converter.node;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.converter.note.HierarchicalNoteToNoteConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.note.HierarchicalNoteDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HierarchicalNodeToNodeConverter implements DtoToDboConverter<HierarchicalNodeDTO, NodeEntity> {

    private final HierarchicalNoteToNoteConverter hierarchicalNoteToNoteConverter;

    @Override
    public HierarchicalNodeDTO convertToDto(final NodeEntity nodeEntity) {
        final HierarchicalNodeDTO hierarchicalNodeDTO = new HierarchicalNodeDTO();
        BeanUtils.copyProperties(nodeEntity, hierarchicalNodeDTO);

        final List<HierarchicalNodeDTO> children =
                Optional.ofNullable(nodeEntity.getChildren()).orElse(Collections.emptySet()).stream()
                        .filter(NodeEntity::isActive)
                        .map(this::convertToDto).collect(Collectors.toList());
        hierarchicalNodeDTO.setChildren(children);

        final List<HierarchicalNoteDTO> notes =
                Optional.ofNullable(nodeEntity.getNotes()).orElse(Collections.emptySet()).stream()
                        .filter(NoteEntity::isActive)
                        .map(hierarchicalNoteToNoteConverter::convertToDto).collect(
                        Collectors.toList());
        hierarchicalNodeDTO.setNotes(notes);
        return hierarchicalNodeDTO;
    }

    @Override
    public NodeEntity convertToDbo(final HierarchicalNodeDTO hierarchicalNodeDTO) {
        throw new UnsupportedOperationException("We shouldn't convert hierarchical node to entity.");
    }
}
