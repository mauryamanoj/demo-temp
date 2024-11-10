package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items;

import com.saudi.tourism.core.models.mobile.components.atoms.PriceWidget;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterResponseModel {

  /**
   * priceWidget.
   */
  @ChildResource
  private PriceWidget priceWidget;

  /**
   * footerComponentStyle.
   */
  @ChildResource
  private FooterComponentStyle footerComponentStyle;

  /**
   * init method.
   */
  @PostConstruct
  public void init() {

    if (Objects.nonNull(priceWidget) && StringUtils.isBlank(priceWidget.getType())) {
      priceWidget = null;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class FooterComponentStyle {

    /**
     * componentUIType.
     */
    @ValueMapValue
    private String componentUIType;

  }
}
