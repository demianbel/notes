package by.demianbel.notes.converter.node;

import by.demianbel.notes.converter.DtoToDboConverter;
import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.UserEntity;
import by.demianbel.notes.dto.node.NodeToSaveDTO;
import by.demianbel.notes.repository.NodeRepository;
import by.demianbel.notes.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class NodeToSaveNodeConverter implements DtoToDboConverter<NodeToSaveDTO, NodeEntity> {

    private final NodeRepository nodeRepository;
    private final UserService userService;

    @Override
    public NodeToSaveDTO convertToDto(final NodeEntity nodeEntity) {
        final NodeToSaveDTO nodeDTO = new NodeToSaveDTO();
        BeanUtils.copyProperties(nodeEntity, nodeDTO);
        nodeDTO.setParentNodeId(Optional.ofNullable(nodeEntity.getParentNode()).map(NodeEntity::getId).orElse(null));
        return nodeDTO;
    }

    @Override
    public NodeEntity convertToDbo(final NodeToSaveDTO nodeToSaveDTO) {
        final NodeEntity nodeEntity = new NodeEntity();
        final Long parentNodeId = nodeToSaveDTO.getParentNodeId();

        final NodeEntity parentNode = Optional.ofNullable(parentNodeId)
                .map(this::findParentNode)
                .orElse(null);

        nodeEntity.setParentNode(parentNode);

        final UserEntity currentUser = userService.getCurrentUser();
        nodeEntity.setUser(currentUser);

        nodeEntity.setActive(true);
        BeanUtils.copyProperties(nodeToSaveDTO, nodeEntity);
        return nodeEntity;
    }

    private NodeEntity findParentNode(final Long id) {
        final UserEntity currentUser = userService.getCurrentUser();
        return nodeRepository.findByUserAndIdAndActiveIsTrue(currentUser, id)
                .orElseThrow(() -> new RuntimeException("Node with id = '" + id + "' doesn't exist."));
    }
}
