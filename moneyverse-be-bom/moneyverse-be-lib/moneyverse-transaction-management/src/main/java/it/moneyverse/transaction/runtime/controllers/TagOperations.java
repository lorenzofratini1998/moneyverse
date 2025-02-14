package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.TagDto;
import it.moneyverse.transaction.model.dto.TagRequestDto;
import it.moneyverse.transaction.model.dto.TagUpdateRequestDto;
import java.util.List;
import java.util.UUID;

public interface TagOperations {
  TagDto createTag(TagRequestDto request);

  List<TagDto> getUserTags(UUID userId);

  TagDto getTag(UUID tagId);

  TagDto updateTag(UUID tagId, TagUpdateRequestDto request);

  void deleteTag(UUID tagId);
}
