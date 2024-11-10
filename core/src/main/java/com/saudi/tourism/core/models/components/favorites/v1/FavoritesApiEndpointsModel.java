package com.saudi.tourism.core.models.components.favorites.v1;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Data;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(
    adaptables = {Resource.class, SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class FavoritesApiEndpointsModel {

  /** Servlet endpoints for sfavorites. */
  private FavoritesApiEndpoints favoritesApiEndpoints;

  /** The crurrent page. */
  @ScriptVariable
  private Page currentPage;

  /**The current resrouce.*/
  @Named("currentResource")
  private Resource currentResource;

  /** Favorites Service. */
  @Inject
  private FavoritesService favoritesService;

  /** init method of sling model. */
  @PostConstruct
  protected void init() {
    final String path = getCurrentPath();
    final String lang = CommonUtils.getLanguageForPath(path);

    favoritesApiEndpoints = favoritesService.computeFavoritesApiEndpoints(lang);
  }

  private String getCurrentPath() {
    if (currentPage != null) {
      return currentPage.getPath();
    }
    if (currentResource != null) {
      return currentResource.getPath();
    }
    return null;
  }
}
