package com.saudi.tourism.core.models.app.entertainer;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DateStatusEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * App region.
 */
@Model(adaptables = Resource.class,
       resourceType = Constants.RT_APP_GAME_PAGE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(
    selector = "search",
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                               value = "true")})
@Data
public class AppGame implements Serializable {
  /**
   * Title.
   */
  @ValueMapValue
  @Named(com.day.cq.commons.jcr.JcrConstants.JCR_TITLE)
  private String title;
  /**
   * Id.
   */
  private String id;

  /**
   * path.
   */
  private String path;
  /**
   * The current component resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * The list of checkpoints.
   */
  @ChildResource
  private List<CheckpointModel> checkpoints;

  /**
   * The Start date.
   */
  @ValueMapValue
  private String gameStartDate;

  /**
   * The Start date.
   */
  @ValueMapValue
  private String gameEndDate;

  /**
   * The the image path.
   */
  @ValueMapValue
  private String image;

  /**
   * Checkbox to enable/disable starting point.
   */
  @ValueMapValue
  private boolean hasStartingPoint;

  /**
   * return the satutus of the game.
   * @return String status of the game
   */
  public String getStatus() {
    String currentDate = LocalDate.now().toString();
    if (CommonUtils.isDateBefore(currentDate, this.getGameStartDate())) {
      return DateStatusEnum.UPCOMING.getValue();
    } else if (CommonUtils.isDateBetweenStartEndWithInfinite(currentDate,
        this.getGameStartDate(), this.getGameEndDate())) {
      return DateStatusEnum.ONGOING.getValue();
    } else {
      return DateStatusEnum.EXPIRED.getValue();
    }
  }

  /**
   * This model post construct initialization.
   */
  @PostConstruct
  public void init() {
    id = AppUtils.stringToID(currentResource.getParent().getName());
    path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);
  }

}
