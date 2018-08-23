package by.demianbel.notes.dbo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Data
@Entity
@Table(name = "role")
@EqualsAndHashCode(callSuper = true)
public class RoleEntity extends AbstractNamedEntity {

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<UserEntity> users;

}
