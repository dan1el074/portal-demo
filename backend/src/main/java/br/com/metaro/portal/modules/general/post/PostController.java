package br.com.metaro.portal.modules.general.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "api/post")
public class PostController {
    @Autowired
    private PostService postService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<PostDto>> findAll(Pageable pageable) {
        Page<PostDto> page = postService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POST')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PostDto> findById(@PathVariable Long id) {
        PostDto post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_POST')")
    @PostMapping
    public ResponseEntity<PostDto> insert(
            @RequestPart("content") String content,
            @RequestPart(name = "files", required = false)  List<MultipartFile> files
    ) throws IOException {
        PostDto newDto = postService.insert(content, files);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newDto);
    }
}
