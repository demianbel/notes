package by.demianbel.notes.service;

import by.demianbel.notes.converter.user.PersistedUserUserEntityConverter;
import by.demianbel.notes.converter.user.UserToSaveUserEntityConverter;
import by.demianbel.notes.dbo.RoleEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.user.PersistedUserDTO;
import by.demianbel.notes.dto.user.UserToSaveDTO;
import by.demianbel.notes.dto.user.UserToUpdateDTO;
import by.demianbel.notes.exception.RoleNotFoundException;
import by.demianbel.notes.exception.UserNotFoundException;
import by.demianbel.notes.repository.RoleRepository;
import by.demianbel.notes.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    private static final String USER_NAME = "user name";
    private static final String ADMIN_ROLE_NAME = "admin";
    private static final String EMAIL = "email";
    private static final String NAME = "name";

    @TestConfiguration
    public static class UserServiceTestConfiguration {
        @Bean
        public UserService userService(final UserRepository userRepository,
                                       final RoleRepository roleRepository,
                                       final UserToSaveUserEntityConverter userToSaveUserEntityConverter,
                                       final PersistedUserUserEntityConverter persistedUserUserEntityConverter) {
            return new UserService(userRepository, roleRepository, userToSaveUserEntityConverter,
                                   persistedUserUserEntityConverter);
        }

        @Bean
        public UserToSaveUserEntityConverter userToSaveUserEntityConverter() {
            return new UserToSaveUserEntityConverter();
        }

        @Bean
        public PersistedUserUserEntityConverter persistedUserUserEntityConverter() {
            return new PersistedUserUserEntityConverter();
        }
    }

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;


    @Test
    public void getCurrentUser() {
        setupAuth();
        final UserEntity user = new UserEntity();
        Mockito.when(userRepository.findByName(Mockito.eq(USER_NAME))).thenReturn(Optional.of(user));
        final UserEntity currentUser = userService.getCurrentUser();
        Assert.assertEquals(user, currentUser);
    }

    @Test(expected = UserNotFoundException.class)
    public void getCurrentUserEmpty() {
        setupAuth();
        Mockito.when(userRepository.findByName(Mockito.eq(USER_NAME))).thenReturn(Optional.empty());
        userService.getCurrentUser();
    }

    @Test
    public void updateUser() {
        final UserToUpdateDTO userToUpdateDTO = new UserToUpdateDTO();
        final long userId = 1L;
        userToUpdateDTO.setId(userId);
        userToUpdateDTO.setEmail(EMAIL);
        userToUpdateDTO.setName(NAME);

        final UserEntity foundUser = new UserEntity();
        Mockito.when(userRepository.findById(Mockito.eq(userId))).thenReturn(Optional.of(foundUser));
        Mockito.when(userRepository.save(foundUser)).thenReturn(foundUser);

        final PersistedUserDTO persistedUserDTO = userService.updateUser(userToUpdateDTO);

        Assert.assertNotNull(persistedUserDTO);
        Assert.assertEquals(EMAIL, persistedUserDTO.getEmail());
        Assert.assertEquals(NAME, persistedUserDTO.getName());

        Assert.assertEquals(EMAIL, foundUser.getEmail());
        Assert.assertEquals(NAME, foundUser.getName());

    }

    @Test
    public void createAdmin() {

        Mockito.when(roleRepository.findRoleEntityByNameEquals(ADMIN_ROLE_NAME))
                .thenReturn(Optional.of(new RoleEntity()));

        final UserEntity adminToSave = new UserEntity();
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(adminToSave);

        final PersistedUserDTO savedAdmin = userService.createAdmin(new UserToSaveDTO());

        Mockito.verify(roleRepository, Mockito.times(1)).findRoleEntityByNameEquals(ADMIN_ROLE_NAME);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    }

    @Test(expected = RoleNotFoundException.class)
    public void createAdminNoRole() {
        Mockito.when(roleRepository.findRoleEntityByNameEquals(ADMIN_ROLE_NAME))
                .thenReturn(Optional.empty());

        userService.createAdmin(new UserToSaveDTO());
    }

    @Test
    public void deactivateUser() {
        final UserEntity foundUser = new UserEntity();
        foundUser.setActive(true);
        final long userId = 1L;
        foundUser.setId(userId);
        Mockito.when(userRepository.findByIdAndActiveIsTrue(userId)).thenReturn(Optional.of(foundUser));
        Mockito.when(userRepository.save(foundUser)).thenReturn(foundUser);

        final PersistedUserDTO persistedUserDTO = userService.deactivateUser(userId);
        Assert.assertNotNull(persistedUserDTO);
        Assert.assertEquals(Long.valueOf(userId), persistedUserDTO.getId());
        Assert.assertFalse(foundUser.isActive());
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserByIdEmpty() {
        final long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        userService.getUserById(userId);
    }

    @Test
    public void getUserById() {
        final long userId = 1L;
        final UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        final PersistedUserDTO returnedUser = userService.getUserById(userId);
        Assert.assertEquals(Long.valueOf(userId), returnedUser.getId());
    }

    private void setupAuth() {
        SecurityContextHolder.getContext().setAuthentication(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return USER_NAME;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(final boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        });
    }
}