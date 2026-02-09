package br.com.metaro.portal.modules.general.internalCommunication.dots;

import br.com.metaro.portal.modules.general.internalCommunication.entities.InternalCommunicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InternalCommunicationInsertDto {
    private Long request;
    private String client;
    private String item;
    private String title;
    private String description;
    private String reason;
    private InternalCommunicationStatus status;
    private String departments;
}
