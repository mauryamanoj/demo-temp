package com.saudi.tourism.core.models.components.contentfragment.season;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.components.contentfragment.utils.ContentFragmentAwareModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.Calendar;

/**
 * This is the Sling Model class for the Content Fragment component for Season. The {@link Model}
 * annotation allows us to register the class as a Sling Model.
 */
@Model(
    adaptables = {Resource.class, ContentFragment.class},
    adapters = SeasonCFModel.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SeasonCFModel implements ContentFragmentAwareModel {

  /**
   * Story ContentFragment Model.
   */
  @Self
  private transient ContentFragment contentFragment;

  /**
   * Current resource.
   */
  @Self
  private transient Resource resource;

  /**
   * Season Title.
   */
  private String title;

  /**
   * Season Start Date.
   */
  private Calendar startDate;

  /**
   * Season End Date.
   */
  private Calendar endDate;

  /**
   * Season End Date.
   */
  private String itemId;

  /**
   * Type.
   */
  public static final String TYPE = "season";

  public SeasonCFModel(Calendar startDate, Calendar endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  @PostConstruct
  void init() {
    title = getElementValue(contentFragment, "title", String.class);
    startDate = getElementValue(contentFragment, "startDate", Calendar.class);
    endDate = getElementValue(contentFragment, "endDate", Calendar.class);
    itemId = getElementValue(contentFragment, "itemId", String.class);

  }
}
