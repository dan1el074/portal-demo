package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.modules.general.stepFlow.dto.OrderDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.OrderMinDto;
import br.com.metaro.portal.modules.general.stepFlow.entities.Order;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import br.com.metaro.portal.util.erp.dto.ErpOrderDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepFlowServiceTests {

    @Test
    void addsOccurrenceSuffixFromSecondRegistrationOnwards() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        StepFlowService service = new StepFlowService();
        ReflectionTestUtils.setField(service, "orderRepository", orderRepository);
        when(orderRepository.countByNumber(14064)).thenReturn(1L);

        service.create(erpOrder(14064));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order savedOrder = captor.getValue();

        assertThat(savedOrder.getNumber()).isEqualTo(14064);
        assertThat(savedOrder.getOccurrence()).isEqualTo(2);
        assertThat(new OrderMinDto(savedOrder).getNumber()).isEqualTo("14064-2");
        assertThat(new OrderDto(savedOrder).getNumber()).isEqualTo("14064-2");
    }

    @Test
    void keepsFirstRegistrationWithoutSuffix() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        StepFlowService service = new StepFlowService();
        ReflectionTestUtils.setField(service, "orderRepository", orderRepository);
        when(orderRepository.countByNumber(14064)).thenReturn(0L);

        service.create(erpOrder(14064));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        assertThat(captor.getValue().getDisplayNumber()).isEqualTo("14064");
    }

    private ErpOrderDto erpOrder(int number) {
        return new ErpOrderDto(
                number,
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
    }
}
