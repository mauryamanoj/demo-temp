package com.saudi.tourism.core.models.components.nav.v2;

import com.saudi.tourism.core.models.components.ArticleItem;
import com.saudi.tourism.core.models.components.nav.v3.NavigationLink;
import com.saudi.tourism.core.utils.NavItemUtils;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Unit test class for testing {@link NavigationTab} methods.
 */
@Getter
class NavigationTabTest {

  private NavigationTab navigationTab;

  @BeforeEach void setUp() {
    navigationTab = spy(NavigationTab.class);
  }

  @Test void isNest() {
    doReturn(false).when(navigationTab).hasColumns();

    assertFalse(navigationTab.isNest());

    navigationTab.setNavType(NavItemUtils.NAV_TYPE_EVENTS);
    assertFalse(navigationTab.isNest());

    navigationTab.setNavType(null);
    doReturn(true).when(navigationTab).hasColumns();
    assertTrue(navigationTab.isNest());
  }

  @Test void hasColumns() {
    navigationTab.getLinksColumn().add(new NavigationLink(null, null, null));
    assertTrue(navigationTab.hasColumns());

    navigationTab.getLinksColumn().clear();
    assertFalse(navigationTab.hasColumns());

    navigationTab.setNavType(NavItemUtils.NAV_TYPE_ARTICLES);
    assertFalse(navigationTab.hasColumns());
    navigationTab.setArticleItem(new ArticleItem());
    assertTrue(navigationTab.hasColumns());

    navigationTab.setNavType(NavItemUtils.NAV_TYPE_PLAN);
    assertFalse(navigationTab.hasColumns());
    navigationTab.getSecondaryLinks().add(new PlanItem());
    assertTrue(navigationTab.hasColumns());
  }

  @Test void hasWidgets() {
    navigationTab.setNavType(null);
    assertFalse(navigationTab.hasWidgets());

    navigationTab.setNavType(NavItemUtils.NAV_TYPE_WIDGETS);
    assertTrue(navigationTab.hasWidgets());

    navigationTab.setNavType(NavItemUtils.NAV_TYPE_ARTICLES);
    assertFalse(navigationTab.hasWidgets());

    navigationTab.setArticleItem(new ArticleItem());
    assertTrue(navigationTab.hasWidgets());

    navigationTab.setNavType(null);
    assertFalse(navigationTab.hasWidgets());
  }
}
