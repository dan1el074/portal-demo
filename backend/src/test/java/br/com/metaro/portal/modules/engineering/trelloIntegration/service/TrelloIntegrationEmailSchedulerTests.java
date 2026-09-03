package br.com.metaro.portal.modules.engineering.trelloIntegration.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TrelloIntegrationEmailSchedulerTests {
    @Test
    void publishesTheBatchAndSchedulesItOneSecondAfterCommit() {
        TaskScheduler taskScheduler = mock(TaskScheduler.class);
        TrelloIntegrationEmailSender emailSender = mock(TrelloIntegrationEmailSender.class);
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        TrelloIntegrationEmailScheduler scheduler = new TrelloIntegrationEmailScheduler(
                taskScheduler, emailSender, eventPublisher
        );
        ArgumentCaptor<TrelloIntegrationEmailsReadyEvent> eventCaptor =
                ArgumentCaptor.forClass(TrelloIntegrationEmailsReadyEvent.class);

        scheduler.schedule(List.of(1L, 2L));

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getRecordIds()).containsExactly(1L, 2L);

        Instant beforeSchedule = Instant.now();
        scheduler.scheduleAfterCommit(eventCaptor.getValue());
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(taskScheduler).schedule(taskCaptor.capture(), instantCaptor.capture());
        assertThat(instantCaptor.getValue()).isAfterOrEqualTo(beforeSchedule.plusMillis(900));

        taskCaptor.getValue().run();
        verify(emailSender).sendPending(1L);
        verify(emailSender).sendPending(2L);
    }
}
