package com.abhi.leximentor.leximentor.service;

import com.abhi.leximentor.leximentor.dto.NamedObjectDTO;
import com.abhi.leximentor.leximentor.entities.NamedObject;
import com.abhi.leximentor.leximentor.mapper.NamedObjectMapper;
import com.abhi.leximentor.leximentor.repository.NamedObjectRepository;
import com.abhi.leximentor.leximentor.service.base.AbstractApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NamedObjectServiceImpl extends AbstractApplicationService implements NamedObjectService {
    private final NamedObjectRepository namedObjectRepository;
    private final NamedObjectMapper namedObjectMapper;

    @Override
    public NamedObjectDTO add(NamedObjectDTO dto) {
        log.info("Adding named object. name={}", dto == null ? null : dto.getName());
        NamedObjectDTO response = namedObjectMapper.toDto(namedObjectRepository.save(namedObjectMapper.toEntity(dto)));
        log.info("Added named object. key={}", response.getKey());
        return response;
    }

    @Override
    public List<NamedObjectDTO> addAll(List<NamedObjectDTO> dtos) {
        log.info("Adding named objects. count={}", dtos == null ? 0 : dtos.size());
        List<NamedObject> entities = namedObjectMapper.toEntityList(dtos);
        entities = namedObjectRepository.saveAll(entities);
        List<NamedObjectDTO> response = namedObjectMapper.toDtoList(entities);
        log.info("Added named objects. count={}", response.size());
        return response;
    }

    @Override
    public List<NamedObjectDTO> find() {
        log.info("Fetching all named objects");
        List<NamedObjectDTO> response = namedObjectMapper.toDtoList(namedObjectRepository.findAll());
        log.info("Fetched named objects. count={}", response.size());
        return response;
    }

    @Override
    public NamedObjectDTO findByKey(String key) {
        log.info("Fetching named object by key={}", key);
        NamedObject namedObject = requireEntity(namedObjectRepository.findByKey(key), "Named object not found for refId: " + key);
        NamedObjectDTO response = namedObjectMapper.toDto(namedObject);
        log.info("Fetched named object by key={}", key);
        return response;
    }

    @Override
    public List<NamedObjectDTO> findByGenre(String genre) {
        log.info("Fetching named objects by genre={}", genre);
        List<NamedObject> entities = namedObjectRepository.findByGenre(genre);
        List<NamedObjectDTO> response = namedObjectMapper.toDtoList(entities);
        log.info("Fetched named objects by genre. count={}", response.size());
        return response;
    }

    @Override
    public List<NamedObjectDTO> findByAlias(String alias) {
        log.info("Fetching named objects by alias={}", alias);
        List<NamedObject> entities = namedObjectRepository.findByAlias(alias);
        List<NamedObjectDTO> response = namedObjectMapper.toDtoList(entities);
        log.info("Fetched named objects by alias. count={}", response.size());
        return response;
    }

    @Override
    public List<NamedObjectDTO> findByStatus(int status) {
        log.info("Fetching named objects by status={}", status);
        List<NamedObject> entities = namedObjectRepository.findByStatus(status);
        List<NamedObjectDTO> response = namedObjectMapper.toDtoList(entities);
        log.info("Fetched named objects by status. count={}", response.size());
        return response;
    }

    @Override
    public NamedObjectDTO findByName(String name) {
        log.info("Fetching named object by name={}", name);
        NamedObject namedObject = requireEntity(namedObjectRepository.findByName(name), "Named object not found for name: " + name);
        NamedObjectDTO response = namedObjectMapper.toDto(namedObject);
        log.info("Fetched named object by name={}", name);
        return response;
    }

    @Override
    public NamedObjectDTO updateStatus(NamedObject entity, int status) {
        log.info("Updating named object status. key={}, status={}", entity == null ? null : entity.getKey(), status);
        entity.setStatus(status);
        NamedObjectDTO response = namedObjectMapper.toDto(namedObjectRepository.save(entity));
        log.info("Updated named object status. refId={}, status={}", response.getKey(), status);
        return response;
    }

    @Override
    public void delete(NamedObjectDTO dto) {
        log.info("Deleting named object. key={}", dto == null ? null : dto.getKey());
        NamedObject namedObject = requireEntity(namedObjectRepository.findByKey(dto.getKey()), "Named object not found for refId: " + dto.getKey());
        namedObjectRepository.delete(namedObject);
        log.info("Deleted named object. refId={}", dto.getKey());
    }


}
