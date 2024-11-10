package com.saudi.tourism.core.models.components.greenTaxi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Download App model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class DownloadAppModel {

  /**
   * title.
   */
  @ValueMapValue
  private String title;

  /**
   * sub title.
   */
  @ValueMapValue
  @SerializedName(value = "sub_title")
  private String subTitle;

  /**
   * qrcode image.
   */
  @ValueMapValue
  @SerializedName(value = "qrcode_image")
  private String qrcodeImage;

  /**
   * qrcode title.
   */
  @ValueMapValue
  @SerializedName(value = "qrcode_title")
  private String qrcodeTitle;
}
