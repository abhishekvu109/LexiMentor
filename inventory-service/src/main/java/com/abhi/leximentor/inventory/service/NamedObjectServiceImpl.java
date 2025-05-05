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
        return NamedObjectBuilder.buildDTO(namedObjectRepository.save(NamedObjectBuilder.buildEntity(dto)));
    }

    @Override
    public List<NamedObjectDTO> addAll(List<NamedObjectDTO> dtos) {
        List<NamedObject> entities = dtos.stream().map(NamedObjectBuilder::buildEntity).toList();
        return entities.stream().map(NamedObjectBuilder::buildDTO).toList();
    }

    @Override
    public List<NamedObjectDTO> find() {
        return namedObjectRepository.findAll().stream().map(NamedObjectBuilder::buildDTO).toList();
    }

    @Override
    public NamedObjectDTO findByRefId(long refId) {
        return NamedObjectBuilder.buildDTO(namedObjectRepository.findByRefId(refId));
    }

    @Override
    public List<NamedObjectDTO> findByGenre(String genre) {
        List<NamedObject> entities = namedObjectRepository.findByGenre(genre);
        return entities.stream().map(NamedObjectBuilder::buildDTO).toList();
    }

    @Override
    public List<NamedObjectDTO> findByAlias(String alias) {
        List<NamedObject> entities = namedObjectRepository.findByAlias(alias);
        return entities.stream().map(NamedObjectBuilder::buildDTO).toList();
    }

    @Override
    public List<NamedObjectDTO> findByStatus(int status) {
        List<NamedObject> entities = namedObjectRepository.findByStatus(status);
        return entities.stream().map(NamedObjectBuilder::buildDTO).toList();
    }

    @Override
    public NamedObjectDTO findByName(String name) {
        return NamedObjectBuilder.buildDTO(namedObjectRepository.findByName(name));
    }

    @Override
    public NamedObjectDTO updateStatus(NamedObject entity, int status) {
        entity.setStatus(status);
        return NamedObjectBuilder.buildDTO(namedObjectRepository.save(entity));
    }

    @Override
    public void delete(NamedObjectDTO dto) {
        namedObjectRepository.delete(namedObjectRepository.findByRefId(dto.getRefId()));
    }

    static class NamedObjectBuilder {

        public static NamedObject buildEntity(NamedObjectDTO dto) {
            String tags = dto.getTags().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",", "[", "]"));
            String aliases = dto.getAliases().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",", "[", "]"));
            return NamedObject.builder().refId(KeyGeneratorUtil.refId()).name(dto.getName()).alias(aliases).tags(tags).description(dto.getDescription()).genre(dto.getGenre()).status(Status.ApplicationStatus.ACTIVE).build();
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
            return NamedObjectDTO.builder().refId(entity.getRefId()).name(entity.getName()).aliases(aliasesList).tags(tagsList).status(Status.ApplicationStatus.getStatusStr(entity.getStatus())).build();
        }
    }
}
