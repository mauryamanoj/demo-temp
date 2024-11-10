package com.saudi.tourism.core.servlets.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.google.common.collect.Iterators;
import com.saudi.tourism.core.models.common.CountriesGroupsListConfigModel;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.evisa.v1.EvisaService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static com.saudi.tourism.core.utils.Constants.DEFAULT_LOCALE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CountriesGroupsDataSourceServletTest {

  private static final String PAGE_PATH = "/content/sauditourism/en/Configs/admin";
  private static final String RESOURCE_PATH =
    "/content/sauditourism/en/Configs/admin/jcr:content/countries-groups";

  @Mock
  private EvisaService evisaService;

  private MockSlingHttpServletRequest request;

  private MockSlingHttpServletResponse response;

  @InjectMocks
  private CountriesGroupsDataSourceServlet countriesGroupsDataSourceServlet =
    new CountriesGroupsDataSourceServlet();

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.load().json("/pages/admin-config.json", PAGE_PATH);

    request = aemContext.request();
    response = aemContext.response();
  }

  @Test
  void doGetShouldReturnCountriesGroups(final AemContext aemContext)
    throws ServletException, IOException {
    // Arrange
    final CountriesGroupsListConfigModel countriesGroupsListmodel =
      aemContext.currentResource(RESOURCE_PATH).adaptTo(CountriesGroupsListConfigModel.class);
    when(evisaService.getCountriesGroups(eq(DEFAULT_LOCALE)))
      .thenReturn(countriesGroupsListmodel.getListCountriesGroups());

    // Act
    countriesGroupsDataSourceServlet.doGet(request, response);

    // Assert
    final SimpleDataSource ds = (SimpleDataSource) request.getAttribute(DataSource.class.getName());
    assertNotNull(ds);
    assertEquals(4, Iterators.size(ds.iterator()));
  }
}
