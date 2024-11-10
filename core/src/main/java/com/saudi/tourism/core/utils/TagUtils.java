package com.saudi.tourism.core.utils;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.common.Category;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * The Class TagUtils.
 */
@Slf4j
public final class TagUtils {

  // Constants
  /**
   * CONTENT_DAM_TAG_ICONS.
   */
  public static final String CONTENT_DAM_TAG_ICONS = "/dam/sauditourism/tag-icons/";

  /**
   * CQ_TAGS.
   */
  public static final String CQ_TAGS = "/cq:tags/sauditourism/";

  /**
   * SVG.
   */
  public static final String SVG = ".svg";

  /**
   * Instantiates a new TagUtils.
   */
  private TagUtils() {
  }

  public static List<Category> getTagOrChildren(String tagName, TagManager tagManager,
                                                ResourceResolver resourceResolver, String language) {
    if (tagManager == null) {
      tagManager = resourceResolver.adaptTo(TagManager.class);
    }
    Tag tag = tagManager.resolve(tagName);

    if (tag == null) {
      return new ArrayList<>(); // or throw an IllegalArgumentException
    }

    return getCategories(tag, language);
  }

  private static List<Category> getCategories(Tag tag, String language) {
    List<Category> categories = new ArrayList<>();
    // Process the current tag
    categories.add(createCategoryFromTag(tag, language));
    return categories;
  }

  public static Category createCategoryFromTag(Tag tag, String language) {
    return new Category(tag.getTagID(), capitalize(tag.getTitle(Locale.forLanguageTag(language))),
        tag.getPath().replace(CQ_TAGS, CONTENT_DAM_TAG_ICONS).replace(":", "-").concat(SVG));
  }

  private static String capitalize(String str) {
    if (str == null || str.length() == 0) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  /**
   * This method aims to remove the prefix sauditourism: from tags id and make the icon path as
   * absolute url.
   *
   * @param tag             category tag object
   * @param resolver        resource resolver
   * @param settingsService sling settings service
   */
  public static void processTagsForMobile(
      Category tag, ResourceResolver resolver, SlingSettingsService settingsService) {
    processTagWithoutNamespace(tag);
    tag.setIcon(
        LinkUtils.getAuthorPublishAssetUrl(
            resolver, tag.getIcon(), settingsService.getRunModes().contains(Externalizer.PUBLISH)));
  }

  /**
   * This method aims to remove the prefix sauditourism: .
   *
   * @param tag category tag object
   */
  public static void processTagWithoutNamespace(
      Category tag) {

    String id = tag.getId();
    if (StringUtils.isNotEmpty(id) && id.contains("sauditourism:")) {
      tag.setId(StringUtils.substringAfterLast(id, ":"));
    }

  }


  /**
   * This method aims to remove the prefix sauditourism: .
   *
   * @param tag tag object
   * @return tag
   */
  public static String getTagWithoutNamespace(Tag tag) {

    String id = tag.getTagID();
    if (StringUtils.isNotEmpty(id) && id.contains("sauditourism:")) {
      id = StringUtils.substringAfterLast(id, ":");
    }

    return id;

  }
}
