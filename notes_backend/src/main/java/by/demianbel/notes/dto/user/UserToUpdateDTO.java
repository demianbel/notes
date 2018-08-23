package by.demianbel.notes.dto.user;

import by.demianbel.notes.dto.HasIdAndNameDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserToUpdateDTO extends HasIdAndNameDTO {
    private String password;
    private String email;
}
