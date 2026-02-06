package br.com.metaro.portal.modules.general.internalCommunication.dots;

import br.com.metaro.portal.core.dto.PositionDto;
import br.com.metaro.portal.modules.general.internalCommunication.internalCommunicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InternalControlInsertDto {
    private Long request;
    private String client;
    private String item;
    private String title;
    private String description;
    private String reason;
    private internalCommunicationStatus status;
    private List<PositionDto> departments;
}
