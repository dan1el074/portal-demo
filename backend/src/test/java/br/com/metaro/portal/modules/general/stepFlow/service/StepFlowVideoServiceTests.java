package br.com.metaro.portal.modules.general.stepFlow.service;

import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.modules.general.stepFlow.dto.StepFlowVideoCreateDto;
import br.com.metaro.portal.modules.general.stepFlow.entities.Order;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStatus;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStep;
import br.com.metaro.portal.modules.general.stepFlow.entities.StepType;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import br.com.metaro.portal.modules.general.stepFlow.repositories.StepFlowVideoRepository;
import br.com.metaro.portal.util.video.Video;
import br.com.metaro.portal.util.video.VideoService;
import br.com.metaro.portal.util.video.dto.VideoUploadDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepFlowVideoServiceTests {

    @Test
    void shouldPrefixOriginalVideoNameWithEvidence() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        StepFlowVideoRepository stepFlowVideoRepository = mock(StepFlowVideoRepository.class);
        VideoService videoService = mock(VideoService.class);
        UserService userService = mock(UserService.class);
        StepFlowVideoService service = new StepFlowVideoService(
                orderRepository,
                stepFlowVideoRepository,
                videoService,
                userService
        );

        OrderStep currentStep = new OrderStep();
        currentStep.setStep(StepType.FINAL_ASSEMBLY);

        Order order = new Order();
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setCurrentStep(StepType.FINAL_ASSEMBLY);
        order.setSteps(List.of(currentStep));

        Video video = new Video();
        VideoUploadDto upload = mock(VideoUploadDto.class);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(videoService.createPendingVideo("EVIDENCIA_montagem.mp4")).thenReturn(video);
        when(videoService.createUploadInstructions(video)).thenReturn(upload);

        VideoUploadDto result = service.createVideoUpload(
                1L,
                new StepFlowVideoCreateDto("montagem.mp4")
        );

        assertThat(result).isSameAs(upload);
        assertThat(currentStep.getVideos()).containsExactly(video);
        verify(videoService).createPendingVideo("EVIDENCIA_montagem.mp4");
    }
}
