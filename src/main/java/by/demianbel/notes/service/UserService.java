package by.demianbel.notes.service;

import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserEntity createUser(final UserEntity userEntity) {
        return userRepository.save(userEntity);
    }
}
