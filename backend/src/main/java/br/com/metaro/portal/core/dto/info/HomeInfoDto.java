package br.com.metaro.portal.core.dto.info;

import br.com.metaro.portal.core.dto.event.EventDto;
import br.com.metaro.portal.modules.general.post.PostDto;
import br.com.metaro.portal.util.smb.files.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HomeInfoDto {
    private Long upcomingEvents;
    private Long openOrders;
    private Long openMemorandos;
    private List<FileDto> files;
    private EventDto event;
    private List<BirthdayDto> monthBirthdays;
    private List<BirthdayDto> todayBirthdays;
    private List<PostDto> feed;
}
