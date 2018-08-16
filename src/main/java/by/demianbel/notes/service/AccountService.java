package by.demianbel.notes.service;

import by.demianbel.notes.converter.user.PersistedUserUserEntityConverter;
import by.demianbel.notes.converter.user.UserToSaveUserEntityConverter;
import by.demianbel.notes.dbo.RoleEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.PersistedUserDTO;
import by.demianbel.notes.dto.user.UserToSaveDTO;
import by.demianbel.notes.repository.RoleRepository;
import by.demianbel.notes.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class AccountService {

    private static final String USER_ROLE_NAME = "user";
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PersistedUserUserEntityConverter persistedUserUserEntityConverter;
    private final UserToSaveUserEntityConverter userToSaveUserEntityConverter;
    private final PasswordEncoder passwordEncoder;


    public PersistedUserDTO signUp(UserToSaveDTO userToSaveDTO) {
        final RoleEntity userRole = roleRepository.findRoleEntityByNameEquals(USER_ROLE_NAME)
                .orElseThrow(() -> new RuntimeException("User role '" + USER_ROLE_NAME + "' doesn't exist."));

        final UserEntity userToSave = userToSaveUserEntityConverter.convertToDbo(userToSaveDTO);
        userToSave.setRoles(Collections.singleton(userRole));

        final String password = userToSave.getPassword();
        final String encodedPassword = passwordEncoder.encode(password);
        userToSave.setPassword(encodedPassword);
        final UserEntity savedUserEntity = userRepository.save(userToSave);
        return persistedUserUserEntityConverter.convertToDto(savedUserEntity);
    }

    public PersistedUserDTO deactivateCurrentAccount() {
        final UserEntity currentUser = userService.getCurrentUser();
        currentUser.setActive(false);
        UserEntity deactivatedUser = userRepository.save(currentUser);
        return persistedUserUserEntityConverter.convertToDto(deactivatedUser);
    }

}
