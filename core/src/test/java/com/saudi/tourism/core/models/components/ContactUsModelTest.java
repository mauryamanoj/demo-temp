package com.saudi.tourism.core.models.components;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.components.contactus.v1.ContactUsModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ContactUsModelTest {

  private static final String RESOURCE_PATH =
      "/content/sauditourism/en/help-support/jcr:content/root/responsivegrid/contact_us_form";

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Mock private SaudiTourismConfigs saudiTourismConfigs;

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.load().json("/components/contact-us-form/content.json", RESOURCE_PATH);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);

    when(saudiTourismConfigs.getContactUsMessageTypeApiEndpoint())
        .thenReturn("/bin/api/contactus/messageType");
    when(saudiTourismConfigs.getContactUsFormEndpoint())
      .thenReturn("/bin/api/contactus/formData");
    when(saudiTourismConfigs.getFlagsPath()).thenReturn("/content/dam/saudi-tourism/media/flags");
  }

  @Test
  void testContactusModel(final AemContext aemContext) {
    // Arrange
    aemContext.currentResource(RESOURCE_PATH);

    // Act
    final ContactUsModel model = aemContext.currentResource().adaptTo(ContactUsModel.class);
    final String json = model.getJson();
    final ContactUsModel data = gson.fromJson(json, ContactUsModel.class);

    // Assert
    assertEquals("/bin/api/contactus/messageType", data.getMessageTypeServlet());
    assertEquals("/bin/api/contactus/formData", data.getSubmitFormServlet());
    assertEquals("/content/dam/saudi-tourism/media/flags", data.getFlagsPath());
  }
}
