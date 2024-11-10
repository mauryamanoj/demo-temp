package com.saudi.tourism.core.models.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.Constants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Region-City object.
 */
@Getter
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@NoArgsConstructor
@SuperBuilder
public class RegionCity implements Serializable {

  /**
   * id for this city.
   */
  @ValueMapValue(name = Constants.VALUE)
  private String id;

  /**
   * Name / title of this city.
   */
  @ValueMapValue(name = "text")
  private String name;

  /**
      * description / description of this city.
   */
  @Getter
  @Setter
  private String description;
  /**
   * ctaType / ctaType of this city.
   */
  @Getter
  @Setter
  private String ctaType;
  /**
   * hideFav / hideFav of this city.
   */
  @Getter
  @Setter
  private String hideFav;
  /**
   * highlight / highlight of this city.
   */
  @Getter
  @Setter
  private String highlight;
  /**
   * icon / icon of this city.
   */
  @Getter
  @Setter
  private String icon;
  /**
   * imagePosition / imagePosition of this city.
   */
  @Getter
  @Setter
  private String imagePosition;
  /**
   * isFeatured / isFeatured of this city.
   */
  @Getter
  @Setter
  private String isFeatured;
  /**
   * linkUrl of the exploreregion.
   */
  @Getter
  @Setter
  private String linkUrl;
  /**
   * linkCopy of this city.
   */
  @Getter
  @Setter
  private String linkCityUrl;
  /**
   * ImageDesktopLink of this Cities/Region.
   */
  @Getter
  @Setter
  private String imageDesktopLink;
  /**
   * ImageMobileLink of this Cities/Region.
   */
  @Getter
  @Setter
  private String imageMobileLink;
  /**
   * linkCopy of this city.
   */
  @Getter
  @Setter
  private String linkCopy;
  /**
   * type / type of this city.
   */
  @Getter
  @Setter
  private String type;
  /**
   * useActivityLink / useActivityLink of this city.
   */
  @Getter
  @Setter
  private String useActivityLink;
  /**
   * Dropdown text for this city.
   */
  @JsonIgnore
  @Setter(value = AccessLevel.PROTECTED)
  @ValueMapValue
  private transient String text;

  /**
   * Constructor.
   *
   * @param id   id
   * @param name name
   */
  public RegionCity(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Constructor.
   *
   * @param id              id
   * @param name            name
   * @param text            dropdown text
   */
  public RegionCity(@NotNull final String id, final String name, String text) {
    this.id = id;
    this.name = name;
    this.text = text;
  }

  /**
   * Copy constructor for cloning this instance.
   *
   * @param source the original RegionCity instance to be cloned
   */
  public RegionCity(final RegionCity source) {
    this(source.getId(), source.getName());

    text = source.getText();
  }

  /**
   * Checks if the current object is a region.
   *
   * @return {@code true} if the name ends with region
   */
  @JsonIgnore
  public boolean isRegion() {
    return StringUtils.endsWith(getId(), Constants.REGION_SUFFIX);
  }
}
