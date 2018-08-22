package by.demianbel.notes.service;

import by.demianbel.notes.converter.user.PersistedUserUserEntityConverter;
import by.demianbel.notes.converter.user.UserToSaveUserEntityConverter;
import by.demianbel.notes.dbo.RoleEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.PersistedUserDTO;
import by.demianbel.notes.dto.user.UserToSaveDTO;
import by.demianbel.notes.dto.user.UserToUpdateDTO;
import by.demianbel.notes.repository.RoleRepository;
import by.demianbel.notes.repository.UserRepository;
import by.demianbel.notes.security.NotesUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private static final String USER_ROLE_NAME = "user";
    private static final String ADMIN_ROLE_NAME = "admin";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserToSaveUserEntityConverter userToSaveUserEntityConverter;
    private final PersistedUserUserEntityConverter persistedUserUserEntityConverter;

    public UserEntity getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final NotesUserDetails principal = (NotesUserDetails) authentication.getPrincipal();
        final Optional<UserEntity> currentUser = userRepository.findById(principal.getUser().getId());
        return currentUser.orElseThrow(
                () -> new RuntimeException("User with id = '" + principal.getUser().getId() + "' not found."));

    }

    public PersistedUserDTO createAdmin(final UserToSaveDTO userToSaveDTO) {
        final RoleEntity userRole = roleRepository.findRoleEntityByNameEquals(ADMIN_ROLE_NAME)
                .orElseThrow(() -> new RuntimeException("User role '" + ADMIN_ROLE_NAME + "' doesn't exist."));

        final UserEntity userToSave = userToSaveUserEntityConverter.convertToDbo(userToSaveDTO);
        userToSave.setRoles(Collections.singleton(userRole));

        final UserEntity savedUserEntity = userRepository.save(userToSave);
        return persistedUserUserEntityConverter.convertToDto(savedUserEntity);

    }

    public PersistedUserDTO updateUser(final UserToUpdateDTO userToUpdateDTO) {

        final Long userId = userToUpdateDTO.getId();
        final UserEntity userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id = '" + userId + "' hasn't found."));

        final String userName = userToUpdateDTO.getName();
        if (userName != null) {
            userToUpdate.setName(userName);
        }

        final String userEmail = userToUpdateDTO.getEmail();
        if (userEmail != null) {
            userToUpdate.setEmail(userEmail);
        }

        final UserEntity savedUserEntity = userRepository.save(userToUpdate);
        return persistedUserUserEntityConverter.convertToDto(savedUserEntity);

    }

    public PersistedUserDTO deactivateUser(final Long userId) {
        final UserEntity userToDeactivate = userRepository.findByIdAndActiveIsTrue(userId)
                .orElseThrow(() -> new RuntimeException("User with id = '" + userId + "' hasn't found."));
        userToDeactivate.setActive(false);
        userRepository.save(userToDeactivate);
        return persistedUserUserEntityConverter.convertToDto(userToDeactivate);
    }

    public PersistedUserDTO getUserById(final Long userId) {
        return userRepository.findById(userId).map(persistedUserUserEntityConverter::convertToDto)
                .orElseThrow(() -> new RuntimeException("User with id = '" + userId + "' hasn't found."));
    }
}
