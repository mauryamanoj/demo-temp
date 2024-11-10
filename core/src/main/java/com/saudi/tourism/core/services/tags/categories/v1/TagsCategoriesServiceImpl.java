package com.saudi.tourism.core.services.tags.categories.v1;

import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.saudi.tourism.core.services.tags.categories.v1.TagsCategoriesServiceImpl.SERVICE_DESCRIPTION;

@Component(
    service = TagsCategoriesService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class TagsCategoriesServiceImpl implements TagsCategoriesService {

  /**
   * This Service description for OSGi.
   */
  static final String SERVICE_DESCRIPTION = "Categories Tags Service";

  /**
   * SaudiTourismConfigs.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * User Service.
   */
  @Reference
  private UserService userService;

  @Override
  public List<Tag> fetchAllTagsCategories(String locale) {

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var categoriesTagsRoot = resourceResolver.getResource(saudiTourismConfigs.getCategoriesTagsPath());
      if (categoriesTagsRoot == null) {
        LOGGER.error("Tags Categories Root node not found under %s", categoriesTagsRoot);
        return null;
      }
      final var childResources = categoriesTagsRoot.listChildren();
      if (!childResources.hasNext()) {
        return null;
      }
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      Locale local = new Locale(locale);

      return Stream.generate(() -> null)
          .takeWhile(x -> childResources.hasNext())
          .map(n -> childResources.next())
          .filter(Objects::nonNull)
          .map(resource -> tagManager.resolve(resource.getPath()))
          .filter(Objects::nonNull)
          .map(tag -> Tag.builder().path(tag.getTagID()).title(tag.getTitle(local)).build())
          .collect(Collectors.toList());
    }
  }
}
