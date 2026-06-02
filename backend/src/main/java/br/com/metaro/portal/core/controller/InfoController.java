package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.info.HomeInfoDto;
import br.com.metaro.portal.core.services.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/info")
public class InfoController {
    @Autowired
    private InfoService infoService;

    @GetMapping(value = "/home")
    public ResponseEntity<HomeInfoDto> getHomeInfo() {
        HomeInfoDto dto = infoService.getHomeInfo();
        return ResponseEntity.ok(dto);
    }
}
