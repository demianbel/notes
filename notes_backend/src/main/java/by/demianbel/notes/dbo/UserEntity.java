package by.demianbel.notes.dbo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(name = "uq_user__name", columnNames = {"name"}))
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends AbstractNamedEntity {

    @NotNull
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "email")
    private String email;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    @EqualsAndHashCode.Exclude
    private Set<RoleEntity> roles;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "note_share_user",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "note_id")}
    )
    @EqualsAndHashCode.Exclude
    private Set<NoteEntity> sharedNotes;
}
