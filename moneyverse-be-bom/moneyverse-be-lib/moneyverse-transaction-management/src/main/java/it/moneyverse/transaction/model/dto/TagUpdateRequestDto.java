package it.moneyverse.transaction.model.dto;

import it.moneyverse.core.model.dto.StyleRequestDto;

public record TagUpdateRequestDto(String tagName, String description, StyleRequestDto style) {}
