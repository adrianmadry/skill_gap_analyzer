package com.skillgap.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillgap.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByName(String name);

    Optional<Skill> findByNameIgnoreCase(String name);

    List<Skill> findAllByNameInIgnoreCase(Set<String> skillNames);

}
