package com.saudi.tourism.core.models.common;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CountriesGroupsListConfigModelTest {
  private static final String PAGE_PATH = "/content/sauditourism/en/Configs/admin";
  private static final String RESOURCE_PATH = "/content/sauditourism/en/Configs/admin/jcr:content/countries-groups";

  @BeforeEach
  public void setUp(final AemContext context) {
    context.load().json("/pages/admin-config.json", PAGE_PATH);
  }

  @Test
  void shouldCountriesGroups(final AemContext aemContext) {
    // Arrange

    // Act
    final CountriesGroupsListConfigModel model =
      aemContext.currentResource(RESOURCE_PATH).adaptTo(CountriesGroupsListConfigModel.class);

    // Assert
    assertNotNull(model);
    assertNotNull(model.getListCountriesGroups());
    assertEquals(4, model.getListCountriesGroups().size());
    assertEquals("gcc", model.getListCountriesGroups().get(0).getName());
    assertEquals("eligibleplus", model.getListCountriesGroups().get(1).getName());
    assertEquals("eligible", model.getListCountriesGroups().get(2).getName());
    assertEquals("noneligible", model.getListCountriesGroups().get(3).getName());
  }
}
