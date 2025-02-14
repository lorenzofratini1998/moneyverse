package it.moneyverse.transaction.utils.mapper;

import it.moneyverse.transaction.model.dto.TagDto;
import it.moneyverse.transaction.model.dto.TagRequestDto;
import it.moneyverse.transaction.model.dto.TagUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TagMapper {

  public static List<TagDto> toTagDto(List<Tag> tags) {
    if (tags == null || tags.isEmpty()) {
      return Collections.emptyList();
    }
    return tags.stream().map(TagMapper::toTagDto).toList();
  }

  public static Set<TagDto> toTagDto(Set<Tag> tags) {
    if (tags == null || tags.isEmpty()) {
      return Collections.emptySet();
    }
    return tags.stream().map(TagMapper::toTagDto).collect(Collectors.toSet());
  }

  public static TagDto toTagDto(Tag tag) {
    if (tag == null) {
      return null;
    }
    return TagDto.builder()
        .withTagId(tag.getTagId())
        .withUserId(tag.getUserId())
        .withTagName(tag.getTagName())
        .withDescription(tag.getDescription())
        .build();
  }

  public static Tag toTag(TagRequestDto request) {
    if (request == null) {
      return null;
    }
    Tag tag = new Tag();
    tag.setUserId(request.userId());
    tag.setTagName(request.tagName());
    tag.setDescription(request.description());
    return tag;
  }

  public static Tag partialUpdate(Tag tag, TagUpdateRequestDto request) {
    if (request == null) {
      return tag;
    }
    if (request.tagName() != null) {
      tag.setTagName(request.tagName());
    }
    if (request.description() != null) {
      tag.setDescription(request.description());
    }
    return tag;
  }

  private TagMapper() {}
}
