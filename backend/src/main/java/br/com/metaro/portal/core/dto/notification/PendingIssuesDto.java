package br.com.metaro.portal.core.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PendingIssuesDto {
    private Long id;
    private String title;
    private String action;
    private String urgency;
}
