package com.saudi.tourism.core.utils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.exceptions.MissingRequestParameterException;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit Test Class to test Tag utils.
 */
class TagUtilsTest {


  @Test
  void processTagWithoutNamespaceNonEmpty() {


    Category categoryTag = new Category();
    categoryTag.setId("sauditourism:categories/nature/natural_site");

    TagUtils.processTagWithoutNamespace(categoryTag);

    Assertions.assertEquals("categories/nature/natural_site", categoryTag.getId());
  }


  @Test
  void processTagWithoutNamespaceWithoutColumn() {


    Category categoryTag = new Category();
    categoryTag.setId("categories/nature/natural_site");

    TagUtils.processTagWithoutNamespace(categoryTag);

    Assertions.assertEquals("categories/nature/natural_site", categoryTag.getId());
  }


  @Test
  void getTagWithoutNamespaceNonEmpty() {

    Tag tag = mock(Tag.class);
    when(tag.getTagID()).thenReturn("sauditourism:categories/nature/natural_site");

    Assertions.assertEquals("categories/nature/natural_site",TagUtils.getTagWithoutNamespace(tag));
  }

}
