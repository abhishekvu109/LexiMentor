package com.abhi.synapster.inventory.service;

import com.abhi.synapster.inventory.dto.DocumentDTO;

import java.util.List;

public interface DocumentService {
    public DocumentDTO add(DocumentDTO documentDTO, long contentRefId);

    public List<DocumentDTO> addAll(List<DocumentDTO> documentDTOList,long contentRefId);

    public DocumentDTO getByRefId(long refId);

    public DocumentDTO getByName(String name);

    public DocumentDTO update(DocumentDTO documentDTO);

    public void delete(DocumentDTO documentDTO);

    public void deleteAll(List<DocumentDTO> documentDTOList);

}
