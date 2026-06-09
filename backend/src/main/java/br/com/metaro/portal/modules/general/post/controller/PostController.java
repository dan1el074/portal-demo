package br.com.metaro.portal.modules.general.post.controller;

import br.com.metaro.portal.modules.general.post.dto.PostDto;
import br.com.metaro.portal.modules.general.post.dto.PostInsertDto;
import br.com.metaro.portal.modules.general.post.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping(value = "api/post")
public class PostController {
    @Autowired
    private PostService postService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POST')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> insert(@ModelAttribute PostInsertDto dto) throws IOException {
        PostDto newDto = postService.insert(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POST')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IOException {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
