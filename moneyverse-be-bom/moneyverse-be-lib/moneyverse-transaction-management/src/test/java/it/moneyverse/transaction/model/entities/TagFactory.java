package it.moneyverse.transaction.model.entities;

import static it.moneyverse.test.utils.FakeUtils.MAX_TAGS_PER_USER;
import static it.moneyverse.test.utils.FakeUtils.MIN_TAGS_PER_USER;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(TagFactory.class);

  public static List<Tag> createTags(List<UserModel> users) {
    List<Tag> tags = new ArrayList<>();
    for (UserModel user : users) {
      int numTagsPerUser = RandomUtils.randomInteger(MIN_TAGS_PER_USER, MAX_TAGS_PER_USER);
      for (int i = 0; i < numTagsPerUser; i++) {
        tags.add(fakeTag(user.getUserId()));
      }
    }
    LOGGER.info("Created {} random tags for testing", tags.size());
    return tags;
  }

  public static Tag fakeTag(UUID userId) {
    Tag tag = new Tag();
    tag.setTagId(RandomUtils.randomUUID());
    tag.setUserId(userId);
    tag.setTagName(RandomUtils.randomString(10));
    tag.setDescription(RandomUtils.randomString(30));
    return tag;
  }
}
