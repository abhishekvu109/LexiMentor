package com.abhi.leximentor.leximentor.service;

import com.abhi.leximentor.leximentor.constants.Status;
import com.abhi.leximentor.leximentor.dto.NamedObjectDTO;
import com.abhi.leximentor.leximentor.entities.NamedObject;

import java.util.List;

public interface NamedObjectService {
    NamedObjectDTO add(NamedObjectDTO dto);

    List<NamedObjectDTO> addAll(List<NamedObjectDTO> dtos);

    List<NamedObjectDTO> find();

    NamedObjectDTO findByRefId(long refId);

    List<NamedObjectDTO> findByGenre(String genre);

    List<NamedObjectDTO> findByAlias(String alias);

    List<NamedObjectDTO> findByStatus(int status);

    NamedObjectDTO findByName(String name);

    NamedObjectDTO updateStatus(NamedObject entity, int status);

    void delete(NamedObjectDTO dto);
}
