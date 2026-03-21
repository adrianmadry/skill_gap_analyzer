package com.skillgap.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.skillgap.dao.JobOfferRepository;
import com.skillgap.dto.response.JobOfferResponseDto;
import com.skillgap.mapper.ApiJobOfferMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JobOfferService {

    private final JobOfferRepository jobOfferRepository;
    private final ApiJobOfferMapper apiJobOfferMapper;

    public Page<JobOfferResponseDto> getAllOffers(Pageable pageable) {
        return jobOfferRepository.findAll(pageable)
                                    .map(offer -> apiJobOfferMapper.mapFromEntityToDto(offer));         
    }




}
