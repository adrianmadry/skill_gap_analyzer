package com.skillgap.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.skillgap.entity.JobOffer;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    boolean existsByExternalId(String externalId);

    @Query("SELECT j.externalId FROM JobOffer j")
    Set<String> findAllExternalIds();

}
