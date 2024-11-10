package com.saudi.tourism.core.utils;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.components.search.SearchPillsModel;
import com.saudi.tourism.core.models.components.search.SearchTrendingsModel;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.saudi.tourism.core.utils.Constants.PN_TITLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SearchUtilsTest {

  private ResourceResolver resourceResolver;

  @Mock
  private TagManager tagManager;

  @Mock
  private Tag tag;

  @Mock
  private UserService userService;

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/pages/full-day-in-jeddah.json", "/content/sauditourism/en/packages/full-day-in-jeddah");
    context.load().json("/pages/admin-config.json",
        "/content/sauditourism/en/Configs/admin");

    context.registerService(UserService.class, userService);

    resourceResolver = Mockito.spy(context.resourceResolver());
    Mockito.lenient().when(tag.getTitle(any())).thenReturn("category");
    Mockito.lenient().when(tagManager.resolve(anyString())).thenReturn(tag);
    Mockito.lenient().when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
    Mockito.lenient().when(userService.getResourceResolver()).thenReturn(context.resourceResolver());
  }

  @Test void getSearchPills(AemContext context) {
    SearchPillsModel searchPillsModel = SearchUtils.getSearchPills(PN_TITLE,
        context.resourceResolver(), "en");
    Assertions.assertEquals(2, searchPillsModel.getPills().size());
  }

  @Test void getSearchTrendings(AemContext context) {
    SearchTrendingsModel searchTrendings = SearchUtils.getSearchTrendings(PN_TITLE,
        context.resourceResolver(), "en", Constants.TRENDING_PAGE );
    Assertions.assertEquals(1, searchTrendings.getCards().size());
  }
}
