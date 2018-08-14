package by.demianbel.notes.service;

import by.demianbel.notes.repository.NodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

}
