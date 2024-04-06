package com.abhi.synapster.inventory.repository;

import com.abhi.synapster.inventory.entities.Subject;
import com.abhi.synapster.inventory.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    public Topic findByRefId(long refId);

    public List<Topic> findBySubject(Subject subject);

    public Topic findByName(String name);
}
