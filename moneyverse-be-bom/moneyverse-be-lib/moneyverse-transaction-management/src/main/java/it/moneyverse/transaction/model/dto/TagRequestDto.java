package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.model.dto.StyleRequestDto;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TagRequestDto(
    @NotNull(message = "User ID must not be null") UUID userId,
    @NotNull(message = "Tag name must not be null") String tagName,
    String description,
    StyleRequestDto style) {}
