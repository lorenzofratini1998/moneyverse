package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.TagDto;
import it.moneyverse.transaction.model.dto.TagRequestDto;
import it.moneyverse.transaction.model.dto.TagUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {
  TagDto createTag(TagRequestDto request);

  List<TagDto> getUserTags(UUID userId);

  TagDto getTagById(UUID tagId);

  Set<Tag> getTagsByIds(Set<UUID> tagIds);

  TagDto updateTag(UUID tagId, TagUpdateRequestDto request);

  void deleteTag(UUID tagId);

  void deleteAllTagsByUserId(UUID userId);
}
