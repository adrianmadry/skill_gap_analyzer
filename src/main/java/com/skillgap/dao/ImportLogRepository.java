package com.skillgap.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillgap.entity.ImportLog;

public interface ImportLogRepository extends JpaRepository<ImportLog, Long> {

}
