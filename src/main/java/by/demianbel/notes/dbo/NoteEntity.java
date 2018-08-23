package by.demianbel.notes.dbo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Set;

@Data
@Entity
@Table(name = "note")
@EqualsAndHashCode(callSuper = true)
public class NoteEntity extends AbstractNamedEntity {

    @Column(name = "text")
    private String text;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "tag_note",
            joinColumns = {@JoinColumn(name = "note_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    @EqualsAndHashCode.Exclude
    private Set<TagEntity> tags;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private UserEntity user;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "node_id")
    @EqualsAndHashCode.Exclude
    private NodeEntity node;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "note_share_user",
            joinColumns = {@JoinColumn(name = "note_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @EqualsAndHashCode.Exclude
    private Set<UserEntity> usersToShare;

}
