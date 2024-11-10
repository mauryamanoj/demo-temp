package com.saudi.tourism.core.beans;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FavoritesApiEndpoints {
  /**
   * Servlet endpoint to update favorites.
   */
  @Expose
  private String updateFavUrl;

  /**
   * Servlet endpoint to delete a favorite.
   */
  @Expose
  private String deleteFavUrl;

  /**
   * Servlet endpoint to fetch favorites.
   */
  @Expose
  private String getFavUrl;
}
