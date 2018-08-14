package by.demianbel.notes.service;

import by.demianbel.notes.converter.user.PersistedUserUserEntityConverter;
import by.demianbel.notes.converter.user.UserToSaveUserEntityConverter;
import by.demianbel.notes.converter.user.UserToUpdateUserEntityConverter;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserToSaveUserEntityConverter userToSaveUserEntityConverter;
    private final UserToUpdateUserEntityConverter userToUpdateUserEntityConverter;
    private final PersistedUserUserEntityConverter persistedUserUserEntityConverter;

    public UserEntity getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final NotesUserDetails principal = (NotesUserDetails) authentication.getPrincipal();
        final Optional<UserEntity> currentUser = userRepository.findById(principal.getUser().getId());
        return currentUser.orElseThrow(
                () -> new RuntimeException("User with id = '" + principal.getUser().getId() + "' not found."));

    }

    public PersistedUserDTO createUser(final UserToSaveDTO userToSaveDTO) {
        final RoleEntity userRole = roleRepository.findRoleEntityByNameEquals(USER_ROLE_NAME)
                .orElseThrow(() -> new RuntimeException("User role '" + USER_ROLE_NAME + "' doesn't exist."));

        final UserEntity userToSave = userToSaveUserEntityConverter.convertToDbo(userToSaveDTO);
        userToSave.setRoles(Collections.singleton(userRole));

        final UserEntity savedUserEntity = userRepository.save(userToSave);
        return persistedUserUserEntityConverter.convertToDto(savedUserEntity);

    }

    public PersistedUserDTO updateUser(final UserToUpdateDTO userToUpdateDTO) {

        final Long userId = userToUpdateDTO.getId();
        final Optional<UserEntity> userToUpdate = userRepository.findById(userId);
        if (userToUpdate.isPresent()) {

            final UserEntity userEntity = userToUpdate.get();
            if (userEntity.getPassword().equals(userToUpdateDTO.getPassword())) {
                final String userName = userToUpdateDTO.getName();
                if (userName != null) {
                    userEntity.setName(userName);
                }
                final String userEmail = userToUpdateDTO.getEmail();
                if (userEmail != null) {
                    userEntity.setEmail(userEmail);
                }
                final UserEntity savedUserEntity = userRepository.save(userEntity);
                return persistedUserUserEntityConverter.convertToDto(savedUserEntity);
            } else {
                throw new RuntimeException("Wrong password.");
            }

        } else {
            throw new RuntimeException("User with id = '" + userId + "' hasn't found.");
        }
    }

    public PersistedUserDTO removeUser(final UserToUpdateDTO userToUpdateDTO) {
        final Long userId = userToUpdateDTO.getId();
        final Optional<UserEntity> userToDelete = userRepository.findById(userId);
        if (userToDelete.isPresent()) {
            userRepository.delete(userToDelete.get());
            return persistedUserUserEntityConverter.convertToDto(userToDelete.get());
        } else {
            throw new RuntimeException("User with id = '" + userId + "' hasn't found.");
        }
    }

    public PersistedUserDTO getUserById(final Long userId) {
        if (userId != null) {
            final Optional<UserEntity> foundUser = userRepository.findById(userId);
            if (foundUser.isPresent()) {
                return persistedUserUserEntityConverter.convertToDto(foundUser.get());
            } else {
                throw new RuntimeException("User with id = '" + userId + "' hasn't found.");
            }
        } else {
            throw new RuntimeException("Id of user shouldn't be null.");
        }
    }
}
