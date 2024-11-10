package com.saudi.tourism.core.beans;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

/**
 * Bean for the Road Trip API Response.
 */
@Getter
public class RoadTripResponse {

  /**
   * List object routeBody.
   */
  @SerializedName("route_body")
  private List<RouteBody> routeBody;

  /**
   * Class RouteBody .
   */
  @Getter
  public static class RouteBody {

    /**
     * Variable photo .
     */
    private String photo;


    /**
     * Variable ID.
     */
    private String id;

    /**
     * Variable name .
     */
    private Name name;

    /**
     * Variable description .
     */
    private Description description;

    /**
     * Variable author .
     */
    private Author author;

    /**
     * Variable city .
     */
    @SerializedName("start_city_name")
    private City city;

    /**
     * Object metadata .
     */
    private Metadata metadata;

    /**
     * Class Name .
     */
    @Getter
    public static class Name {

      /**
       * Variable en.
       */
      private String en;

      /**
       * Variable ru.
       */
      private String ru;

      /**
       * Variable es.
       */
      private String es;

      /**
       * Variable pt.
       */
      private String pt;

      /**
       * Variable ar.
       */
      private String ar;

      /**
       * Variable de.
       */
      private String de;
    }

    /**
     * Class Description .
     */
    @Getter
    public static class Description {
      /**
       * Variable en.
       */
      private String en;

      /**
       * Variable ru.
       */
      private String ru;

      /**
       * Variable es.
       */
      private String es;

      /**
       * Variable pt.
       */
      private String pt;

      /**
       * Variable ar.
       */
      private String ar;

      /**
       * Variable de.
       */
      private String de;
    }

    /**
     * Class Author .
     */
    @Getter
    public static class Author {

      /**
       * Variable image.
       */
      private String image;

      /**
       * Variable business.
       */
      private String business;

      /**
       * Variable name.
       */
      private AuthorName name;

      /**
       * Class AuthorName.
       */
      @Getter
      public static class AuthorName {

        /**
         * Variable en.
         */
        private String en;

        /**
         * Variable ru.
         */
        private String ru;

        /**
         * Variable es.
         */
        private String es;

        /**
         * Variable pt.
         */
        private String pt;

        /**
         * Variable ar.
         */
        private String ar;

        /**
         * Variable de.
         */
        private String de;
      }
    }

    /**
     * Class City .
     */
    @Getter
    public static class City {

      /**
       * Variable en.
       */
      private String en;

      /**
       * Variable ru.
       */
      private String ru;

      /**
       * Variable es.
       */
      private String es;

      /**
       * Variable pt.
       */
      private String pt;

      /**
       * Variable ar.
       */
      private String ar;

      /**
       * Variable de.
       */
      private String de;
    }

    /**
     * Class Metadata .
     */
    @Getter
    public static class Metadata {

      /**
       * variable duration .
       */
      private Integer duration;
    }
  }
}
