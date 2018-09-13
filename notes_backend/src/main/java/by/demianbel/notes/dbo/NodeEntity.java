package by.demianbel.notes.dbo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "node")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NodeEntity extends AbstractNamedEntity {

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private UserEntity user;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "parent_node_id")
    @EqualsAndHashCode.Exclude
    private NodeEntity parentNode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentNode")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<NodeEntity> children;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "node")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<NoteEntity> notes;
}
