package by.demianbel.notes.dbo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractNamedEntity extends AbstractEntity {

    @NotNull
    @Column(name = "name")
    private String name;

}
