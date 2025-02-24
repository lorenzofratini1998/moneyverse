package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.TagDto;
import it.moneyverse.transaction.model.dto.TagRequestDto;
import it.moneyverse.transaction.model.dto.TagUpdateRequestDto;
import java.util.List;
import java.util.UUID;

public interface TagService {
  TagDto createTag(TagRequestDto request);

  List<TagDto> getUserTags(UUID userId);

  TagDto getTagById(UUID tagId);

  TagDto updateTag(UUID tagId, TagUpdateRequestDto request);

  void deleteTag(UUID tagId);

  void deleteAllTagsByUserId(UUID userId);
}
