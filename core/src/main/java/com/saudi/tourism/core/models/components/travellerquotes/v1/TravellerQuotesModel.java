package com.saudi.tourism.core.models.components.travellerquotes.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;
import javax.annotation.PostConstruct;


/**
 * This Class contains Traveller Quotes Model details.
 */
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TravellerQuotesModel {
  /**
   * ORNAMENT_ID_ONE.
   */
  public static final String ORNAMENT_ID_ONE = "01";
  /**
   * Ornament ID.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String ornamentId;

  /**
  * Variable to hold list of quotes slides.
  */
  @Getter
  @ChildResource
  @Expose
  private List<QuotesSlide> quotesSlides;

  /**
   * Variable to hold slides count.
   */
  @Getter
  @Expose
  private int slidesCount;
  /**
   * get the count of the slides.
   */
  @PostConstruct
  protected void init() {
    if (null != quotesSlides) {
      slidesCount = quotesSlides.size();
    }
    if (StringUtils.isBlank(ornamentId)) {
      ornamentId = ORNAMENT_ID_ONE;
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
