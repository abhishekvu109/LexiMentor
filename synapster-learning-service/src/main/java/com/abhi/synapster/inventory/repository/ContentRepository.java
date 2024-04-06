package com.abhi.synapster.inventory.repository;

import com.abhi.synapster.inventory.entities.Content;
import com.abhi.synapster.inventory.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    public Content findByRefId(long refId);

    public List<Content> findByTopic(Topic topic);
}
