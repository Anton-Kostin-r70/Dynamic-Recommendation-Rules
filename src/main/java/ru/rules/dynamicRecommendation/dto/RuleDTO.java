package ru.rules.dynamicRecommendation.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleDTO {
    private UUID id;
    private String productName;
    private UUID productId;
    private String productText;
    private List<QueryDTO> queriesDTO;
}
