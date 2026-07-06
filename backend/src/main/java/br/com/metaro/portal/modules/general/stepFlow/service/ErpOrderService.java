package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderItemDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderSummaryDto;
import br.com.metaro.portal.modules.general.stepFlow.repositories.ErpOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ErpOrderService {
    @Autowired
    private ErpOrderRepository repository;

    @Transactional(readOnly = true)
    public Optional<ErpOrderSummaryDto> findByOrder(int orderNumber) {
        return repository.findOrderSummary(orderNumber);
    }

    @Transactional(readOnly = true)
    public List<ErpOrderItemDto> findItemsByOrder(int orderNumber) {
        return repository.findOrderItems(orderNumber);
    }
}
