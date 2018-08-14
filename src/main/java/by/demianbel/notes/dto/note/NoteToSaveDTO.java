package by.demianbel.notes.dto.note;

import lombok.Data;

import java.util.List;

@Data
public class NoteToSaveDTO {

    private String name;

    private String text;

    private List<Long> tagIds;

    private Long nodeId;

}
