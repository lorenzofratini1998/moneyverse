package it.moneyverse.transaction.utils.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.TagTestFactory;
import it.moneyverse.transaction.model.dto.TagDto;
import it.moneyverse.transaction.model.entities.Tag;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TagMapperTest {

  private static final UUID USER_ID = RandomUtils.randomUUID();

  @Test
  void testToTagDto_NullTag() {
    assertNull(TagMapper.toTagDto((Tag) null));
  }

  @Test
  void testToTagDto_ValidTag() {
    Tag tag = TagTestFactory.fakeTag(USER_ID);

    TagDto tagDto = TagMapper.toTagDto(tag);

    assertEquals(tag.getTagId(), tagDto.getTagId());
    assertEquals(tag.getUserId(), tagDto.getUserId());
    assertEquals(tag.getTagName(), tagDto.getTagName());
    assertEquals(tag.getDescription(), tagDto.getDescription());
  }

  @Test
  void testToTagDtoSet_EmptyTagSet() {
    assertEquals(Collections.emptySet(), TagMapper.toTagDto(new HashSet<>()));
  }

  @Test
  void testToTagDtoSet_ValidTagSet() {
    int entitiesCount = RandomUtils.randomInteger(0, 10);
    Set<Tag> tags = new HashSet<>(entitiesCount);
    for (int i = 0; i < entitiesCount; i++) {
      tags.add(TagTestFactory.fakeTag(USER_ID));
    }

    Set<TagDto> tagDtos = TagMapper.toTagDto(tags);

    for (Tag tag : tags) {
      TagDto tagDto =
          tagDtos.stream()
              .filter(dto -> dto.getTagId().equals(tag.getTagId()))
              .findFirst()
              .orElseThrow(
                  () -> new AssertionError("TagDto with ID " + tag.getTagId() + " not found"));

      assertEquals(tag.getTagId(), tagDto.getTagId());
      assertEquals(tag.getUserId(), tagDto.getUserId());
      assertEquals(tag.getTagName(), tagDto.getTagName());
      assertEquals(tag.getDescription(), tagDto.getDescription());
    }
  }
}
