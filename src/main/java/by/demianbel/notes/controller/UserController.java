package by.demianbel.notes.controller;

import by.demianbel.notes.dto.user.PersistedUserDTO;
import by.demianbel.notes.dto.user.UserToSaveDTO;
import by.demianbel.notes.dto.user.UserToUpdateDTO;
import by.demianbel.notes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final String USER_ID_PARAMETER_NAME = "userId";
    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public PersistedUserDTO addUser(@RequestBody UserToSaveDTO userToSaveDTO) {
        return userService.createAdmin(userToSaveDTO);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public PersistedUserDTO updateUser(@RequestBody UserToUpdateDTO userToUpdateDTO) {
        return userService.updateUser(userToUpdateDTO);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public PersistedUserDTO deactivateUser(@RequestBody Long userId) {
        return userService.deactivateUser(userId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public PersistedUserDTO getUserById(@RequestParam(USER_ID_PARAMETER_NAME) Long userId) {
        return userService.getUserById(userId);
    }
}
