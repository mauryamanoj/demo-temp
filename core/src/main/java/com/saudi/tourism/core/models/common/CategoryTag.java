package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Class represents info for one tag.
 */
@Data
@NoArgsConstructor
public class CategoryTag implements Serializable {

  /**
   * Id of the tag in format: "sauditourism:events/sightseeing".
   */
  @Expose
  private String id;

  /**
   * Label for the tag, localized.
   */
  @Expose
  private String copy;

  /**
   * Icon for the tag.
   */
  @Expose
  private String icon;

  /**
   * Constructor with id.
   *
   * @param tagId tag id (path to tag)
   */
  public CategoryTag(final String tagId) {
    setId(tagId);
    // Tries to get tag name from id
    setCopy(StringUtils.substringAfterLast(tagId, Constants.FORWARD_SLASH_CHARACTER));
  }

  /**
   * Constructor with id and label.
   *
   * @param tagId   tag id (path to tag)
   * @param tagName label for the tag, localized
   */
  public CategoryTag(final String tagId, final String tagName) {
    setId(tagId);
    setCopy(tagName);
  }

  /**
   * Constructor with id, label and icon.
   *
   * @param tagId   tag id (path to tag)
   * @param tagName label for the tag, localized
   * @param icon icon name
   */
  public CategoryTag(final String tagId, final String tagName, final String icon) {
    setId(tagId);
    setCopy(tagName);
    setIcon(icon);
  }
}
