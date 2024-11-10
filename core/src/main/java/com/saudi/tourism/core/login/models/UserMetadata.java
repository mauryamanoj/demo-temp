package com.saudi.tourism.core.login.models;

import lombok.Data;

/**
 * The type User metadata.
 */
@Data
public class UserMetadata {
  /**
   * The Favorites.
   */
  private String[] favorites;

  /**
   * The Trips.
   */
  private String[] trips;

  /**
   * The Profile.
   */
  private Profile profile;


}
