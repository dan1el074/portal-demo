package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.stepFlow.dto.StepFlowVideoCreateDto;
import br.com.metaro.portal.modules.general.stepFlow.entities.*;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import br.com.metaro.portal.modules.general.stepFlow.repositories.StepFlowVideoRepository;
import br.com.metaro.portal.util.video.Video;
import br.com.metaro.portal.util.video.VideoService;
import br.com.metaro.portal.util.video.dto.VideoUploadDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StepFlowVideoService {
    private final OrderRepository orderRepository;
    private final StepFlowVideoRepository stepFlowVideoRepository;
    private final VideoService videoService;
    private final UserService userService;

    public StepFlowVideoService(
            OrderRepository orderRepository,
            StepFlowVideoRepository stepFlowVideoRepository,
            VideoService videoService,
            UserService userService
    ) {
        this.orderRepository = orderRepository;
        this.stepFlowVideoRepository = stepFlowVideoRepository;
        this.videoService = videoService;
        this.userService = userService;
    }

    @Transactional
    public VideoUploadDto createVideoUpload(Long orderId, StepFlowVideoCreateDto dto) {
        Order order = orderRepository.findById(orderId).orElseThrow(ResourceNotFoundException::new);

        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new UnprocessableEntityException("Não é possível editar um pedido cancelado!");
        }

        OrderStep currentStep = order.getSteps().stream()
                .filter(step -> step.getStep().equals(order.getCurrentStep()))
                .findFirst().orElseThrow(ResourceNotFoundException::new);

        Video video = videoService.createPendingVideo(dto.getName());
        currentStep.addVideo(video);

        return videoService.createUploadInstructions(video);
    }

    @Transactional
    public void completeVideoUpload(Long id) {
        Video video = findStepFlowVideo(id);
        videoService.markAsReady(video);
    }

    @Transactional
    public void deleteVideo(Long id) {
        User me = userService.authenticate();

        if (
            !me.getPosition().getName().equals("Montagem Final")
            && !me.getPosition().getName().equals("Expedição")
            && !me.getPosition().getName().equals("TI")
        ) {
            throw new ForbiddenException("Você não tem permissão para excluir esse vídeo!");
        }

        videoService.delete(findStepFlowVideo(id));
    }

    private Video findStepFlowVideo(Long id) {
        return stepFlowVideoRepository.findVideoById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
