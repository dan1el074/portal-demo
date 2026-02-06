package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.entities.Param;
import br.com.metaro.portal.core.repositories.ParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParamService {
    @Autowired
    private ParamRepository paramRepository;

    @Transactional
    public Long newInternalControl() {
        Param param = paramRepository.getReferenceById(2L);
        Long newValue = Long.parseLong(param.getContent()) + 1L;
        param.setContent(String.valueOf(newValue));
        paramRepository.save(param);

        return newValue;
    }
}
