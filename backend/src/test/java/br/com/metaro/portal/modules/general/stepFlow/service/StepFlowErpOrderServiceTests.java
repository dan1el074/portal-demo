package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.stepFlow.entities.Order;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderItem;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStatus;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import br.com.metaro.portal.util.erp.ErpOrderQueryService;
import br.com.metaro.portal.util.erp.dto.ErpOrderDto;
import br.com.metaro.portal.util.erp.dto.ErpOrderItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StepFlowErpOrderServiceTests {
    private UserService userService;
    private ErpOrderQueryService erpOrderQueryService;
    private OrderRepository orderRepository;
    private StepFlowErpOrderService service;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        erpOrderQueryService = mock(ErpOrderQueryService.class);
        orderRepository = mock(OrderRepository.class);

        User authenticatedUser = new User();
        when(userService.authenticate()).thenReturn(authenticatedUser);

        service = new StepFlowErpOrderService(
                userService,
                erpOrderQueryService,
                orderRepository
        );
    }

    @Test
    void addsQuantitiesAlreadyProducedLocally() {
        ErpOrderDto erpOrder = createErpOrder(10);
        Order localOrder = createLocalOrder(4);
        when(erpOrderQueryService.findProductionOrderByNumber(14064))
                .thenReturn(Optional.of(erpOrder));
        when(orderRepository.findByNumber(14064, OrderStatus.CANCELLED))
                .thenReturn(List.of(localOrder));

        ErpOrderDto result = service.findAvailableOrderByNumber(14064);

        assertThat(result.getItems().getFirst().getProducedQuantity())
                .isEqualTo(4);
    }

    @Test
    void rejectsAnOrderThatWasFullyProduced() {
        ErpOrderDto erpOrder = createErpOrder(10);
        Order localOrder = createLocalOrder(10);
        when(erpOrderQueryService.findProductionOrderByNumber(14064))
                .thenReturn(Optional.of(erpOrder));
        when(orderRepository.findByNumber(14064, OrderStatus.CANCELLED))
                .thenReturn(List.of(localOrder));

        assertThatThrownBy(() -> service.findAvailableOrderByNumber(14064))
                .isInstanceOf(UnprocessableEntityException.class);
    }

    private ErpOrderDto createErpOrder(int quantity) {
        ErpOrderDto order = new ErpOrderDto(
                14064,
                "Cliente",
                null,
                null,
                null,
                null,
                null,
                null,
                100.0,
                0.0,
                100.0
        );
        order.addItem(new ErpOrderItemDto(
                "ITEM-1",
                "Item",
                10.0,
                "un",
                quantity,
                0
        ));
        return order;
    }

    private Order createLocalOrder(int producedQuantity) {
        OrderItem item = new OrderItem();
        item.setItemCode("ITEM-1");
        item.setProducedQuantity(producedQuantity);

        Order order = new Order();
        order.setItems(List.of(item));
        return order;
    }
}
