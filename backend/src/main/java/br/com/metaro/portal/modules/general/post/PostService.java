package br.com.metaro.portal.modules.general.post;

import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.util.File;
import br.com.metaro.portal.util.FileService;
import br.com.metaro.portal.util.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;

    private String imgPath = "assets/others/";

    @Transactional(readOnly = true)
    public Page<PostDto> findAll(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostDto::new);
    }

    @Transactional(readOnly = true)
    public PostDto findById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        return new PostDto(post.get());
    }

    @Transactional
    public PostDto insert(String content, List<MultipartFile> files) throws IOException {
        Instant now = Instant.now();

        Post post = new Post();
        if (files != null) post.setFiles(fileService.save(files, FileType.POST));
        post.setContent(content);
        post.setAuthor(userRepository.getReferenceById(userService.getMe().getId()));
        post.setCreatedAt(now);
        post.setUpdateAt(now);
        post = postRepository.save(post);

        List<File> fileList = new ArrayList<>();
        for (File file : post.getFiles()) {
            file.setPost(post);
            fileList.add(file);
        }

        post.setFiles(fileList);
        fileService.setPost(fileList);

        return new PostDto(post);
    }
}
