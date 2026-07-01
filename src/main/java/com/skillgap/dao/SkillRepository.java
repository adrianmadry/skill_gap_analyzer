package com.skillgap.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skillgap.entity.Skill;
import com.skillgap.entity.enums.JobRoleTag;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByName(String name);

    Optional<Skill> findByNameIgnoreCase(String name);

    List<Skill> findAllByNameInIgnoreCase(Set<String> skillNames);

    List<Skill> findAllByIdIn(Set<Long> skillIds);

    @Query("SELECT s.id FROM Skill s WHERE LOWER(s.name) IN :names")
    List<Long> findIdsByNamesIgnoreCase(@Param("names") List<String> userSkills);

    @Query(
      "SELECT CAST(COUNT(DISTINCT o) AS long) " +
      "FROM JobOffer o " +
      "JOIN o.skills s1 " +
      "JOIN o.skills s2 " +
      "WHERE s1.id = :missingSkill " +
      "AND s2.id IN :userSkills " +
      "AND (:city IS NULL OR o.city = :city) " +
      "AND (:role IS NULL OR o.roleTag = :role)"
    )
    long findRecommendationScore(
                @Param("missingSkill") Long missingskillId, 
                @Param("userSkills") Set<Long> userSkillsIds, 
                @Param("role") JobRoleTag roleTag, 
                @Param("city") String city
    );

    @Query(
      "SELECT s1.id, CAST(COUNT(DISTINCT o) AS long) " +
      "FROM JobOffer o " +
      "JOIN o.skills s1 " +
      "JOIN o.skills s2 " +
      "WHERE s1.id IN :missingSkills " +
      "AND s2.id IN :userSkills " +
      "AND (:city IS NULL OR o.city = :city) " +
      "AND (:role IS NULL OR o.roleTag = :role)" +
      "GROUP BY s1.id"
    )
    List<Object[]> findRecommendationScoresBatch(
                      @Param("missingSkills") List<Long> missingSkillsIds, 
                      @Param("userSkills") Set<Long> userSkillsIds, 
                      @Param("role") JobRoleTag roleTag, 
                      @Param("city") String city
    );


}
