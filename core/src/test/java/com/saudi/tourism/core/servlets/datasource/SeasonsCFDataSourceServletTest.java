package com.saudi.tourism.core.servlets.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.google.common.collect.Iterators;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.services.seasons.v1.SeasonsCFService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SeasonsCFDataSourceServletTest {

  @Mock
  private SeasonsCFService seasonsCFService;

  @Mock
  private Resource cfResource;

  private MockSlingHttpServletRequest request;

  private MockSlingHttpServletResponse response;

  @InjectMocks
  private SeasonsCFDataSourceServlet servlet = new SeasonsCFDataSourceServlet();

  @BeforeEach
  void setUp(final AemContext aemContext) {
    request = aemContext.request();
    response = aemContext.response();
  }

  @Test
  void doGetShouldReturnSeasons(final AemContext aemContext)
    throws ServletException, IOException {
    aemContext.request().setPathInfo("/content/sauditourism/en/calendar");

    // Arrange
    final var season = SeasonCFModel.builder()
      .title("Riyadh Season")
      .resource(cfResource)
      .build();

    when(cfResource.getPath()).thenReturn("/content/dam/sauditourism/cf/en/seasons/riyadh-season");
    when(seasonsCFService.fetchAllSeasons(eq("en"))).thenReturn(Arrays.asList(season));

    // Act
    servlet.doGet(request, response);

    // Assert
    final SimpleDataSource ds = (SimpleDataSource) request.getAttribute(DataSource.class.getName());
    assertNotNull(ds);
    assertEquals(2, Iterators.size(ds.iterator()));
  }
}