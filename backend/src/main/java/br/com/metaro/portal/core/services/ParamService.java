package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.entities.Param;
import br.com.metaro.portal.core.repositories.ParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Service
public class ParamService {
    @Autowired
    private ParamRepository paramRepository;

    @Transactional
    public Long newInternalControl() {
        List<Param> params = paramRepository.findAll();

        int currentYear = Instant.now().atZone(ZoneId.systemDefault()).getYear();
        if (!params.get(2).getContent().equals(String.valueOf(currentYear))) {
            params.get(1).setContent("0");
            params.get(2).setContent(String.valueOf(currentYear));
        }

        Long newValue = Long.parseLong(params.get(1).getContent()) + 1L;
        params.get(1).setContent(String.valueOf(newValue));
        paramRepository.saveAll(params);

        return newValue;
    }
}
