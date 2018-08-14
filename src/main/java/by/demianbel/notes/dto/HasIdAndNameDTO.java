package by.demianbel.notes.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HasIdAndNameDTO extends HasIdDTO {
    private String name;
}
