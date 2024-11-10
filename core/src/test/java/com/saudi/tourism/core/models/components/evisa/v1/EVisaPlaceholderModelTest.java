package com.saudi.tourism.core.models.components.evisa.v1;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EVisaPlaceholderModelTest {

  private static final String RESOURCE_PATH = "/content/sauditourism/en/understand-test/test/jcr:content/root/responsivegrid/e_visa";

  @BeforeEach
  public void setUp(final AemContext context) {
    context.load().json("/components/evisa-placeholder/content.json", RESOURCE_PATH);
  }

  @Test
  void getCountryListEndPoint(final AemContext aemContex) {

    final EVisaPlaceholderModel model = aemContex.currentResource(RESOURCE_PATH).adaptTo(EVisaPlaceholderModel.class);

    Assertions.assertEquals("/bin/api/v1/geo/countries", model.getCountryListEndPoint());

  }

  @Test
  void getEVisaConfigEndPoint(final AemContext aemContex) {
    final EVisaPlaceholderModel model = aemContex.currentResource(RESOURCE_PATH).adaptTo(EVisaPlaceholderModel.class);

    Assertions.assertEquals("/bin/api/v1/evisa/config", model.getEVisaConfigEndPoint());
  }

  @Test
  void getErrorMessage(final AemContext aemContex) {
    final EVisaPlaceholderModel model = aemContex.currentResource(RESOURCE_PATH).adaptTo(EVisaPlaceholderModel.class);

    Assertions.assertEquals("Something Went Wrong", model.getErrorMessage());
  }
}