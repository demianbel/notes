package by.demianbel.notes.dbo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "tag")
@EqualsAndHashCode(callSuper = true)
public class TagEntity extends AbstractNamedEntity {

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "tag_note",
            joinColumns = {@JoinColumn(name = "tag_id")},
            inverseJoinColumns = {@JoinColumn(name = "note_id")}
    )
    @EqualsAndHashCode.Exclude
    private Set<NoteEntity> notes;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private UserEntity user;

}
