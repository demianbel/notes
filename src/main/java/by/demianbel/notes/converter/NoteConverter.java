package by.demianbel.notes.converter;

import by.demianbel.notes.dbo.NoteEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.NoteSavingDto;
import by.demianbel.notes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NoteConverter implements DtoToDboConverter<NoteSavingDto, NoteEntity> {

    private final UserRepository userRepository;

    @Autowired
    public NoteConverter(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public NoteSavingDto convertToDto(final NoteEntity noteEntity) {
        final NoteSavingDto noteSavingDto = new NoteSavingDto();

        noteSavingDto.setName(noteEntity.getName());
        noteSavingDto.setText(noteEntity.getText());
        noteSavingDto.setUserId(noteEntity.getUser().getId());

        return noteSavingDto;
    }

    @Override
    public NoteEntity convertToDbo(final NoteSavingDto noteSavingDto) {
        final NoteEntity noteEntity = new NoteEntity();

        noteEntity.setName(noteSavingDto.getName());
        noteEntity.setText(noteSavingDto.getText());
        Optional<UserEntity> user = userRepository.findById(noteSavingDto.getUserId());
        if (user.isPresent()) {
            noteEntity.setUser(user.get());
            return noteEntity;
        } else {
            throw new RuntimeException("Unknown user with id = " + noteSavingDto.getUserId());
        }

    }
}
