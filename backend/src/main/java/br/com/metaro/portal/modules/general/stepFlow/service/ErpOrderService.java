package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.ErpOrderItemDto;
import br.com.metaro.portal.modules.general.stepFlow.entities.Order;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStatus;
import br.com.metaro.portal.modules.general.stepFlow.repositories.ErpOrderRepository;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ErpOrderService {
    @Autowired
    private ErpOrderRepository erpRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public ErpOrderDto findOrderByNumber(int orderNumber) {
        ErpOrderDto dto = erpRepository.findOrder(orderNumber).orElseThrow(ResourceNotFoundException::new);
        List<Order> orders = orderRepository.findByNumber(orderNumber, OrderStatus.CANCELLED);

        for (Order order : orders) {
            for (ErpOrderItemDto item : dto.getItems()) {
                Integer producedQuantity = order.getItems()
                        .stream()
                        .filter(oi -> oi.getItemCode().equals(item.getCode()))
                        .findFirst()
                        .orElseThrow(ResourceNotFoundException::new)
                        .getProducedQuantity();

                Integer InvoicedQuantity = order.getItems()
                        .stream()
                        .filter(oi -> oi.getItemCode().equals(item.getCode()))
                        .findFirst()
                        .orElseThrow(ResourceNotFoundException::new)
                        .getInvoicedQuantity();

                item.setProducedQuantity(item.getProducedQuantity() + producedQuantity);
                item.setInvoicedQuantity(item.getInvoicedQuantity() + InvoicedQuantity);
            }
        }

        if (dto.getItems().stream()
                .allMatch(item -> item.getProducedQuantity().equals(item.getQuantity()))) {
            throw new UnprocessableEntityException("Essa ordem já foi produzida!");
        }

        return dto;
    }
}
