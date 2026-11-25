package com.skillgap.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.skillgap.entity.SkillAlias;

public interface SkillAliasRepository extends JpaRepository<SkillAlias, Long> {

    @Query("SELECT sa FROM SkillAlias sa JOIN FETCH sa.skill")
    List<SkillAlias> findAllWithSkill();

    Optional<SkillAlias> findByAliasIgnoreCase(String alias);

    boolean existsByAliasIgnoreCase(String alias);
}
