package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.PositionDto;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.repositories.PositionRepository;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PositionService {
    @Autowired
    private PositionRepository positionRepository;

    @Transactional(readOnly = true)
    public List<PositionDto> findAll() {
        Sort sort = Sort.by("name");
        List<Position> positions = positionRepository.findAll(sort);
        return positions.stream().map(PositionDto::new).toList();
    }

    @Transactional(readOnly = true)
    public PositionDto findById(Long id) {
        Position position = positionRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return new PositionDto(position);
    }
}
