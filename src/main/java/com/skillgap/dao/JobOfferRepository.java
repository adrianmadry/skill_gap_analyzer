package com.skillgap.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillgap.entity.JobOffer;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    boolean existsByExternalId(String externalId);

}
