package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.*;
import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.entities.User;
import br.com.metaro.portal.core.repositories.RoleRepository;
import br.com.metaro.portal.core.repositories.UserRepository;
import br.com.metaro.portal.core.repositories.projections.UserDetailsProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<UserMinDto> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMinDto::new).toList();
    }

    @Transactional(readOnly = true)
    public MeDto getMe() {
        User user = authenticate();
        return new MeDto(user);
    }

    @Transactional
    public UserDto insert(UserInsertDto dto) {
        User user = new User();
        dtoToEntity(dto, user);
        user = userRepository.save(user);
        return new UserDto(user);
    }

    private void dtoToEntity(UserInsertDto dto, User entity) {
        entity.setName(dto.getName());
        entity.setPosition(dto.getPosition());
        entity.setBirthDate(dto.getBirthDate());
        entity.setEmail(dto.getEmail());
        entity.setPicture(dto.getPicture());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setActivated(true);
        entity.setCreatedAt(Instant.now());
        entity.setUpdateAt(Instant.now());
        entity.setRoles(new HashSet<>());

        for (Long roleId : dto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleId);
            entity.addRole(role);
        }
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
            user.addRole(new
                    Role(projection.getRoleId(),projection.getAuthority(), projection.getTitle(), projection.getParent()));
        }
        return user;
    }

    protected User authenticate() {
        // pega os claims do token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
        String username = jwtPrincipal.getClaim("username");

        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }
}
