package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.postgresql.hostchooser.HostRequirement.any;

import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.services.SseEventService;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.TagTestFactory;
import it.moneyverse.transaction.model.dto.TagDto;
import it.moneyverse.transaction.model.dto.TagRequestDto;
import it.moneyverse.transaction.model.dto.TagUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.utils.mapper.TagMapper;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagManagementServiceTest {

  @InjectMocks private TagManagementService tagManagementService;
  @Mock private TagRepository tagRepository;
  @Mock private SecurityService securityService;
  @Mock private SseEventService eventService;
  private MockedStatic<TagMapper> tagMapper;

  @BeforeEach
  void setUp() {
    tagMapper = Mockito.mockStatic(TagMapper.class);
  }

  @AfterEach
  void tearDown() {
    tagMapper.close();
  }

  @Test
  void givenTagRequestDto_whenCreateTag_thenReturnTagDto(@Mock Tag tag, @Mock TagDto tagDto) {
    UUID userId = RandomUtils.randomUUID();
    TagRequestDto request = TagTestFactory.fakeTagRequest(userId);
    when(tagRepository.existsByTagNameAndUserId(request.tagName(), userId)).thenReturn(false);
    tagMapper.when(() -> TagMapper.toTag(request)).thenReturn(tag);
    when(tagRepository.save(tag)).thenReturn(tag);
    tagMapper.when(() -> TagMapper.toTagDto(tag)).thenReturn(tagDto);
    when(securityService.getAuthenticatedUserId()).thenReturn(userId);

    tagDto = tagManagementService.createTag(request);

    assertNotNull(tagDto);
    verify(tagRepository, times(1)).existsByTagNameAndUserId(request.tagName(), userId);
    verify(tagRepository, times(1)).save(tag);
  }

  @Test
  void givenTagRequestDto_whenCreateTag_thenTagAlreadyExists() {
    UUID userId = RandomUtils.randomUUID();
    TagRequestDto request = TagTestFactory.fakeTagRequest(userId);
    when(tagRepository.existsByTagNameAndUserId(request.tagName(), userId)).thenReturn(true);

    assertThrows(
        ResourceAlreadyExistsException.class, () -> tagManagementService.createTag(request));

    verify(tagRepository, times(1)).existsByTagNameAndUserId(request.tagName(), userId);
    verify(tagRepository, never()).save(any(Tag.class));
  }

  @Test
  void givenTagUpdateRequestDto_WhenUpdateTag_thenReturnTagDto(@Mock Tag tag, @Mock TagDto tagDto) {
    UUID tagId = RandomUtils.randomUUID();
    UUID userId = RandomUtils.randomUUID();
    TagUpdateRequestDto request = TagTestFactory.fakeTagUpdateRequest();
    when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
    when(tag.getUserId()).thenReturn(userId);
    when(tagRepository.existsByTagNameAndUserIdAndTagIdNot(request.tagName(), userId, tagId))
        .thenReturn(false);
    tagMapper.when(() -> TagMapper.partialUpdate(tag, request)).thenReturn(tag);
    when(tagRepository.save(tag)).thenReturn(tag);
    tagMapper.when(() -> TagMapper.toTagDto(tag)).thenReturn(tagDto);
    when(securityService.getAuthenticatedUserId()).thenReturn(userId);

    tagDto = tagManagementService.updateTag(tagId, request);

    assertNotNull(tagDto);
    verify(tagRepository, times(1)).findById(tagId);
    verify(tagRepository, times(1))
        .existsByTagNameAndUserIdAndTagIdNot(request.tagName(), userId, tagId);
    verify(tagRepository, times(1)).save(tag);
  }

  @Test
  void givenTagUpdateRequestDto_WhenUpdateTag_thenTagAlreadyExists(@Mock Tag tag) {
    UUID tagId = RandomUtils.randomUUID();
    UUID userId = RandomUtils.randomUUID();
    TagUpdateRequestDto request = TagTestFactory.fakeTagUpdateRequest();
    when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
    when(tag.getUserId()).thenReturn(userId);
    when(tagRepository.existsByTagNameAndUserIdAndTagIdNot(request.tagName(), userId, tagId))
        .thenReturn(true);

    assertThrows(
        ResourceAlreadyExistsException.class, () -> tagManagementService.updateTag(tagId, request));

    verify(tagRepository, times(1)).findById(tagId);
    verify(tagRepository, times(1))
        .existsByTagNameAndUserIdAndTagIdNot(request.tagName(), userId, tagId);
    verify(tagRepository, never()).save(any(Tag.class));
  }

  @Test
  void givenTagUpdateRequestDto_WhenUpdateTag_thenTagNotFound() {
    UUID tagId = RandomUtils.randomUUID();
    UUID userId = RandomUtils.randomUUID();
    TagUpdateRequestDto request = TagTestFactory.fakeTagUpdateRequest();
    when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> tagManagementService.updateTag(tagId, request));

    verify(tagRepository, times(1)).findById(tagId);
    verify(tagRepository, never()).existsByTagNameAndUserId(request.tagName(), userId);
    verify(tagRepository, never()).save(any(Tag.class));
  }

  @Test
  void givenTagIds_WhenGetTagsByIds_thenReturnTags(@Mock Tag tag) {
    UUID tagId = RandomUtils.randomUUID();
    when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
    Set<Tag> result = tagManagementService.getTagsByIds(Set.of(tagId));

    assertEquals(1, result.size());
    verify(tagRepository, times(1)).findById(tagId);
  }

  @Test
  void givenEmptyTagIds_WhenGetTagsByIds_thenReturnEmptySet() {
    Set<Tag> result = tagManagementService.getTagsByIds(Set.of());

    assertEquals(0, result.size());
    verify(tagRepository, never()).findById(any(UUID.class));
  }
}
