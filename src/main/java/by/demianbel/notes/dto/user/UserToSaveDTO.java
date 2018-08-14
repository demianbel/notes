package by.demianbel.notes.dto.user;

import lombok.Data;

@Data
public class UserToSaveDTO {

    private String name;
    private String password;
    private String email;
}
