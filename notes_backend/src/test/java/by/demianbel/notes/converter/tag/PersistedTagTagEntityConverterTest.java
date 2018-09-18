package by.demianbel.notes.converter.tag;

import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dto.tag.PersistedTagDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PersistedTagTagEntityConverterTest {

    private static final String TEST_TAG_NAME = "test tag name";

    private PersistedTagTagEntityConverter converter;

    @Before
    public void setUp() {
        converter = new PersistedTagTagEntityConverter();
    }

    @Test
    public void convertToDto() {
        final TagEntity tagEntity = new TagEntity();
        tagEntity.setId(1L);
        tagEntity.setName(TEST_TAG_NAME);

        final PersistedTagDTO persistedTagDTO = converter.convertToDto(tagEntity);
        Assert.assertEquals(Long.valueOf(1L), persistedTagDTO.getId());
        Assert.assertEquals(TEST_TAG_NAME, persistedTagDTO.getName());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void convertToDbo() {
        converter.convertToDbo(null);
    }
}