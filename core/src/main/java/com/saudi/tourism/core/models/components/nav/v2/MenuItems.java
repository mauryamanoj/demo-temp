package com.saudi.tourism.core.models.components.nav.v2;

import com.saudi.tourism.core.models.common.Link;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class MenuItems.
 */
@Data
public class MenuItems {

  /**
   * The Title.
   */
  private String title;

  /**
   * The Items.
   */
  private List<Link> items = new ArrayList<>();

}
