package by.demianbel.notes.service;

import by.demianbel.notes.converter.user.PersistedUserUserEntityConverter;
import by.demianbel.notes.converter.user.UserToSaveUserEntityConverter;
import by.demianbel.notes.dbo.RoleEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.PersistedUserDTO;
import by.demianbel.notes.dto.user.UserToRestoreDTO;
import by.demianbel.notes.dto.user.UserToSaveDTO;
import by.demianbel.notes.repository.RoleRepository;
import by.demianbel.notes.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class AccountServiceTest {

    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String USER_ROLE_NAME = "user";
    private static final String ROLE_NAME = "role name";
    private static final String RIGHT_PASSWORD = "right password";
    private static final String WRONG_PASSWORD = "wrong password";

    @TestConfiguration
    public static class AccountServiceTestConfiguration {
        @Bean
        public AccountService accountService(final UserService userService,
                                             final UserRepository userRepository,
                                             final RoleRepository roleRepository,
                                             final PersistedUserUserEntityConverter persistedUserUserEntityConverter,
                                             final UserToSaveUserEntityConverter userToSaveUserEntityConverter,
                                             final PasswordEncoder passwordEncoder) {
            return new AccountService(userService, userRepository, roleRepository, persistedUserUserEntityConverter,
                                      userToSaveUserEntityConverter, passwordEncoder);
        }

        @Bean
        public PersistedUserUserEntityConverter persistedUserUserEntityConverter() {
            return new PersistedUserUserEntityConverter();
        }

        @Bean
        public UserToSaveUserEntityConverter userToSaveUserEntityConverter() {
            return new UserToSaveUserEntityConverter();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void signUp() {

        final UserToSaveDTO userToSaveDTO = new UserToSaveDTO();
        userToSaveDTO.setName(NAME);
        userToSaveDTO.setEmail(EMAIL);
        userToSaveDTO.setPassword(PASSWORD);

        final RoleEntity role = new RoleEntity();
        role.setName(ROLE_NAME);
        Mockito.when(roleRepository.findRoleEntityByNameEquals(USER_ROLE_NAME))
                .thenReturn(Optional.of(role));

        Mockito.when(userRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        final PersistedUserDTO persistedUserDTO = accountService.signUp(userToSaveDTO);

        Assert.assertNotNull(persistedUserDTO);
        Assert.assertEquals(EMAIL, persistedUserDTO.getEmail());
        Assert.assertEquals(NAME, persistedUserDTO.getName());

        final List<String> roles = persistedUserDTO.getRoles();
        Assert.assertEquals(1, roles.size());
        Assert.assertEquals(ROLE_NAME, roles.get(0));
    }

    @Test
    public void deactivateCurrentAccount() {
        final UserEntity currentUser = new UserEntity();
        currentUser.setName(NAME);
        currentUser.setActive(true);

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(userRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        final PersistedUserDTO persistedUserDTO = accountService.deactivateCurrentAccount();

        Assert.assertFalse(currentUser.isActive());
        Assert.assertEquals(NAME, persistedUserDTO.getName());
    }

    @Test
    public void restoreRightPassword() {

        final UserEntity disabledUser = getDeactivatedUserEntity();

        Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.of(disabledUser));

        Mockito.when(userRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        final UserToRestoreDTO userToRestoreDTO = new UserToRestoreDTO();
        userToRestoreDTO.setName(NAME);
        userToRestoreDTO.setPassword(RIGHT_PASSWORD);

        final PersistedUserDTO restoredUser = accountService.restore(userToRestoreDTO);

        Assert.assertNotNull(restoredUser);
        Assert.assertEquals(NAME, restoredUser.getName());
        Assert.assertTrue(disabledUser.isActive());

    }

    @Test(expected = BadCredentialsException.class)
    public void restoreWrongPassword() {

        final UserEntity disabledUser = getDeactivatedUserEntity();

        Mockito.when(userRepository.findByName(NAME)).thenReturn(Optional.of(disabledUser));

        final UserToRestoreDTO userToRestoreDTO = new UserToRestoreDTO();
        userToRestoreDTO.setName(NAME);
        userToRestoreDTO.setPassword(WRONG_PASSWORD);

        accountService.restore(userToRestoreDTO);

    }

    private UserEntity getDeactivatedUserEntity() {
        final UserEntity disabledUser = new UserEntity();
        disabledUser.setName(NAME);
        disabledUser.setActive(false);
        disabledUser.setPassword(passwordEncoder.encode(RIGHT_PASSWORD));
        return disabledUser;
    }
}