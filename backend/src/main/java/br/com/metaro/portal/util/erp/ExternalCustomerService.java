package br.com.metaro.portal.util.erp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExternalCustomerService {
    @Autowired
    private ExternalCustomerRepository repository;

    @Transactional(readOnly = true)
    public List<OrderDto> searchOrderByNumber(int orderNumber) {
        return repository.searchOrderByNumber(orderNumber);
    }
}
