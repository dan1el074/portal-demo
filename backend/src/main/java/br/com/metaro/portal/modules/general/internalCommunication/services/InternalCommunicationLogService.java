package br.com.metaro.portal.modules.general.internalCommunication.services;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunication;
import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunicationLog;
import br.com.metaro.portal.modules.general.internalCommunication.repository.InternalCommunicationLogRepository;
import br.com.metaro.portal.modules.general.internalCommunication.repository.InternalCommunicationRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class InternalCommunicationLogService {
    @Autowired
    private InternalCommunicationLogRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private InternalCommunicationRepository ciRepository;

    @Transactional
    public void create(Long ciId, String content) {
        User me = userService.authenticate();
        InternalCommunication ci = ciRepository.getReferenceById(ciId);
        InternalCommunicationLog entity = new InternalCommunicationLog();

        entity.setContent(content);
        entity.setCreatedAt(Instant.now());
        entity.setInternalCommunication(ci);
        entity.setUser(me);
        repository.save(entity);
    }

    @Transactional
    public void system(@NotNull Long ciId, @NotNull String content) {
        InternalCommunication ci = ciRepository.getReferenceById(ciId);
        InternalCommunicationLog entity = new InternalCommunicationLog();

        entity.setContent(content);
        entity.setCreatedAt(Instant.now());
        entity.setInternalCommunication(ci);
        repository.save(entity);
    }
}
