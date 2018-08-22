package by.demianbel.notes.dto.hierarhical;

import by.demianbel.notes.dto.node.HierarchicalNodeDTO;
import by.demianbel.notes.dto.note.PersistedNoteDTO;
import lombok.Data;

import java.util.List;

@Data
public class HierarchicalDataResponse {
    private List<HierarchicalNodeDTO> nodes;
    private List<PersistedNoteDTO> notes;
}
