package com.saudi.tourism.core.models.app.entertainer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.app.location.PolygonCoordinatesModel;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

/**
 * This model is used to get checkpoint of game.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class CheckpointModel implements Serializable {

  /**
   * coordinates.
   */
  @ChildResource
  private List<PolygonCoordinatesModel> coordinates;
  /**
   * The title of the checkpoint.
   */
  @ValueMapValue
  private String titleCheckpoint;


  /**
   * The order of the checkpoint.
   */
  @ValueMapValue
  private Integer order;

  /**
   * The Start date.
   */
  @ValueMapValue
  private String checkpointStartDate;

  /**
   * The Start date.
   */
  @ValueMapValue
  private String checkpointEndDate;
  /**
   * The id of the checkpoint .
   */
  @ValueMapValue
  private String idCheckpoint;

  /**
   * currentResource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;
  /**
   * This model post construct initialization.
   */
  @PostConstruct
  public void init() {
    String idGame =
        AppUtils.stringToID(currentResource.getParent().getParent().getParent().getName());
    idCheckpoint = idGame + Constants.FORWARD_SLASH_CHARACTER +  AppUtils.stringToID(idCheckpoint);
  }

}
