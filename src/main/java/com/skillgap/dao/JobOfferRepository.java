package com.skillgap.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skillgap.dto.SkillTotalCountDto;
import com.skillgap.dto.response.SkillCoCountDto;
import com.skillgap.dto.response.SkillStatsDto;
import com.skillgap.entity.JobOffer;
import com.skillgap.entity.enums.JobRoleTag;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

   boolean existsByExternalId(String externalId);

   @Query("SELECT j.externalId FROM JobOffer j")
   Set<String> findAllExternalIds();


   @Query(
      "SELECT new com.skillgap.dto.response.SkillStatsDto(s.name, CAST(COUNT(o) AS long)) " +
      "FROM JobOffer o " +
      "JOIN o.skills s " +
      "WHERE (:city IS NULL OR o.city = :city) " +
      "AND (:role IS NULL OR o.roleTag = :role) " +
      "GROUP BY s.name " +
      "ORDER BY COUNT(o) DESC"
   )
   List<SkillStatsDto> findTopSkillsByCityAndRole(@Param("city") String city,
                                                   @Param("role") JobRoleTag role,
                                                   Pageable pageable);

   @Query(
      "SELECT new com.skillgap.dto.response.SkillCoCountDto(s.id, s.name, CAST(COUNT(o) AS long)) " +
      "FROM JobOffer o " +
      "JOIN o.skills s " +
      "WHERE o.id IN (" +
      "    SELECT o2.id FROM JobOffer o2 JOIN o2.skills s2 " +
      "    WHERE s2.id = :baseSkillId" +
      ") " +
      "AND (:city IS NULL OR o.city = :city) " +
      "AND (:role IS NULL OR o.roleTag = :role) " +
      "AND s.id != :baseSkillId " +
      "GROUP BY s.id, s.name " +
      "ORDER BY COUNT(o) DESC"
   )
   List<SkillCoCountDto> findCoOccuringSkills(@Param("baseSkillId") Long baseSkillId,
                                             @Param("city") String city,
                                             @Param("role") JobRoleTag role,
                                             Pageable pageable);

   @Query(
      "SELECT CAST(COUNT(o) AS long) " +
      "FROM JobOffer o " +
      "JOIN o.skills s " +
      "WHERE s.id = :id " +
      "AND (:city IS NULL OR o.city = :city) " +
      "AND (:role IS NULL OR o.roleTag = :role)"
   )                                          
   long countBySkillId(@Param("id") Long id,
                     @Param("city") String city, 
                     @Param("role") JobRoleTag role);

   @Query(
      "SELECT new com.skillgap.dto.SkillTotalCountDto(s.id, s.name, CAST(COUNT(o) AS long)) " +
      "FROM JobOffer o " +
      "JOIN o.skills s " +
      "WHERE s.id IN :skillIds " +
      "AND (:city IS NULL OR o.city = :city) " +
      "AND (:role IS NULL OR o.roleTag = :role)" +
      "GROUP BY s.id "
   )
   List<SkillTotalCountDto> countForMultipleSkillsIds(
                     @Param("skillIds") List<Long> skillIds,
                     @Param("city") String city, 
                     @Param("role") JobRoleTag role);

   @Query(
      "SELECT CAST(COUNT(o) AS long) " +
      "FROM JobOffer o " +
      "WHERE (:role IS NULL OR o.roleTag = :role) " +
      "AND (:city IS NULL OR o.city = :city) "
   )
   long countOffersByRoleAndCity(
                     @Param("role") JobRoleTag role, 
                     @Param("city") String city);

   @Query(
      "SELECT new com.skillgap.dto.SkillTotalCountDto(s.id, s.name, CAST(COUNT(o) AS long)) " +
      "FROM JobOffer o " +
      "JOIN o.skills s " +
      "WHERE (:role IS NULL OR o.roleTag = :role) " +
      "AND (:city IS NULL OR o.city = :city) " +
      "GROUP BY s.id " +
      "ORDER BY COUNT(o) DESC"
   )
   List<SkillTotalCountDto> getSkillsDistribution(
                     @Param("role") JobRoleTag role, 
                     @Param("city") String city);


}
