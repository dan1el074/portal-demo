package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.modules.general.stepFlow.dto.*;
import br.com.metaro.portal.modules.general.stepFlow.entities.*;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import br.com.metaro.portal.modules.general.stepFlow.repositories.projections.StatusCountsProjection;
import br.com.metaro.portal.modules.general.stepFlow.repositories.projections.StepCountProjection;
import br.com.metaro.portal.util.others.StringUtils;
import br.com.metaro.portal.util.picture.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StepFlowService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ErpOrderService erpOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private PictureService pictureService;

    @Transactional(readOnly = true)
    public OrderDto findById(Long id) {
        Order dto = orderRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return new OrderDto(dto);
    }

    @Transactional(readOnly = true)
    public Page<OrderMinDto> listOrders(String search, Pageable pageable) {
        String translated = search;

        if (translated != null) {
            OrderStatus status = OrderStatus.fromDescription(search);
            if (status != null) translated = status.name();

            StepType step = StepType.fromDescription(search);
            if (step != null) translated = step.name();
        }

        Page<Order> entities = orderRepository.search(translated != null ? translated : "", pageable);
        return entities.map(OrderMinDto::new);
    }

    @Transactional(readOnly = true)
    public List<OrderMinDto> findByCurrentStep(Integer ordinal) {
        List<Order> entities = orderRepository.findByCurrentStep(StepType.values()[ordinal], StepStatus.ACTIVE);
        return entities.stream().map(OrderMinDto::new).toList();
    }

    @Transactional(readOnly = true)
    public StepFlowInfoDto generateDashboard() {
        StatusCountsProjection counts = orderRepository.findStatusCounts().orElseThrow(ResourceNotFoundException::new);
        Integer total = counts.getTotalCount() != null ? counts.getTotalCount().intValue() : 0;
        Integer progress = counts.getProgressCount() != null ? counts.getProgressCount().intValue() : 0;
        Integer complete = counts.getCompleteCount() != null ? counts.getCompleteCount().intValue() : 0;
        Integer late = counts.getLateCount() != null ? counts.getLateCount().intValue() : 0;

        Map<StepType, Integer> stepMap = orderRepository.findCountByStep(OrderStatus.COMPLETED).stream().collect(Collectors
                .toMap(StepCountProjection::getStep, row -> row.getCount().intValue()));

        List<Integer> stepsCount = Arrays.stream(StepType.values())
                .map(step -> stepMap.getOrDefault(step, 0))
                .toList();

        return new StepFlowInfoDto(total, progress, complete, late, stepsCount);
    }

    @Transactional
    public void create(Integer orderNumber) {
        ErpOrderSummaryDto info = erpOrderService.findByOrder(orderNumber).orElseThrow(ResourceNotFoundException::new);
        List<ErpOrderItemDto> items = erpOrderService.findItemsByOrder(orderNumber);

        Order entity = new Order();
        snapshotErpInfo(entity, info, items);
        addAllSteps(entity);

        entity.setNumber(orderNumber);
        entity.setCurrentStep(StepType.FINAL_ASSEMBLY);
        entity.setStatus(OrderStatus.IN_PROGRESS);
        entity.setShipment(0.0);

        this.orderRepository.save(entity);
    }

    @Transactional
    public OrderDto update(Long id, OrderInputDto dto) throws IOException {
        Order order = orderRepository.getReferenceById(id);
        rulesForUpdate(dto, order);
        return new OrderDto(order);
    }

    @Transactional
    public void goToNextStep(Long id) {
        Order order = this.orderRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        StepType type = order.getCurrentStep();
        OrderStep currentStep = order.getSteps().stream().filter(step -> step.getStep().equals(type))
                .findFirst().orElseThrow(ResourceNotFoundException::new);

        currentStep.setStatus(StepStatus.DONE);
        currentStep.setFinishedAt(Instant.now());
        currentStep.setFinishedBy(userService.authenticate());

        if (!type.equals(StepType.SHIPPING)) {
            StepType nextType = StepType.values()[type.ordinal() + 1];
            OrderStep nextStep = order.getSteps().stream().filter(step -> step.getStep().equals(nextType))
                    .findFirst().orElseThrow(ResourceNotFoundException::new);

            order.setCurrentStep(nextType);
            nextStep.setStatus(StepStatus.ACTIVE);
            nextStep.setStartedAt(Instant.now());
        }

        if (type.equals(StepType.SHIPPING)) {
            order.setStatus(OrderStatus.COMPLETED);
        }

        orderRepository.save(order);
    }

    private void snapshotErpInfo(Order entity, ErpOrderSummaryDto summary, List<ErpOrderItemDto> items) {
        // summary dto
        entity.setClient(summary.getClient());
        entity.setCnpj(summary.getCnpj());
        entity.setSeller(summary.getSalesperson());
        entity.setStartDate(summary.getStartDate());
        entity.setDueDate(summary.getDueDate());
        entity.setAddress(summary.getAddress());
        entity.setSubtotal(summary.getSubtotal());
        entity.setDiscount(summary.getDiscount());
        entity.setTotal(summary.getTotal());
        entity.setItems(new ArrayList<>());

        if (summary.getPhone() != null) entity.setPhone(summary.getPhone());

        for (ErpOrderItemDto dto : items) {
            OrderItem item = new OrderItem();

            item.setItemCode(dto.getCode());
            item.setDescription(dto.getDescription());
            item.setUnit(dto.getUnit());
            item.setQuantity(dto.getQuantity());
            item.setUnitPrice(dto.getUnitValue());
            item.setTotal(dto.getUnitValue() * dto.getQuantity());

            entity.addItem(item);
        }
    }

    private void addAllSteps(Order entity) {
        entity.setSteps(new ArrayList<>());

        for (int i=0; i<5; i++) {
            OrderStep step = new OrderStep();

            step.setStep(StepType.values()[i]);
            step.setStatus(StepStatus.WAITING);
            step.setMessages(new ArrayList<>());
            step.setPictures(new ArrayList<>());

            entity.addStep(step);
        }

        entity.getSteps().getFirst().setStatus(StepStatus.ACTIVE);
        entity.getSteps().getFirst().setStartedAt(Instant.now());
    }

    private void rulesForUpdate(OrderInputDto dto, Order order) throws IOException {
        User me = userService.authenticate();
        if (!canInteract(order, me)) throw new ForbiddenException("Usuário não corresponde ao setor competente!");

        OrderStep currentStep = order.getSteps()
                .stream()
                .filter(step -> step.getStep().equals(order.getCurrentStep()))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new);
        if (dto.getComment() != null && !dto.getComment().trim().isEmpty()) {
            currentStep.getMessages().add(new StepMessage(StringUtils.toMarkdown(dto.getComment()), currentStep, me));
        }

        switch (order.getCurrentStep()) {
            case FINAL_ASSEMBLY, SHIPPING -> proccessImage(dto, currentStep);
            case FREIGHT -> {
                if (dto.getCarrier() != null) order.setCarrier(dto.getCarrier());
                if (dto.getShippment() != null) order.setShipment(Double.parseDouble(dto.getShippment()));
            }
        }
    }

    private void proccessImage(OrderInputDto dto, OrderStep step) throws IOException {
        if (dto.getImages() == null || dto.getImages().length == 0) return;

        pictureService.saveStepFlowImages(Arrays.stream(dto.getImages()).toList(), step);
    }

    private boolean canInteract(Order order, User user) {
        if (user.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) return true;

        return switch (order.getCurrentStep()) {
            case FINAL_ASSEMBLY -> user.getPosition().getName().equals("Montagem Final");
            case PCP ->  user.getPosition().getName().equals("PCP");
            case FREIGHT -> user.getPosition().getName().equals("Frete");
            case BILLING -> user.getPosition().getName().equals("Faturamento");
            case SHIPPING -> user.getPosition().getName().equals("Expedição");
        };
    }
}
