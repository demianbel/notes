package by.demianbel.notes.controller;

import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public UserEntity addUser(UserEntity userToSave) {
        return userService.createUser(userToSave);
    }
}
