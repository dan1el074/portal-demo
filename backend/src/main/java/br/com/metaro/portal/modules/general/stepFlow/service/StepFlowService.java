package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.stepFlow.dto.*;
import br.com.metaro.portal.modules.general.stepFlow.entities.*;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import br.com.metaro.portal.modules.general.stepFlow.repositories.projections.StatusCountsProjection;
import br.com.metaro.portal.modules.general.stepFlow.repositories.projections.StepCountProjection;
import br.com.metaro.portal.util.others.StringUtils;
import br.com.metaro.portal.util.picture.PictureService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ObjectMapper objectMapper;

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
    public Page<OrderMinDto> listOrders(Pageable pageable, String search, String stepFilter) {
        String translated = search;
        String onlyStep = stepFilter;

        if (translated != null) {
            StepType step = StepType.fromDescription(search);
            if (step != null) translated = step.name();

            OrderStatus status = OrderStatus.fromDescription(search);
            if (status != null) translated = status.name();
        }

        if (onlyStep != null) {
            StepType step = StepType.fromDescription(stepFilter);
            if (step != null) onlyStep = step.name();
        }

        Page<Order> entities;

        if (onlyStep != null) {
            entities = orderRepository.searchOnlyStep(
                    pageable,
                    translated != null ? translated : "",
                    onlyStep,
                    OrderStatus.CANCELLED
            );
            return entities.map(OrderMinDto::new);
        }

        entities = orderRepository.search(pageable, translated != null ? translated : "");
        return entities.map(OrderMinDto::new);
    }

    @Transactional(readOnly = true)
    public List<OrderMinDto> listOrdersByCurrentStep(Integer ordinal) {
        List<Order> entities = orderRepository.findByCurrentStep(StepType.values()[ordinal], StepStatus.ACTIVE, OrderStatus.CANCELLED);
        return entities.stream().map(OrderMinDto::new).toList();
    }

    @Transactional(readOnly = true)
    public StepFlowInfoDto generateDashboard() {
        StatusCountsProjection counts = orderRepository.findCountByStatus().orElseThrow(ResourceNotFoundException::new);
        Integer total = counts.getTotalCount() != null ? counts.getTotalCount().intValue() : 0;
        Integer progress = counts.getProgressCount() != null ? counts.getProgressCount().intValue() : 0;
        Integer complete = counts.getCompleteCount() != null ? counts.getCompleteCount().intValue() : 0;
        Integer late = counts.getLateCount() != null ? counts.getLateCount().intValue() : 0;

        Map<StepType, Integer> stepMap = orderRepository.findCountByStep(OrderStatus.IN_PROGRESS).stream().collect(Collectors
                .toMap(StepCountProjection::getStep, row -> row.getCount().intValue()));

        List<Integer> stepsCount = Arrays.stream(StepType.values())
                .map(step -> stepMap.getOrDefault(step, 0))
                .toList();

        return new StepFlowInfoDto(total, progress, complete, late, stepsCount);
    }

    @Transactional
    public void create(ErpOrderDto erpOrder) {
        Order entity = new Order();
        snapshotErpInfo(entity, erpOrder);
        addAllSteps(entity);

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

        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new UnprocessableEntityException("Não é possível editar um pedido cancelado!");
        }

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

    @Transactional
    public void deleteImageById(Long id) throws IOException {
        User me = userService.authenticate();

        if (
            !me.getPosition().getName().equals("Montagem Final")
            && !me.getPosition().getName().equals("Expedição")
            && !me.getPosition().getName().equals("TI")
        ) {
            throw new ForbiddenException("Você não tem permissão para excluir essa imagem!");
        }

        pictureService.delete(id);
    }

    private void snapshotErpInfo(Order entity, ErpOrderDto order) {
        entity.setNumber(order.getNumber());
        entity.setClient(order.getClient());
        entity.setCnpj(order.getCnpj());
        entity.setSeller(order.getSalesperson());
        entity.setStartDate(order.getStartDate());
        entity.setDueDate(order.getDueDate());
        entity.setAddress(order.getAddress());
        entity.setSubtotal(order.getSubtotal());
        entity.setDiscount(order.getDiscount());
        entity.setTotal(order.getTotal());
        entity.setItems(new ArrayList<>());

        if (order.getPhone() != null) entity.setPhone(order.getPhone());

        for (ErpOrderItemDto dto : order.getItems()) {
            OrderItem item = new OrderItem();

            item.setItemCode(dto.getCode());
            item.setDescription(dto.getDescription());
            item.setUnit(dto.getUnit());
            item.setQuantity(dto.getQuantity());
            item.setProducedQuantity(dto.getProducedQuantity());
            item.setInvoicedQuantity(dto.getInvoicedQuantity());
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

        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new UnprocessableEntityException("Não é possível editar um pedido cancelado!");
        }

        OrderStep currentStep = order.getSteps()
                .stream()
                .filter(step -> step.getStep().equals(order.getCurrentStep()))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new);

        if (dto.getComment() != null && !dto.getComment().trim().isEmpty()) {
            currentStep.getMessages().add(new StepMessage(StringUtils.toMarkdown(dto.getComment()), currentStep, me));
        }

        switch (order.getCurrentStep()) {
            case FINAL_ASSEMBLY -> {
                proccessImage(dto, currentStep);
                checkQuantity(dto, order);
            }
            case PCP -> checkCancelled(dto, order);
            case FREIGHT -> {
                if (dto.getCarrier() != null) order.setCarrier(dto.getCarrier());
                if (dto.getShippment() != null) order.setShipment(Double.parseDouble(dto.getShippment()));
            }
            case SHIPPING -> proccessImage(dto, currentStep);
        }
    }

    private void checkQuantity(OrderInputDto dto, Order order) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) return;

        // verifica se houve alteração
        boolean found = false;
        for (OrderItemInputDto input : dto.getItems()) {
            OrderItem item = order.getItems().stream().filter(currentItem -> currentItem.getId()
                    .equals(input.getId())).findFirst().orElseThrow(ResourceNotFoundException::new);

            if (input.getProducedQuantity() != null && !input.getProducedQuantity().equals(item.getProducedQuantity())) {
                found = true;
                break;
            }
            if (input.getInvoicedQuantity() != null && !input.getInvoicedQuantity().equals(item.getInvoicedQuantity())) {
                found = true;
                break;
            }
        }
        if (!found) return;

        // traz todas as ordens desse mesmo código, exceto esse
        List<Order> orders = orderRepository.findByNumber(order.getNumber(), OrderStatus.CANCELLED);
        orders = orders.stream().filter(o -> !o.getId().equals(order.getId())).toList();

        for (OrderItem item : order.getItems()) {
            Integer totalProduced = 0;

            for (Order currentOrder : orders) {
                for (OrderItem orderItem : currentOrder.getItems()) {
                    if (!orderItem.getItemCode().equals(item.getItemCode())) break;
                    totalProduced += orderItem.getProducedQuantity();
                }
            }

            OrderItemInputDto inputDto = dto.getItems().stream()
                    .filter(oii -> oii.getId().equals(item.getId()))
                    .findFirst().orElseThrow(ResourceNotFoundException::new);

            if (inputDto.getProducedQuantity() <= (item.getQuantity() - totalProduced)) {
                item.setProducedQuantity(inputDto.getProducedQuantity());
                break;
            }

            throw new UnprocessableEntityException("Essa quantidade de itens não pode ser utilizada!");
        }
    }

    private void checkCancelled(OrderInputDto dto, Order order) {
        if (dto.getCancelled() == null || !dto.getCancelled().equals("true")) return;

        order.setStatus(OrderStatus.CANCELLED);
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

    public List<OrderItemInputDto> parseItems(String itemsJson) throws IOException {
        if (itemsJson == null || itemsJson.isBlank()) return List.of();
        return objectMapper.readValue(itemsJson, new TypeReference<List<OrderItemInputDto>>() {});
    }
}
