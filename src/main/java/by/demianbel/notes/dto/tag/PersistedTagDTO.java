package by.demianbel.notes.dto.tag;

import by.demianbel.notes.dto.HasIdAndNameDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersistedTagDTO extends HasIdAndNameDTO {
    private Long id;
    private String name;
}
