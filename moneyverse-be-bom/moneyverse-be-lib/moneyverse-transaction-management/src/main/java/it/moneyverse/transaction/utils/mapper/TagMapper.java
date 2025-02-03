package it.moneyverse.transaction.utils.mapper;

import it.moneyverse.transaction.model.dto.TagDto;
import it.moneyverse.transaction.model.entities.Tag;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class TagMapper {

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

  private TagMapper() {}
}
