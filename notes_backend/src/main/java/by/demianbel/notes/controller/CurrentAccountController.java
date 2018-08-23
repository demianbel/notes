package by.demianbel.notes.controller;

import by.demianbel.notes.dto.user.PersistedUserDTO;
import by.demianbel.notes.dto.user.UserToUpdateDTO;
import by.demianbel.notes.service.AccountService;
import by.demianbel.notes.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("notes/rest/account")
@PreAuthorize("hasAnyAuthority('user','admin')")
public class CurrentAccountController {

    private final AccountService accountService;
    private final UserService userService;

    @RequestMapping(value = "/deactivate", method = RequestMethod.DELETE)
    public PersistedUserDTO deactivateCurrentAccount() {
        return accountService.deactivateCurrentAccount();
    }


    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public PersistedUserDTO updateUser(@RequestBody final UserToUpdateDTO userToUpdateDTO) {
        return userService.updateUser(userToUpdateDTO);
    }
}
