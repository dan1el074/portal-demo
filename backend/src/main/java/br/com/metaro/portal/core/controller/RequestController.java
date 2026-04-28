package br.com.metaro.portal.core.controller;

import br.com.metaro.portal.core.dto.request.RequestDto;
import br.com.metaro.portal.core.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/request-access")
public class RequestController {
    @Autowired
    private RequestService requestService;

    @PostMapping
    public ResponseEntity<Void> requestNewAccess(@RequestBody RequestDto dto) throws Exception {
        requestService.requestNewAccess(dto);
        return ResponseEntity.ok().build();
    }
}
