package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ForbiddenException;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.core.services.exceptions.UnprocessableEntityException;
import br.com.metaro.portal.modules.general.stepFlow.dto.StepFlowVideoCreateDto;
import br.com.metaro.portal.modules.general.stepFlow.dto.StepFlowVideoUploadDto;
import br.com.metaro.portal.modules.general.stepFlow.entities.*;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import br.com.metaro.portal.modules.general.stepFlow.repositories.StepFlowVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StepFlowVideoService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StepFlowVideoRepository stepFlowVideoRepository;
    @Autowired
    private BunnyStreamService bunnyStreamService;
    @Autowired
    private UserService userService;

    @Transactional
    public StepFlowVideoUploadDto create(Long orderId, StepFlowVideoCreateDto dto) {
        Order order = orderRepository.findById(orderId).orElseThrow(ResourceNotFoundException::new);

        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new UnprocessableEntityException("Não é possível editar um pedido cancelado!");
        }

        OrderStep currentStep = order.getSteps().stream()
                .filter(step -> step.getStep().equals(order.getCurrentStep()))
                .findFirst().orElseThrow(ResourceNotFoundException::new);

        String bunnyVideoId = bunnyStreamService.createVideo(dto.getName());
        String viewUrl = bunnyStreamService.buildViewUrl(bunnyVideoId);

        StepFlowVideo video = new StepFlowVideo();
        video.setName(dto.getName());
        video.setBunnyVideoId(bunnyVideoId);
        video.setViewUrl(viewUrl);
        video.setStatus(VideoStatus.PENDING);
        video.setOrderStep(currentStep);

        currentStep.getVideos().add(video);
        stepFlowVideoRepository.save(video);

        BunnyStreamService.TusCredentials credentials = bunnyStreamService.generateTusCredentials(bunnyVideoId);

        StepFlowVideoUploadDto response = new StepFlowVideoUploadDto();
        response.setId(video.getId());
        response.setBunnyVideoId(bunnyVideoId);
        response.setLibraryId(bunnyStreamService.getLibraryId());
        response.setUploadEndpoint(BunnyStreamService.TUS_ENDPOINT);
        response.setAuthorizationSignature(credentials.signature());
        response.setAuthorizationExpire(credentials.expiration());
        response.setViewUrl(viewUrl);

        return response;
    }

    @Transactional
    public void complete(Long id) {
        StepFlowVideo video = stepFlowVideoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        video.setStatus(VideoStatus.READY);
        stepFlowVideoRepository.save(video);
    }

    @Transactional
    public void deleteById(Long id) {
        User me = userService.authenticate();

        if (
            !me.getPosition().getName().equals("Montagem Final")
            && !me.getPosition().getName().equals("Expedição")
            && !me.getPosition().getName().equals("TI")
        ) {
            throw new ForbiddenException("Você não tem permissão para excluir esse vídeo!");
        }

        StepFlowVideo video = stepFlowVideoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        bunnyStreamService.deleteVideo(video.getBunnyVideoId());
        stepFlowVideoRepository.delete(video);
    }
}
