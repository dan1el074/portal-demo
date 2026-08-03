package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.role.RoleGroupDto;
import br.com.metaro.portal.core.dto.role.RoleSummaryDto;
import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<RoleGroupDto> findAll() {
        List<Role> roles = roleRepository.findAll();
        Map<Long, RoleSummaryDto> nodes = new LinkedHashMap<>();
        Map<String, List<RoleSummaryDto>> groups = new LinkedHashMap<>();

        for (Role role : roles) {
            if (role.getTitle() == null) continue;
            nodes.put(role.getId(), new RoleSummaryDto(role.getId(), role.getTitle()));
        }

        for (Role role : roles) {
            RoleSummaryDto node = nodes.get(role.getId());
            if (node == null) continue;

            Role father = role.getFather();
            RoleSummaryDto fatherNode = father == null ? null : nodes.get(father.getId());
            if (fatherNode != null) {
                fatherNode.getChildrens().add(node);
                continue;
            }

            groups.computeIfAbsent(role.getParent(), key -> new ArrayList<>()).add(node);
        }

        return groups.entrySet().stream()
                .map(entry -> new RoleGroupDto(entry.getKey(), entry.getValue()))
                .toList();
    }
}
