package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.entities.Param;
import br.com.metaro.portal.core.repositories.ParamRepository;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParamService {
    @Autowired
    private ParamRepository paramRepository;

    @Transactional
    public Long newInternalControl() {
        List<Param> params = paramRepository.findAll();
        Param countParam = params.stream().filter(p -> p.getName().equals("memorandoCount")).findFirst()
                .orElseThrow(ResourceNotFoundException::new);

        Long newValue = Long.parseLong(countParam.getContent()) + 1L;
        countParam.setContent(String.valueOf(newValue));
        paramRepository.save(countParam);

        return newValue;
    }
}
