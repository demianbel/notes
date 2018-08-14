package by.demianbel.notes.dto.note;

import by.demianbel.notes.dto.HasIdAndNameDTO;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import by.demianbel.notes.dto.tag.PersistedTagDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersistedNoteDTO extends HasIdAndNameDTO {

    private Long id;

    @EqualsAndHashCode.Exclude
    private String name;

    @EqualsAndHashCode.Exclude
    private String text;

    @EqualsAndHashCode.Exclude
    private List<PersistedTagDTO> tags;

    @EqualsAndHashCode.Exclude
    private PersistedNodeDTO node;

}
