package com.saudi.tourism.core.atfeed.models;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;
import java.util.List;

/**
 * Entity.
 * https://experienceleague.adobe.com/docs/target/using/recommendations/entities/entity-attributes.html?lang=en
 */
public class Entity {

  /** Id of entity. */
  @Getter
  @Setter
  private String id;
  /** Name of entity. */
  @Getter
  @Setter
  private String name;
  /** CategoryId of entity. */
  @Getter
  @Setter
  private String categoryId;
  /** Brand of entity. */
  @Getter
  @Setter
  private String brand;
  /** Page URL of entity. */
  @Getter
  @Setter
  private String pageUrl;
  /** Thumbnail URL of entity. */
  @Getter
  @Setter
  private String thumbnailUrl;
  /** Description of entity. */
  @Getter
  @Setter
  private String message;
  /** Inventory of entity. */
  @Getter
  @Setter
  private Integer inventory;
  /** Value of entity. */
  @Getter
  @Setter
  private Long value;
  /** Margin of entity. */
  @Getter
  @Setter
  private String  margin;
  /** last update of entity. */
  @Getter
  @Setter
  private String lastUpdatedBy;
  /** Event details of entity. */
  @Getter
  @Setter
  private Boolean eventDetails;
  /** Scene7 thumbnail URL of entity. */
  @Getter
  @Setter
  private String thumbnailS7Url;
  /** CTA text of entity. */
  @Getter
  @Setter
  private String ctaText;
  /** CTA URL of entity. */
  @Getter
  @Setter
  private String ctaURL;
  /** Area of entity. */
  @Getter
  @Setter
  private String area;
  /** Alt text of entity. */
  @Getter
  @Setter
  private String thumbnailUrlText;
  /** Custom prop of entity. */
  @Getter
  @Setter
  private String custom1;
  /** Custom prop of entity. */
  @Getter
  @Setter
  private String custom2;
  /** Latitude of entity. */
  @Getter
  @Setter
  private String custom3;
  /** Custom prop of entity. */
  @Getter
  @Setter
  private String latitude;
  /** Longitude of entity. */
  @Getter
  @Setter
  private String longitude;
  /** Custom prop of entity. */
  @Getter
  @Setter
  private List<String> interestTags;
  /** tags prop of entity. */
  @Getter
  @Setter
  private String tags;
  /** interest prop of entity. */
  @Getter
  @Setter
  private String interests;
  /** CTA Type. **/
  @Getter
  @Setter
  private String ctaType;
  /** description. **/
  @Getter
  @Setter
  private String description;
  /** region. **/
  @Getter
  @Setter
  private String region;

  @Override
  public String toString() {
    String header = StringUtils.EMPTY;
    StringBuilder sb = new StringBuilder();
    sb.append(header);
    sb.append("Entity{");
    sb.append("id='");
    sb.append(id);
    sb.append('\'');
    sb.append(", name='");
    sb.append(name);
    sb.append('\'');
    sb.append(", categoryId='");
    sb.append(categoryId);
    sb.append('\'');
    sb.append(", brand='");
    sb.append(brand);
    sb.append('\'');
    sb.append(", pageUrl='");
    sb.append(pageUrl);
    sb.append('\'');
    sb.append(", thumbnailUrl='");
    sb.append(thumbnailUrl);
    sb.append('\'');
    sb.append(", message='");
    sb.append(message);
    sb.append('\'');
    sb.append(", inventory=");
    sb.append(inventory);
    sb.append(", value=");
    sb.append(value);
    sb.append(", margin=");
    sb.append(margin);
    sb.append(", lastUpdatedBy='");
    sb.append(lastUpdatedBy);
    sb.append('\'');
    sb.append(", eventDetails=");
    sb.append(eventDetails);
    sb.append(", thumbnailS7Url='");
    sb.append(thumbnailS7Url);
    sb.append('\'');
    sb.append(", ctaText='");
    sb.append(ctaText);
    sb.append('\'');
    sb.append(", ctaURL='");
    sb.append(ctaURL);
    sb.append('\'');
    sb.append(", area='");
    sb.append(area);
    sb.append('\'');
    sb.append(", thumbnailUrlText='");
    sb.append(thumbnailUrlText);
    sb.append('\'');
    sb.append(", custom1='");
    sb.append(custom1);
    sb.append('\'');
    sb.append(", custom2='");
    sb.append(custom2);
    sb.append('\'');
    sb.append(", ctaType='");
    sb.append(ctaType);
    sb.append('\'');
    sb.append(", description='");
    sb.append(description);
    sb.append('\'');
    sb.append(", custom3='");
    sb.append(custom3);
    sb.append('\'');
    sb.append(", latitude='");
    sb.append(latitude);
    sb.append('\'');
    sb.append(", longitude='");
    sb.append(longitude);
    sb.append('\'');
    sb.append(", interestTags='");
    sb.append(interestTags);
    sb.append('\'');
    sb.append('}');
    return sb.toString();
  }

  /**
   *  Get entity data for CSV.
   * @return CSV String
   */
  public String toStringCSV() {
    return id + ',' + StringEscapeUtils.escapeCsv(name) + ',' + categoryId + ','
        + StringEscapeUtils.escapeCsv(message) + ','
        + thumbnailUrl + ',' + value  + ',' + pageUrl + ',' + inventory + ',' + margin +  ','
        + thumbnailS7Url + ',' + interestTags + ',' + region + ','
        + interestToString(interestTags);
  }

  /**
   *  Strings for multi interest tags.
   * @param interestTags InterestTags
   * @return Interest
   */
  public static String interestToString(@NotNull List<String> interestTags) {
    if (interestTags != null && interestTags.size() > 0) {
      String temp = getStringFromList(interestTags);
      return "\"" + "[" + temp + "]" + "\"";
    }
    return StringUtils.EMPTY;
  }
  /**
   * Strings for multi Interest.
   * @param interestTags InterestTags
   * @return Tags
   */
  public static String getStringFromList(List<String> interestTags) {
    if (interestTags != null && interestTags.size() > 0) {
      Iterator<String> interestIt = interestTags.iterator();
      String temp = StringUtils.EMPTY;
      while (interestIt.hasNext()) {
        temp = temp + "\"\"" + interestIt.next() + "\"" + "\"";
        if (interestIt.hasNext()) {
          temp = temp + ",";
        }
      }
      return temp;
    }
    return StringUtils.EMPTY;
  }
  /**
   * Strings for multi Interest.
   * @param interestTags InterestTags
   * @return Tags
   */
  public static String getStringFromListOfArray(List<String> interestTags) {
    if (interestTags != null && interestTags.size() > 0) {
      Iterator<String> interestIt = interestTags.iterator();
      String temp = StringUtils.EMPTY;
      while (interestIt.hasNext()) {
        temp = temp + interestIt.next();
        if (interestIt.hasNext()) {
          temp = temp + ",";
        }
      }
      return temp;
    }
    return StringUtils.EMPTY;
  }
/**
 * Strings for Interest.
 * @param tags tags
 * @return tags
 */
  public static String getStringFromArray(String tags) {
    if (tags != null) {
      String temp = StringUtils.EMPTY;
      temp = temp + "[\"" + tags + "\"" + "]";
      return temp;
    }
    return StringUtils.EMPTY;
  }
}
