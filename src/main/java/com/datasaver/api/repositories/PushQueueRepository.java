package com.datasaver.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.datasaver.api.domains.PushQueue;

@Repository
public interface PushQueueRepository extends JpaRepository<PushQueue, Long> {
}