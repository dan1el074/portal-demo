package br.com.metaro.portal.util.erp;

import br.com.metaro.portal.util.erp.dto.ErpOrderDto;
import br.com.metaro.portal.util.erp.dto.ErpOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ErpOrderQueryService {
    @Autowired
    private ErpOrderRepository erpOrderRepository;

    public Optional<ErpOrderDto> findProductionOrderByNumber(int orderNumber) {
        return erpOrderRepository.findProductionOrderByNumber(orderNumber);
    }

    public Optional<ErpOrderDto> findProductionOrderByNumberWithoutRules(int orderNumber) {
        return erpOrderRepository.findProductionOrderByNumberWithoutRules(orderNumber);
    }

    public List<ErpOrderLineDto> findOrderLinesByNumber(int orderNumber) {
        return erpOrderRepository.findOrderLinesByNumber(orderNumber);
    }
}
