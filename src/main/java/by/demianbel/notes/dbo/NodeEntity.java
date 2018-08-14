package by.demianbel.notes.dbo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "node")
@EqualsAndHashCode(callSuper = true)
public class NodeEntity extends AbstractNamedEntity {

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private UserEntity user;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "parent_node_id")
    @EqualsAndHashCode.Exclude
    private NodeEntity parentNode;
}
