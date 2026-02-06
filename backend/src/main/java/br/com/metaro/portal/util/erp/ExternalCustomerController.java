package br.com.metaro.portal.util.erp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/erp/order")
public class ExternalCustomerController {
    @Autowired
    private ExternalCustomerService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping(value = "/{number}")
    public ResponseEntity<List<OrderDto>> searchByOrder(@PathVariable Integer number) {
        List<OrderDto> dtos = service.searchOrderByNumber(number);
        return ResponseEntity.ok(dtos);
    }
}
