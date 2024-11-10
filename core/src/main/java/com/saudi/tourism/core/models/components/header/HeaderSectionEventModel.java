package com.saudi.tourism.core.models.components.header;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import javax.annotation.PostConstruct;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class HeaderSectionEventModel extends  HeaderSectionModel {
  /**
   * Image Title.
   */
  @ChildResource
  @Setter
  @Expose
  private Image titleImage;

  @PostConstruct
  void init() {
    super.init();
    if (Boolean.FALSE.equals(getEnableManualAuthoring()) && getCfModel() != null) {
      titleImage = new Image();

      if (getCfModel().getS7titleImage() != null) {
        titleImage.setS7fileReference(getCfModel().getS7titleImage());
      }
      if (getCfModel().getTitleImage() != null) {
        titleImage.setDesktopImage(getCfModel().getTitleImage());
      }
      if (getCfModel().getAltTitleImage() != null) {
        titleImage.setAlt(getCfModel().getAltTitleImage());
      }
      DynamicMediaUtils.setAllImgBreakPointsInfo(titleImage, "crop-1040x200", "crop-1040x200",
            "1280", "420", getCurrentResource().getResourceResolver(), getCurrentResource());
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
