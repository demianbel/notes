package by.demianbel.notes.dto;

import lombok.Data;

@Data
public class NoteSavingDto {

    private String name;

    private String text;

    private long userId;
}
