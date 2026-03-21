package com.skillgap.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobOfferResponseDto {

    private Long id;
    private String title;
    private String companyName;
    private String city;
    private String country;
    private String experienceLevel;
    private String workModel;
    private String description;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    private LocalDate publishedDate;
    private String offerSource;
    private Set<String> skills;
    
}
