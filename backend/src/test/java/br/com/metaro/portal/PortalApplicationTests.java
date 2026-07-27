package br.com.metaro.portal;

import br.com.metaro.portal.modules.general.stepFlow.entities.Order;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStatus;
import br.com.metaro.portal.modules.general.stepFlow.entities.OrderStep;
import br.com.metaro.portal.modules.general.stepFlow.entities.StepStatus;
import br.com.metaro.portal.modules.general.stepFlow.entities.StepType;
import br.com.metaro.portal.modules.general.stepFlow.repositories.OrderRepository;
import br.com.metaro.portal.modules.general.stepFlow.repositories.StepFlowVideoRepository;
import br.com.metaro.portal.util.video.Video;
import br.com.metaro.portal.util.video.VideoProvider;
import br.com.metaro.portal.util.video.VideoStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
		"external.datasource.jdbc-url=jdbc:h2:mem:external-testdb",
		"external.datasource.driver-class-name=org.h2.Driver",
		"external.datasource.username=sa",
		"external.datasource.password="
})
class PortalApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private StepFlowVideoRepository stepFlowVideoRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void exposesOpenApiDocumentationWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("Portal Metaro"));
	}

	@Test
	@Transactional
	void associatesReusableVideoWithStepFlow() {
		Order order = new Order();
		order.setNumber(999999);
		order.setCurrentStep(StepType.FINAL_ASSEMBLY);
		order.setStatus(OrderStatus.IN_PROGRESS);
		order.setItems(new ArrayList<>());
		order.setSteps(new ArrayList<>());

		OrderStep step = new OrderStep();
		step.setStep(StepType.FINAL_ASSEMBLY);
		step.setStatus(StepStatus.ACTIVE);
		order.addStep(step);

		Video video = new Video();
		video.setName("Vídeo de teste");
		video.setProvider(VideoProvider.BUNNY);
		video.setProviderVideoId(UUID.randomUUID().toString());
		video.setPlaybackUrl("https://example.test/video");
		video.setStatus(VideoStatus.READY);
		step.addVideo(video);

		orderRepository.saveAndFlush(order);

		assertThat(stepFlowVideoRepository.findVideoById(video.getId()))
				.contains(video);
	}
}
