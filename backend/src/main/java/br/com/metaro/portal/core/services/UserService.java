package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.*;
import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.RoleRepository;
import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.core.repositories.projections.UserDetailsProjection;
import br.com.metaro.portal.util.picture.Picture;
import br.com.metaro.portal.util.picture.PictureService;
import br.com.metaro.portal.util.picture.PictureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserMinDto> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMinDto::new).toList();
    }

    @Transactional(readOnly = true)
    public UserEditDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("Erro ao buscar usuário por ID");
        });
        return new UserEditDto(user);
    }

    @Transactional(readOnly = true)
    public MeDto getMe() {
        User user = authenticate();

        if (user.getRoles().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            user.getRoles().clear();
            List<Role> roleList = roleRepository.findAll();

            for (Role role : roleList) {
                if (role.getAuthority().equals("ROLE_USER")) continue;
                user.getRoles().add(role);
            }
        }

        return new MeDto(user);
    }

    @Transactional
    public UserMinDto insert(UserInsertDto dto) throws IOException {
        User user = new User();
        rulesForInsert(dto, user);
        user = userRepository.save(user);
        return new UserMinDto(user);
    }

    @Transactional
    public UserMinDto update(Long id, UserInsertDto dto, String resetPicture) throws IOException {
        User user = userRepository.getReferenceById(id);
        rulesForUpdate(dto, user, resetPicture);
        user = userRepository.save(user);
        return new UserMinDto(user);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.getReferenceById(id);
        user.setActivated(false);
        userRepository.save(user);
    }

    private void rulesForUpdate(UserInsertDto dto, User entity, String resetPicture) throws IOException {
        entity.setName(dto.getName());
        entity.setPosition(dto.getPosition());
        entity.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        entity.setActivated(dto.getActivated().equals("true"));
        entity.setUpdateAt(Instant.now());
        entity.setRoles(new HashSet<>());

        if (dto.getPassword() != null) {
            String newPassword = passwordEncoder.encode(dto.getPassword());
            entity.setPassword(newPassword);
        }

        List<Long> rolesList = Arrays.stream(dto.getRoles().split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .toList();
        for (Long roleId : rolesList) {
            Role role = roleRepository.getReferenceById(roleId);
            entity.addRole(role);
        }

        if (resetPicture != null && resetPicture.equals("true")) {
            if (entity.getPicture() != null) {
                pictureService.delete(entity.getPicture().getId());
            }

            entity.setPicture(null);
            return;
        }

        if (dto.getPicture() != null)  {
            List<MultipartFile> fileList = new ArrayList<>();
            fileList.add(dto.getPicture());
            Picture picture = pictureService.saveFiles(fileList, PictureType.PROFILE).get(0);

            if (entity.getPicture() != null) {
                pictureService.delete(entity.getPicture().getId());
            }

            entity.setPicture(picture);
        }
    }

    private void rulesForInsert(UserInsertDto dto, User entity) throws IOException {
        entity.setName(dto.getName());
        entity.setPosition(dto.getPosition());
        entity.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        entity.setActivated(dto.getActivated().equals("true"));
        entity.setCreatedAt(Instant.now());
        entity.setUpdateAt(Instant.now());
        entity.setRoles(new HashSet<>());

        String newPassword = passwordEncoder.encode(dto.getPassword());
        entity.setPassword(newPassword);

        if (dto.getPicture() != null)  {
            List<MultipartFile> fileList = new ArrayList<>();
            fileList.add(dto.getPicture());
            entity.setPicture(pictureService.saveFiles(fileList, PictureType.PROFILE).get(0));
        }

        List<Long> rolesList = Arrays.stream(dto.getRoles().split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .toList();
        for (Long roleId : rolesList) {
            Role role = roleRepository.getReferenceById(roleId);
            entity.addRole(role);
        }
    }

    protected User authenticate() {
        // pega os claims do token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
        String username = jwtPrincipal.getClaim("username");

        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        List<UserDetailsProjection> projections =
                userRepository.searchUserAndRolesByUsername(userName);
        if (projections.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }
        User user = new User();
        user.setUsername(userName);
        user.setPassword(projections.getFirst().getPassword());
        for (UserDetailsProjection projection : projections) {
            user.addRole(new Role(projection.getRoleId(),projection.getAuthority(), projection.getTitle(), projection.getTitleUrl(), projection.getParent(), projection.getParentUrl()));
        }
        return user;
    }
}
