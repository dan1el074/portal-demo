package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.position.PositionDto;
import br.com.metaro.portal.core.dto.position.PositionFormInputDto;
import br.com.metaro.portal.core.dto.position.PositionMinDto;
import br.com.metaro.portal.core.entities.Position;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.PositionRepository;
import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
public class PositionService {
    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PositionDto> listPositions() {
        Sort sort = Sort.by("name");
        List<Position> positions = positionRepository.findAll(sort);
        return positions.stream().map(PositionDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<PositionMinDto> listActivePositions() {
        Sort sort = Sort.by("name");
        List<Position> positions = positionRepository.findAll(sort);
        positions = positions.stream().filter(p -> p.getActivated().equals(true)).toList();
        return positions.stream().map(PositionMinDto::new).toList();
    }

    @Transactional(readOnly = true)
    public PositionDto getPosition(Long id) {
        Position position = positionRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return new PositionDto(position);
    }

    @Transactional
    public List<PositionDto> createPosition(PositionFormInputDto dto) {
        Position position = new Position();
        position.setManangers(new HashSet<>());
        position.setIsLocked(false);
        position.setCreatedAt(Instant.now());
        copyDtoToEntity(dto, position);

        if (dto.getManangers().isEmpty()) {
            throw new UnprocessableEntityException("É necessário ao menos um gestor para a área.");
        }

        for (Long manangerId : dto.getManangers()) {
            User user = userRepository.getReferenceById(manangerId);
            position.getManangers().add(user);
        }

        positionRepository.save(position);
        return listPositions();
    }

    @Transactional
    public List<PositionDto> updatePosition(Long id, PositionFormInputDto dto) {
        Position position = positionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (Boolean.TRUE.equals(position.getIsLocked()) && !dto.getName().equals(position.getName())) {
            throw new UnprocessableEntityException("Esse departamento não pode ser renomeado porque é usado em alguma ferramenta do sistema!");
        }

        copyDtoToEntity(dto, position);

        if (dto.getManangers().isEmpty()) {
            throw new UnprocessableEntityException("É necessário ao menos um gestor para a área.");
        }

        position.getManangers().clear();
        for (Long manangerId : dto.getManangers()) {
            User user = userRepository.getReferenceById(manangerId);
            position.getManangers().add(user);
        }

        positionRepository.save(position);
        return listPositions();
    }

    @Transactional
    public List<PositionDto> deactivate(Long id) {
        Position position = positionRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        /// verifica se o departamento está em uso por algum usuário, se sim, jogar uma exceção
        if (!position.getUsers().isEmpty()) {
            throw new UnprocessableEntityException("Existem usuários vinculados a esse departamento!");
        }

        position.setActivated(false);
        positionRepository.save(position);
        return listPositions();
    }

    private void copyDtoToEntity(PositionFormInputDto dto, Position entity) {
        entity.setName(dto.getName());
        entity.setActivated(dto.getActivated());
        entity.setUpdatedAt(Instant.now());
    }
}
