package com.migrosone.infrastructure.repository;

import com.migrosone.domain.model.StoreEntryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreLogRepository extends JpaRepository<StoreEntryLog, Long> {
}
