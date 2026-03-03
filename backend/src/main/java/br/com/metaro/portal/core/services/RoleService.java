package br.com.metaro.portal.core.services;

import br.com.metaro.portal.core.dto.RoleGroupDto;
import br.com.metaro.portal.core.dto.RoleSummaryDto;
import br.com.metaro.portal.core.entities.Role;
import br.com.metaro.portal.core.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<RoleGroupDto> findAll() {
        List<Role> roles = roleRepository.findAll();
        List<RoleGroupDto> roleGroupDtos = new ArrayList<>();

        for (Role role : roles) {
            if (role.getTitle() == null) continue;

            boolean find = false;
            for (RoleGroupDto currentGroup: roleGroupDtos) {
                if (currentGroup.getTitle().equals(role.getParent())) {
                    currentGroup.getChildrens().add(new RoleSummaryDto(role.getId(), role.getTitle()));
                    find = true;
                    break;
                }
            }

            if (!find) {
                List<RoleSummaryDto> summaryDtoList = new ArrayList<>();
                summaryDtoList.add(new RoleSummaryDto(role.getId(), role.getTitle()));
                roleGroupDtos.add(new RoleGroupDto(role.getParent(), summaryDtoList));
            }
        }

        return roleGroupDtos;
    }
}
