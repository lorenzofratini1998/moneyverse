package it.moneyverse.transaction.model;

import static it.moneyverse.test.model.TestFactory.MAX_TAGS_PER_USER;
import static it.moneyverse.test.model.TestFactory.MIN_TAGS_PER_USER;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TagRequestDto;
import it.moneyverse.transaction.model.dto.TagUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagTestFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(TagTestFactory.class);
  private static final Supplier<String> FAKE_TAG_NAME_SUPPLIER = () -> RandomUtils.randomString(10);
  private static final Supplier<String> FAKE_DESCRIPTION_SUPPLIER =
      () -> RandomUtils.randomString(30);

  public static List<Tag> createTags(List<UserModel> users) {
    List<Tag> tags =
        users.stream().map(user -> createUserTags(user.getUserId())).flatMap(List::stream).toList();
    LOGGER.info("Created {} random tags for testing", tags.size());
    return tags;
  }

  private static List<Tag> createUserTags(UUID userId) {
    return IntStream.range(0, RandomUtils.randomInteger(MIN_TAGS_PER_USER, MAX_TAGS_PER_USER))
        .mapToObj(i -> fakeTag(userId))
        .toList();
  }

  public static Set<Tag> fakeTags(UUID userId) {
    int tagsCount = RandomUtils.randomInteger(1, 3);
    Set<Tag> tags = new HashSet<>();
    for (int i = 0; i < tagsCount; i++) {
      tags.add(fakeTag(userId));
    }
    return tags;
  }

  public static Tag fakeTag(UUID userId) {
    Tag tag = new Tag();
    tag.setTagId(RandomUtils.randomUUID());
    tag.setUserId(userId);
    tag.setTagName(FAKE_TAG_NAME_SUPPLIER.get());
    tag.setDescription(FAKE_DESCRIPTION_SUPPLIER.get());
    tag.setCreatedAt(LocalDateTime.now());
    tag.setCreatedBy(TestFactory.FAKE_USER);
    tag.setUpdatedAt(LocalDateTime.now());
    tag.setUpdatedBy(TestFactory.FAKE_USER);
    return tag;
  }

  public static TagRequestDto fakeTagRequest(UUID userId) {
    return new TagRequestDto(userId, FAKE_TAG_NAME_SUPPLIER.get(), FAKE_DESCRIPTION_SUPPLIER.get());
  }

  public static TagUpdateRequestDto fakeTagUpdateRequest() {
    return new TagUpdateRequestDto(FAKE_TAG_NAME_SUPPLIER.get(), FAKE_DESCRIPTION_SUPPLIER.get());
  }
}
