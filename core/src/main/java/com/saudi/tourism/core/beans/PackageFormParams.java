package com.saudi.tourism.core.beans;

import lombok.Data;

/**
 * Possible parameters for PackageFormServlet.
 */
@Data
public class PackageFormParams {

  /**
   * Language parameter for producing email in the correct language.
   */
  private String locale;
  /**
   * url of the page.
   */
  private String url;

  /**
   * dmc of the package.
   */
  private String dmc;

  /**
   * Contact email param.
   */
  private String email;

  /**
   * Contact first name.
   */
  private String firstName;

  /**
   * Contact last name.
   */
  private String lastName;

  /**
   * Contact phone.
   */
  private String phone;

  /**
   * Nationality param.
   */
  private String nationality;

  /**
   * Comment param.
   */
  private String comment;

  /**
   * Path to the package resource which is being requested.
   */
  private String packageFormRequest;

  /**
   * Email address to send package requested email.
   * TODO Get this as authored from AEM
   */
  private String dmcEmailAddr = "admin@thechandu.xyz";

}
