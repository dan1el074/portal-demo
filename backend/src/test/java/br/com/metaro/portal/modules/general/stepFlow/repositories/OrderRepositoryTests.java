package br.com.metaro.portal.modules.general.stepFlow.repositories;

import br.com.metaro.portal.modules.general.stepFlow.entities.Order;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStatus;
import br.com.metaro.portal.modules.general.stepFlow.entities.StepType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = { "spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop" })
class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        Order inProgress = order(100, "Zulu", StepType.PCP, OrderStatus.IN_PROGRESS);
        inProgress.setDueDate(LocalDate.now().plusDays(1));
        orderRepository.save(inProgress);

        Order completed = order(300, "Alfa", StepType.PCP, OrderStatus.COMPLETED);
        completed.setDueDate(LocalDate.now().plusDays(1));
        orderRepository.save(completed);

        Order overdue = order(200, "Mike", StepType.FREIGHT, OrderStatus.IN_PROGRESS);
        overdue.setDueDate(LocalDate.now().minusDays(1));
        orderRepository.save(overdue);

        orderRepository.flush();
    }

    @Test
    void appliesPageableSortToSearch() {
        assertThat(searchSortedBy("number", Sort.Direction.DESC))
                .extracting(Order::getNumber)
                .containsExactly(300, 200, 100);

        assertThat(searchSortedBy("client", Sort.Direction.ASC))
                .extracting(Order::getClient)
                .containsExactly("Alfa", "Mike", "Zulu");

        assertThat(searchSortedBy("status", Sort.Direction.ASC))
                .extracting(Order::getStatus)
                .containsExactly(OrderStatus.COMPLETED, OrderStatus.IN_PROGRESS, OrderStatus.IN_PROGRESS);
    }

    @Test
    void appliesPageableSortWhenFilteringByStep() {
        var pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "number"));

        var result = orderRepository.searchOnlyStep(
                pageable,
                "",
                StepType.PCP.name(),
                OrderStatus.CANCELLED
        );

        assertThat(result.getContent())
                .extracting(Order::getNumber)
                .containsExactly(300, 100);
    }

    @Test
    void sortsByTheStatusDisplayedToTheUser() {
        var pageable = PageRequest.of(0, 10, Sort.by("number"));

        var ascending = orderRepository.searchOrderByEffectiveStatus(pageable, "", 1);
        assertThat(ascending.getContent())
                .extracting(Order::getNumber)
                .containsExactly(200, 100, 300);

        var descending = orderRepository.searchOrderByEffectiveStatus(pageable, "", -1);
        assertThat(descending.getContent())
                .extracting(Order::getNumber)
                .containsExactly(300, 100, 200);
    }

    @Test
    void sortsByTheDisplayedStatusWhenFilteringByStep() {
        var pageable = PageRequest.of(0, 10, Sort.by("number"));

        var result = orderRepository.searchOnlyStepOrderByEffectiveStatus(
                pageable,
                "",
                StepType.PCP.name(),
                OrderStatus.CANCELLED,
                1
        );

        assertThat(result.getContent())
                .extracting(Order::getNumber)
                .containsExactly(100, 300);
    }

    private java.util.List<Order> searchSortedBy(String property, Sort.Direction direction) {
        var pageable = PageRequest.of(0, 10, Sort.by(direction, property));
        return orderRepository.search(pageable, "").getContent();
    }

    private Order order(Integer number, String client, StepType step, OrderStatus status) {
        Order order = new Order();
        order.setNumber(number);
        order.setClient(client);
        order.setCurrentStep(step);
        order.setStatus(status);
        order.setItems(new ArrayList<>());
        order.setSteps(new ArrayList<>());
        return order;
    }
}
