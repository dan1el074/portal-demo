package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.position.PositionDto;
import br.com.metaro.portal.core.dto.position.PositionFormImputDto;
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
    public List<PositionDto> findAll() {
        Sort sort = Sort.by("name");
        List<Position> positions = positionRepository.findAll(sort);
        return positions.stream().map(PositionDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<PositionMinDto> list() {
        Sort sort = Sort.by("name");
        List<Position> positions = positionRepository.findAll(sort);
        return positions.stream().map(PositionMinDto::new).toList();
    }

    @Transactional(readOnly = true)
    public PositionDto findById(Long id) {
        Position position = positionRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return new PositionDto(position);
    }

    @Transactional
    public List<PositionDto> insert(PositionFormImputDto dto) {
        Position position = new Position();
        position.setManangers(new HashSet<>());
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
        return findAll();
    }

    @Transactional
    public List<PositionDto> update(Long id, PositionFormImputDto dto) {
        Position position = positionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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
        return findAll();
    }

    @Transactional
    public List<PositionDto> deactive(Long id) {
        Position position = positionRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        /// verifica se o departamento está em uso por algum usuário, se sim, jogar uma exceção
        if (!position.getUsers().isEmpty()) {
            throw new UnprocessableEntityException("Existem usuários vinculados a esse departamento!");
        }

        position.setActivated(false);
        positionRepository.save(position);
        return findAll();
    }

    private void copyDtoToEntity(PositionFormImputDto dto, Position entity) {
        entity.setName(dto.getName());
        entity.setActivated(dto.getActivated());
        entity.setUpdatedAt(Instant.now());
    }
}
