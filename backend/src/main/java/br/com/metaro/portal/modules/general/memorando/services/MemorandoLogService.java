package br.com.metaro.portal.modules.general.memorando.services;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.modules.general.memorando.entities.Memorando;
import br.com.metaro.portal.modules.general.memorando.entities.MemorandoLog;
import br.com.metaro.portal.modules.general.memorando.repository.MemorandoLogRepository;
import br.com.metaro.portal.modules.general.memorando.repository.MemorandoRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class MemorandoLogService {
    @Autowired
    private MemorandoLogRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private MemorandoRepository memorandoRepository;

    @Transactional
    public void create(Long ciId, String content) {
        User me = userService.authenticate();
        Memorando ci = memorandoRepository.getReferenceById(ciId);
        MemorandoLog entity = new MemorandoLog();

        entity.setContent(content);
        entity.setCreatedAt(Instant.now());
        entity.setMemorando(ci);
        entity.setUser(me);
        repository.save(entity);
    }

    @Transactional
    public void system(@NotNull Long ciId, @NotNull String content) {
        Memorando ci = memorandoRepository.getReferenceById(ciId);
        MemorandoLog entity = new MemorandoLog();

        entity.setContent(content);
        entity.setCreatedAt(Instant.now());
        entity.setMemorando(ci);
        repository.save(entity);
    }
}
