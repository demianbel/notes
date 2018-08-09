package by.demianbel.notes.service;

import by.demianbel.notes.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeService {

    private NodeRepository nodeRepository;

    @Autowired
    public NodeService(final NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }
}
