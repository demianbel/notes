package by.demianbel.notes.dto.user;

import by.demianbel.notes.dto.HasIdAndNameDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersistedUserDTO extends HasIdAndNameDTO {

    private String email;
    private List<String> roles;

}
