package com.skillgap.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillgap.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {

}
