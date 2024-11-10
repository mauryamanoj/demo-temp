package com.saudi.tourism.core.services.tags.categories.v1;

import java.util.List;

public interface TagsCategoriesService {

  /**
   * fetch all Category Tags.
   *
   * @param locale
   * @return list of category tag
   */
  List<Tag> fetchAllTagsCategories(String locale);
}
