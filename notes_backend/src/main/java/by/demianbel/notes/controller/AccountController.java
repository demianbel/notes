package by.demianbel.notes.controller;

import by.demianbel.notes.dto.user.PersistedUserDTO;
import by.demianbel.notes.dto.user.UserToRestoreDTO;
import by.demianbel.notes.dto.user.UserToSaveDTO;
import by.demianbel.notes.exception.UserNotFoundException;
import by.demianbel.notes.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("notes/rest/account")
public class AccountController {

    private final AccountService accountService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public PersistedUserDTO signUp(@RequestBody final UserToSaveDTO userToSaveDTO) {
        return accountService.signUp(userToSaveDTO);
    }

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public PersistedUserDTO restore(@RequestBody final UserToRestoreDTO userToRestoreDTO) throws UserNotFoundException {
        return accountService.restore(userToRestoreDTO);
    }

}
