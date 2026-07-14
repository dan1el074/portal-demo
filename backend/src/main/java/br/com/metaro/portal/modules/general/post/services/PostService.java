package br.com.metaro.portal.modules.general.post.services;

import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.core.services.UserService;
import br.com.metaro.portal.core.services.exceptions.ResourceNotFoundException;
import br.com.metaro.portal.modules.general.post.dto.PostDto;
import br.com.metaro.portal.modules.general.post.dto.PostInsertDto;
import br.com.metaro.portal.modules.general.post.entities.Post;
import br.com.metaro.portal.modules.general.post.repositories.PostRepository;
import br.com.metaro.portal.util.picture.Picture;
import br.com.metaro.portal.util.picture.PictureService;
import br.com.metaro.portal.util.picture.PictureType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private UserService userService;
    @PersistenceContext
    private EntityManager entityManager;

    private String imgPath = "assets/others/";

    @Transactional(readOnly = true)
    public List<PostDto> getFeed(Long lastId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postRepository.findBeforeId(lastId, pageable);

        return posts.stream().map(PostDto::new).toList();
    }

    @CacheEvict(value = "homeInfo", allEntries = true)
    @Transactional
    public PostDto insert(PostInsertDto dto) throws IOException {
        Post post = new Post();
        post = rulesForInsert(dto, post);
        return new PostDto(post);
    }

    @CacheEvict(value = "homeInfo", allEntries = true)
    @Transactional
    public void delete(Long id) throws IOException {
        Post post = postRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        List<Picture> pictures = new ArrayList<>(post.getPictures());

        for (Picture picture : pictures) {
            pictureService.delete(picture.getId());
            entityManager.detach(picture);
        }

        post.getPictures().clear();
        postRepository.deleteById(id);
    }

    private Post rulesForInsert(PostInsertDto dto, Post post) throws IOException {
        Instant now = Instant.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        User me = userService.authenticate();
        post.setAuthor(me);

        if (dto.getIsWarning().equals("true")) post.setIsWarning(true);
        if (!dto.getText().isEmpty()) post.setContent(formatPostContent(dto.getText()));

        post = postRepository.save(post);

        if (dto.getImages() != null)  {
            List<MultipartFile> fileList = new ArrayList<>();

            for (MultipartFile file : dto.getImages()) {
                fileList.add(file);
            }

            post.setPictures(pictureService.savePostImages(fileList, post));
        }

        return post;
    }

    private String formatPostContent(String text) {
        if (text == null || text.isBlank()) return text;

        text = text.replaceAll("(?<!\\*)\\*(.+?)\\*(?!\\*)", "<strong>$1</strong>");
        text = text.replace("\r\n", "\n").replace("\n", "<br>");

        return text;
    }
}
