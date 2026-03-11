package com.skillgap.dto.external;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  /// TODO
public class JobOfferDto {
    private String guid;
    private String title;
    private List<String> requiredSkills;
    private String workplaceType;
    private String experienceLevel;
    private String city;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant publishedAt;

    private List<EmploymentTypeDto> employmentTypes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmploymentTypeDto {
        private BigDecimal from;
        private BigDecimal to;
        private String currency;
    }


}
