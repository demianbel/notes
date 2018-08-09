package by.demianbel.notes.dbo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Data
@MappedSuperclass
public abstract class AbstractNamedEntity extends AbstractEntity {

    @NotNull
    @Column(name = "name")
    private String name;

}
