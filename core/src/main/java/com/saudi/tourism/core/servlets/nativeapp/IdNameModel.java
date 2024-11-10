package com.saudi.tourism.core.servlets.nativeapp;

import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.Serializable;

/**
 * Id name object.
 */
public class IdNameModel implements Serializable {

  /**
   * id.
   */
  @Expose
  @Getter
  private String id;

  /**
   * name.
   */
  @Expose
  @Getter
  private String name;
}
