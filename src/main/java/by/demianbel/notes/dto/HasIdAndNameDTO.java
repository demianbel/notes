package by.demianbel.notes.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HasIdAndNameDTO extends HasIdDTO {
    public HasIdAndNameDTO() {
    }

    public HasIdAndNameDTO(final String name) {
        this.name = name;
    }

    private String name;
}
