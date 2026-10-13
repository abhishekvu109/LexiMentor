package com.abhi.leximentor.inventory.service;

import com.abhi.leximentor.inventory.constants.Status;
import com.abhi.leximentor.inventory.dto.NamedObjectDTO;
import com.abhi.leximentor.inventory.entities.NamedObject;
import com.abhi.leximentor.inventory.repository.NamedObjectRepository;
import com.abhi.leximentor.inventory.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NamedObjectServiceImpl implements NamedObjectService {
    private final NamedObjectRepository namedObjectRepository;

    @Override
    public NamedObjectDTO add(NamedObjectDTO dto) {
        log.info("Adding named object. name={}", dto == null ? null : dto.getName());
        NamedObjectDTO response = NamedObjectBuilder.buildDTO(namedObjectRepository.save(NamedObjectBuilder.buildEntity(dto)));
        log.info("Added named object. refId={}", response.getRefId());
        return response;
    }

    @Override
    public List<NamedObjectDTO> addAll(List<NamedObjectDTO> dtos) {
        log.info("Adding named objects. count={}", dtos == null ? 0 : dtos.size());
        List<NamedObject> entities = dtos.stream().map(NamedObjectBuilder::buildEntity).toList();
        entities = namedObjectRepository.saveAll(entities);
        List<NamedObjectDTO> response = entities.stream().map(NamedObjectBuilder::buildDTO).toList();
        log.info("Added named objects. count={}", response.size());
        return response;
    }

    @Override
    public List<NamedObjectDTO> find() {
        log.info("Fetching all named objects");
        List<NamedObjectDTO> response = namedObjectRepository.findAll().stream().map(NamedObjectBuilder::buildDTO).toList();
        log.info("Fetched named objects. count={}", response.size());
        return response;
    }

    @Override
    public NamedObjectDTO findByRefId(long refId) {
        log.info("Fetching named object by refId={}", refId);
        NamedObjectDTO response = NamedObjectBuilder.buildDTO(namedObjectRepository.findByRefId(refId));
        log.info("Fetched named object by refId={}", refId);
        return response;
    }

    @Override
    public List<NamedObjectDTO> findByGenre(String genre) {
        log.info("Fetching named objects by genre={}", genre);
        List<NamedObject> entities = namedObjectRepository.findByGenre(genre);
        List<NamedObjectDTO> response = entities.stream().map(NamedObjectBuilder::buildDTO).toList();
        log.info("Fetched named objects by genre. count={}", response.size());
        return response;
    }

    @Override
    public List<NamedObjectDTO> findByAlias(String alias) {
        log.info("Fetching named objects by alias={}", alias);
        List<NamedObject> entities = namedObjectRepository.findByAlias(alias);
        List<NamedObjectDTO> response = entities.stream().map(NamedObjectBuilder::buildDTO).toList();
        log.info("Fetched named objects by alias. count={}", response.size());
        return response;
    }

    @Override
    public List<NamedObjectDTO> findByStatus(int status) {
        log.info("Fetching named objects by status={}", status);
        List<NamedObject> entities = namedObjectRepository.findByStatus(status);
        List<NamedObjectDTO> response = entities.stream().map(NamedObjectBuilder::buildDTO).toList();
        log.info("Fetched named objects by status. count={}", response.size());
        return response;
    }

    @Override
    public NamedObjectDTO findByName(String name) {
        log.info("Fetching named object by name={}", name);
        NamedObjectDTO response = NamedObjectBuilder.buildDTO(namedObjectRepository.findByName(name));
        log.info("Fetched named object by name={}", name);
        return response;
    }

    @Override
    public NamedObjectDTO updateStatus(NamedObject entity, int status) {
        log.info("Updating named object status. refId={}, status={}", entity == null ? null : entity.getRefId(), status);
        entity.setStatus(status);
        NamedObjectDTO response = NamedObjectBuilder.buildDTO(namedObjectRepository.save(entity));
        log.info("Updated named object status. refId={}, status={}", response.getRefId(), status);
        return response;
    }

    @Override
    public void delete(NamedObjectDTO dto) {
        log.info("Deleting named object. refId={}", dto == null ? null : dto.getRefId());
        namedObjectRepository.delete(namedObjectRepository.findByRefId(dto.getRefId()));
        log.info("Deleted named object. refId={}", dto == null ? null : dto.getRefId());
    }

    public static class NamedObjectBuilder {

        public static NamedObject buildEntity(NamedObjectDTO dto) {
            String tags = dto.getTags().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",", "[", "]"));
            String aliases = dto.getAliases().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",", "[", "]"));
            return NamedObject.builder().refId(KeyGeneratorUtil.refId()).name(dto.getName()).alias(aliases).tags(tags).description(dto.getDescription()).genre(dto.getGenre()).status(Status.ApplicationStatus.ACTIVE).subGenre(dto.getSubGenre()).build();
        }

        public static NamedObjectDTO buildDTO(NamedObject entity) {
            List<String> tagsList = new LinkedList<>();
            List<String> aliasesList = new LinkedList<>();

            if (StringUtils.isNotEmpty(entity.getTags())) {
                String tags = entity.getTags();
                tagsList = Arrays.stream(tags.replace("[", "").replace("]", "").split(",")).toList();
            }
            if (StringUtils.isNotEmpty(entity.getAlias())) {
                String aliases = entity.getAlias();
                aliasesList = Arrays.stream(aliases.replace("[", "").replace("]", "").split(",")).toList();
            }
            return NamedObjectDTO.builder().refId(entity.getRefId()).name(entity.getName()).description(entity.getDescription()).aliases(aliasesList).tags(tagsList).status(Status.ApplicationStatus.getStatusStr(entity.getStatus())).genre(entity.getGenre()).subGenre(entity.getSubGenre()).build();
        }
    }
}
