package com.sta.core.vendors;

/**
 * This is used to define all String constants.
 */
@SuppressWarnings("java:S1075")
public final class ScriptConstants {

  /**
   * The constant VENDOR_KEY.
   */
  public static final String VENDOR_KEY = "vendor";
  /**
   * The constant NAME.
   */
  public static final String NAME = "name";

  /**
   * The constant EVENT.
   */
  public static final String EVENT = "event";

  /**
   * The constant PACKAGE.
   */
  public static final String PACKAGE = "package";

  /**
   * Default constructor.
   */
  private ScriptConstants() {

  }

  /**
   * The constant FORMAT_DATE.
   */
  public static final String EXCEL_FORMAT_DATE = "MM/dd/yyyy";
  /**
   * The constant FORMAT_DATE.
   */
  public static final String FORM_FORMAT_DATE = "yyyy-MM-dd";

  /**
   * The constant FORMAT_ORIGINAL_DATE.
   */
  public static final String FORMAT_ORIGINAL_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  
  /**
   * The constant FORM_FORMAT_12_TIME.
   */
  public static final String FORM_FORMAT_12_TIME = "hh : mm aa";

  /**
   * The constant VENDOR_EVENT_DAM_PATH.
   */
  public static final String VENDOR_EVENT_DAM_PATH = "vendors/events";
  /**
   * The constant DEFAULT_IMAGE.
   */
  public static final String DEFAULT_IMAGE = "/apps/sauditourism/clientlibs/clientlib-site"
      + "/resources"
      + "/default-assets/secondary-hero/image.png";
  /**
   * The constant EVENT_TEMPLATE.
   */
  public static final String EVENT_TEMPLATE = "/conf/sauditourism/settings/wcm/templates/event"
      + "-details";

  /**
   * The constant PACKAGE_TEMPLATE.
   */
  public static final String PACKAGE_TEMPLATE = "/conf/sauditourism/settings/wcm/templates/packages"
          + "-details";

  /**
   * The constant CONTENT_TEMPLATE.
   */
  public static final String CONTENT_TEMPLATE = "/conf/sauditourism/settings/wcm/templates/content"
      + "-page";
}
