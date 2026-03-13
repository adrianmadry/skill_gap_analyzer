package com.skillgap.service.provider;

import java.util.List;

import com.skillgap.dto.external.JobOfferDto;

public interface JobOffersProvider {

    List<JobOfferDto> fetchAll();

    List<JobOfferDto> fetchByCity(String city);
}
