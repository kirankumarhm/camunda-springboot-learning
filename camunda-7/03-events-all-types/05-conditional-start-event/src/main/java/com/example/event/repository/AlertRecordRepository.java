package com.example.event.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.event.model.AlertRecord;
import com.example.event.model.AlertSeverity;

/**
 * Spring Data JPA repository for {@link AlertRecord} entities.
 *
 * <p>Provides persistence and query access to the {@code alert_records}
 * table. Spring Boot auto-configures the datasource and JPA
 * EntityManager — no manual SQL or DAO boilerplate needed.</p>
 */
@Repository
public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {

    Optional<AlertRecord> findByAuditId(String auditId);

    List<AlertRecord> findBySeverity(AlertSeverity severity);

    List<AlertRecord> findBySensorId(String sensorId);

    List<AlertRecord> findByRecordedAtAfter(Instant since);

    List<AlertRecord> findAllByOrderByRecordedAtDesc();
}
