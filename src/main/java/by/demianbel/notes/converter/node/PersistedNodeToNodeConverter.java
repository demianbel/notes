package by.demianbel.notes.converter.node;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dto.node.PersistedNodeDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersistedNodeToNodeConverter implements DtoToDboConverter<PersistedNodeDTO, NodeEntity> {

    @Override
    public PersistedNodeDTO convertToDto(final NodeEntity nodeEntity) {
        final PersistedNodeDTO persistedNodeDTO = new PersistedNodeDTO();
        BeanUtils.copyProperties(nodeEntity, persistedNodeDTO);

        final Long parentNodeId = Optional.ofNullable(nodeEntity.getParentNode()).map(NodeEntity::getId).orElse(null);
        persistedNodeDTO.setParentNodeId(parentNodeId);
        return persistedNodeDTO;
    }

    @Override
    public NodeEntity convertToDbo(final PersistedNodeDTO persistedNodeDTO) {
        throw new UnsupportedOperationException("We shouldn't convert persisted dto to dbo");
    }
}
