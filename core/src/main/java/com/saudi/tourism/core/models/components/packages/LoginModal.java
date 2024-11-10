package com.saudi.tourism.core.models.components.packages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.login.servlets.SSIDLoginUrlServlet;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * The type Login modal.
 */
@Data
@NoArgsConstructor
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LoginModal {
  /**
   * The Title.
   */
  @Expose
  private String title;

  /**
   * The Paragraph.
   */
  @Expose
  private String paragraph;

  /**
   * Login modal button.
   */
  @Expose
  private LoginModalButton button;

  /**
   * Variable for current resource.
   */
  @Self
  private Resource currentResource;

  /**
   * The Model Initializer.
   */
  @PostConstruct private void init() {
    String path = Optional.ofNullable(currentResource.getPath()).orElse(StringUtils.EMPTY);
    String language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
    AdminPageOption adminOptions = AdminUtil.getAdminOptions(language, StringUtils.EMPTY);
    this.title = adminOptions.getFavLoginTitle();
    this.paragraph = adminOptions.getFavLoginCopy();
    this.button = new LoginModalButton();
    this.button.setCopy(adminOptions.getFavLoginButtonCopy());
    this.button.setLink(SSIDLoginUrlServlet.SERVLET_PATH);
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
