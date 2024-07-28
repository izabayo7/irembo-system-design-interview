package rw.companyz.useraccountms.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.companyz.useraccountms.exceptions.DuplicateRecordException;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.models.Privilege;
import rw.companyz.useraccountms.models.dtos.CreatePrivilegeDTO;
import rw.companyz.useraccountms.models.dtos.DeletePrivilegeDTO;
import rw.companyz.useraccountms.repositories.IPrivilegeRepository;
import rw.companyz.useraccountms.services.IPrivilegeService;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class PrivilegeServiceImpl implements IPrivilegeService {

    private final IPrivilegeRepository privilegeRepository;

    public static final String ENTITY = "Privilege";


    @Override
    @Cacheable("privileges")
    public List<Privilege> getAll() {
        return this.privilegeRepository.findAll();
    }

    @Override
    public Privilege create(CreatePrivilegeDTO dto) throws DuplicateRecordException, ResourceNotFoundException {
        Optional<Privilege> duplicateName = this.privilegeRepository.findByName(dto.getName());
        if (duplicateName.isPresent())
            throw new DuplicateRecordException("Privilege", "name", dto.getName());

        var operation  = Privilege.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        privilegeRepository.save(operation);
        return operation;
    }

    @Override
    public void delete(DeletePrivilegeDTO dto) throws ResourceNotFoundException {
        for (String privilege: dto.getPrivileges()) {
            Privilege privilege1 = this.getByName(privilege);
            this.privilegeRepository.delete(privilege1);
        }
    }
    @Override
    @Cacheable("paginatedPrivileges")
    public Page<Privilege> getAllPaginated(Pageable pageable) {
        return this.privilegeRepository.findAll(pageable);
    }


    @Override
    public Page<Privilege> searchAll(String query, Pageable pageable) {
        return this.privilegeRepository.searchAll(query, pageable);
    }

    @Override
    public Privilege getById(UUID id) throws ResourceNotFoundException {
        return this.privilegeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ENTITY, "id", id));
    }

    @Override
    public Privilege getByName(String name) throws ResourceNotFoundException {
        return this.privilegeRepository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException(ENTITY, "name", name));
    }

}
