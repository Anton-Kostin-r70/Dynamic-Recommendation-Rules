package ru.rules.dynamicRecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryDTO {
    private String query;
    private List<String> arguments;
    private boolean negate;
}
