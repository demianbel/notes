package by.demianbel.notes.converter.tag;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dto.tag.PersistedTagDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class PersistedTagTagEntityConverter implements DtoToDboConverter<PersistedTagDTO, TagEntity> {

    @Override
    public PersistedTagDTO convertToDto(final TagEntity tagEntity) {
        final PersistedTagDTO persistedTagDTO = new PersistedTagDTO();
        BeanUtils.copyProperties(tagEntity, persistedTagDTO);
        return persistedTagDTO;
    }

    @Override
    public TagEntity convertToDbo(final PersistedTagDTO persistedTagDTO) {
        final TagEntity tagEntity = new TagEntity();
        BeanUtils.copyProperties(persistedTagDTO, tagEntity);
        return tagEntity;
    }
}
