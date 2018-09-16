package by.demianbel.notes.service;

import by.demianbel.notes.repository.UserRepository;
import by.demianbel.notes.dto.NotesUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotesUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        return userRepository.findByName(username).map(NotesUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with name '" + username + "' hasn't found."));
    }


}
