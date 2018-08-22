package by.demianbel.notes.dto.node;

import by.demianbel.notes.dto.HasIdAndNameDTO;
import by.demianbel.notes.dto.note.HierarchicalNoteDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class HierarchicalNodeDTO extends HasIdAndNameDTO {

    private HierarchicalNodeDTO parent;

    @EqualsAndHashCode.Exclude
    private List<HierarchicalNoteDTO> notes;

    @EqualsAndHashCode.Exclude
    private List<HierarchicalNodeDTO> children;
}
