package com.saudi.tourism.core.models.nativeApp.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * NativeAppFeatureModel .
 */
@Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class NativeAppFeatureModel implements Serializable {

  /**
   * featureList.
   */
  @Setter
  @Expose
  private List<Features> featuresList;
  /**
   * FEATURE_RES Constanst.
   */
  private static final String FEATURE_RES = "nativeAppFeatureApi";

  /**
   * CHILD_RES Constant.
   */
  private static final String CHILD_RES = "features";

  /**
   * Logger .
   */
  private final Logger logger = LoggerFactory.getLogger(NativeAppFeatureModel.class);
  /**
   * The current component resource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * init method.
   */
  @PostConstruct protected void init() {
    setFeaturesList(getFeatureInfo(FEATURE_RES));
  }

  /**
   * This Method sets the cards info inside any child resource.
   *
   * @param resourceName Resource name
   * @return list of cards.
   */
  private List<Features> getFeatureInfo(final String resourceName) {
    Resource resource = null;
    if (!currentResource.getPath().endsWith("/" + resourceName)) {
      resource = currentResource.getChild(resourceName);
    } else {
      resource = currentResource;
    }
    final List<Features> cardsList = new ArrayList<>();
    if (null != resource && resource.hasChildren()) {
      Resource childResource = resource.getChild(CHILD_RES);
      if (null != childResource) {
        childResource.getChildren().forEach(child -> {
          cardsList.add(child.adaptTo(Features.class));
        });
      }
    }
    return cardsList;
  }
}
