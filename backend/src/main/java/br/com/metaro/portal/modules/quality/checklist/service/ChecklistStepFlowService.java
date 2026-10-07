package br.com.metaro.portal.modules.quality.checklist.service;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.stepFlow.entities.Order;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStatus;
import br.com.metaro.portal.modules.general.stepFlow.entities.StepType;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import br.com.metaro.portal.modules.quality.checklist.dto.ChecklistSnapshotDto;
import br.com.metaro.portal.modules.quality.checklist.dto.ChecklistStepBindingDto;
import br.com.metaro.portal.modules.quality.checklist.dto.ChecklistStepRequirementDto;
import br.com.metaro.portal.modules.quality.checklist.dto.ChecklistStepEquipmentDto;
import br.com.metaro.portal.core.dto.notification.PendingIssuesDto;
import br.com.metaro.portal.modules.quality.checklist.entity.*;
import br.com.metaro.portal.modules.quality.checklist.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChecklistStepFlowService {
    private final ChecklistStepRequirementRepository requirementRepository;
    private final ChecklistStepBindingRepository bindingRepository;
    private final ChecklistCategoryRepository categoryRepository;
    private final ChecklistFlowRepository flowRepository;
    private final ChecklistRecordRepository recordRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ChecklistMapper mapper;

    @Transactional(readOnly = true)
    public List<ChecklistStepRequirementDto> listRequirements() {
        return requirementRepository.findAll().stream().map(requirement -> {
            ChecklistStepRequirementDto dto = new ChecklistStepRequirementDto();
            dto.setStepType(requirement.getStepType().name());
            dto.setCategoryId(requirement.getCategory().getId());
            return dto;
        }).toList();
    }

    @Transactional
    public ChecklistStepRequirementDto saveRequirement(ChecklistStepRequirementDto dto) {
        User user = userService.authenticate();
        StepType stepType = parseStepType(dto.getStepType());
        ChecklistCategory category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(ResourceNotFoundException::new);
        if (!category.isActive()) throw new UnprocessableEntityException("Uma categoria inativa não pode ser requisito do step-flow.");
        ChecklistStepRequirement requirement = requirementRepository.findByStepType(stepType).orElseGet(ChecklistStepRequirement::new);
        requirement.setStepType(stepType);
        requirement.setCategory(category);
        requirement.setUpdatedBy(user);
        requirement.setUpdatedAt(Instant.now());
        requirementRepository.save(requirement);
        return dto;
    }

    @Transactional
    public void removeRequirement(String stepType) {
        requirementRepository.deleteByStepType(parseStepType(stepType));
    }

    @Transactional
    public void replaceBindings(Long orderId, ChecklistStepBindingDto dto) {
        User user = userService.authenticate();
        Order order = orderRepository.findById(orderId).orElseThrow(ResourceNotFoundException::new);
        Set<Long> requested = new LinkedHashSet<>(dto.getFlowIds());
        List<ChecklistStepBinding> current = bindingRepository.findByOrderId(orderId);
        current.stream().filter(binding -> !requested.contains(binding.getFlow().getId())).forEach(bindingRepository::delete);
        for (Long flowId : requested) {
            if (bindingRepository.existsByOrderIdAndFlowId(orderId, flowId)) continue;
            ChecklistFlow flow = flowRepository.findById(flowId).orElseThrow(ResourceNotFoundException::new);
            validateBinding(order, flow);
            ChecklistStepBinding binding = new ChecklistStepBinding();
            binding.setOrder(order);
            binding.setFlow(flow);
            binding.setCreatedBy(user);
            binding.setCreatedAt(Instant.now());
            bindingRepository.save(binding);
        }
    }

    @Transactional(readOnly = true)
    public List<ChecklistStepEquipmentDto> listEquipment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(ResourceNotFoundException::new);
        Set<Long> selected = bindingRepository.findByOrderId(orderId).stream()
                .map(binding -> binding.getFlow().getId())
                .collect(java.util.stream.Collectors.toSet());
        return flowRepository.findByOrderNumberAndCancelledFalseOrderBySerialNumberAsc(String.valueOf(order.getNumber())).stream()
                .filter(flow -> flow.getSerialNumber() != null && !flow.getSerialNumber().isBlank())
                .filter(flow -> recordRepository.existsByFlowIdAndStatus(flow.getId(), ChecklistStatus.FINISHED))
                .filter(flow -> selected.contains(flow.getId())
                        || !bindingRepository.existsByFlowIdAndNonCancelledOrderOtherThan(flow.getId(), orderId))
                .map(flow -> {
                    ChecklistStepEquipmentDto dto = new ChecklistStepEquipmentDto();
                    dto.setFlowId(flow.getId());
                    dto.setSerial(flow.getSerialNumber());
                    dto.setItem(flow.getCommercialItem());
                    dto.setSelected(selected.contains(flow.getId()));
                    return dto;
                }).toList();
    }

    @Transactional(readOnly = true)
    public List<PendingIssuesDto> listAlerts() {
        User user = userService.authenticate();
        boolean operator = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CHECKLIST_OPERATOR"));
        if (!operator) return List.of();
        Map<Long, PendingIssuesDto> alerts = new LinkedHashMap<>();
        for (ChecklistStepBinding binding : bindingRepository.findByOrderStatusNot(OrderStatus.CANCELLED)) {
            StepType currentStep = binding.getOrder().getCurrentStep();
            ChecklistStepRequirement requirement = currentStep == null ? null : requirementRepository.findByStepType(currentStep).orElse(null);
            if (requirement == null || requirement.getCategory().getOperators().stream()
                    .noneMatch(authorized -> authorized.getId().equals(user.getId()))) continue;
            if (isCategoryFinished(binding.getFlow(), requirement.getCategory().getId())) continue;
            Order order = binding.getOrder();
            alerts.putIfAbsent(order.getId(), new PendingIssuesDto(
                    order.getId(),
                    "Step-flow pedido " + order.getDisplayNumber(),
                    "Finalize o checklist da categoria " + requirement.getCategory().getName() + ".",
                    "pending"
            ));
        }
        return new ArrayList<>(alerts.values());
    }

    @Transactional(readOnly = true)
    public void assertStepCanFinish(Long orderId, StepType stepType) {
        ChecklistStepRequirement requirement = requirementRepository.findByStepType(stepType).orElse(null);
        if (requirement == null) return;
        List<ChecklistStepBinding> bindings = bindingRepository.findByOrderId(orderId);
        if (bindings.isEmpty()) {
            throw new UnprocessableEntityException("Selecione os números de série dos equipamentos exigidos por esta etapa antes de finalizá-la.");
        }
        for (ChecklistStepBinding binding : bindings) {
            if (!isCategoryFinished(binding.getFlow(), requirement.getCategory().getId())) {
                throw new UnprocessableEntityException("Todos os equipamentos selecionados devem finalizar a categoria de checklist exigida antes que esta etapa possa avançar.");
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean hasActiveDependency(Long flowId) {
        return bindingRepository.existsByFlowIdAndNonCancelledOrder(flowId);
    }

    @Transactional(readOnly = true)
    public boolean categoryIsRequired(Long categoryId) {
        return requirementRepository.existsByCategoryId(categoryId);
    }

    private boolean isCategoryFinished(ChecklistFlow flow, Long categoryId) {
        List<ChecklistSnapshotDto> plan = mapper.read(flow.getPlanJson(), new TypeReference<>() {});
        ChecklistSnapshotDto required = plan.stream().filter(snapshot -> snapshot.getCategory().getId().equals(categoryId)).findFirst().orElse(null);
        if (required == null) return false;
        ChecklistRecord record = recordRepository.findByFlowIdAndTemplateId(flow.getId(), required.getTemplate().getId()).orElse(null);
        return !flow.isCancelled() && record != null && record.getStatus() == ChecklistStatus.FINISHED;
    }

    private void validateBinding(Order order, ChecklistFlow flow) {
        if (flow.isCancelled()) throw new UnprocessableEntityException("Fluxos de checklist cancelados não podem ser vinculados ao step-flow.");
        if (!Objects.equals(String.valueOf(order.getNumber()), flow.getOrderNumber())) {
            throw new UnprocessableEntityException("O equipamento do checklist pertence a outro pedido.");
        }
        if (!recordRepository.existsByFlowIdAndStatus(flow.getId(), ChecklistStatus.FINISHED)) {
            throw new UnprocessableEntityException("O equipamento precisa ter um checklist finalizado antes de iniciar o step-flow.");
        }
        if (bindingRepository.existsByFlowIdAndNonCancelledOrderOtherThan(flow.getId(), order.getId())) {
            throw new UnprocessableEntityException("O equipamento do checklist já pertence a outro processo de step-flow não cancelado.");
        }
    }

    private StepType parseStepType(String value) {
        try {
            return StepType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (RuntimeException exception) {
            throw new UnprocessableEntityException("Tipo de step-flow inválido.");
        }
    }
}
