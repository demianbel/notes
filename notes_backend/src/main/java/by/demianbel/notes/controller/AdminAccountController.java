package by.demianbel.notes.controller;

import by.demianbel.notes.dto.user.PersistedUserDTO;
import by.demianbel.notes.dto.user.UserToSaveDTO;
import by.demianbel.notes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("notes/rest/user")
@PreAuthorize("hasAuthority('admin')")
public class AdminAccountController {

    private static final String USER_ID_PARAMETER_NAME = "userId";
    private final UserService userService;

    @Autowired
    public AdminAccountController(final UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public PersistedUserDTO addUser(@RequestBody final UserToSaveDTO userToSaveDTO) {
        return userService.createAdmin(userToSaveDTO);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public PersistedUserDTO deactivateUser(@RequestBody final Long userId) {
        return userService.deactivateUser(userId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PersistedUserDTO getUserById(@RequestParam(USER_ID_PARAMETER_NAME) final Long userId) {
        return userService.getUserById(userId);
    }
}
