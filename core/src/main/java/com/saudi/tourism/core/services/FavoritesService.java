package com.saudi.tourism.core.services;


import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

/**
 * The Favorites Service interface.
 */
public interface FavoritesService {

  /**
   * Get the favorite type from the favorite path.
   *
   * @param favoritePath The favorite path.
   * @return The calculated favorite type.
   */
  FavoriteType getFavoriteType(String favoritePath);

  /**
   * Return the favorite's data.
   * @param resourceResolver A resource resolver.
   * @param locale current locale.
   * @param favoritePaths A list of favorite paths.
   *
   * @return The favorite's data.
   */
  List<Object> getFavoritesData(ResourceResolver resourceResolver, String locale, List<String> favoritePaths);

  /** Favorite type enum. */
  enum FavoriteType {
    /** Experience favorite type. */
    EXPERIENCE,
    /** Roadmap favorite type. */
    ROADTRIP,
    /** External link favorite type. */
    EXTERNAL_LINK,

    /** Internal link favorite type. */
    INTERNAL_LINK;
  }

  /**
   * Compute fav endpoints.
   *
   * @param locale
   * @return fav api endpoints
   */
  FavoritesApiEndpoints computeFavoritesApiEndpoints(String locale);
}
