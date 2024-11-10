package com.saudi.tourism.core.services.impl;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.PathUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ExportException;
import org.apache.sling.models.factory.MissingExporterException;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ResourceExporterServiceImplTest {

  private static final String RELATIVE_PAGE_PATH = Constants.DEFAULT_LOCALE;

  private static final String ABSOLUTE_PAGE_PATH = PathUtil.append(Constants.ROOT_CONTENT_PATH,
      RELATIVE_PAGE_PATH);

  private static final String ABSOLUTE_JCR_CONTENT_PATH = PathUtil.append(ABSOLUTE_PAGE_PATH,
      JcrConstants.JCR_CONTENT);

  private static final String FAKE_MODEL = "Trust me, I am a model!";

  private ResourceExporterServiceImpl service = new ResourceExporterServiceImpl();

  private ModelFactory modelFactory = mock(ModelFactory.class);

  @Test
  @DisplayName("when getModel is invoked with null resource resolver NPE is thrown")
  void testGetModelWhenResourceResolverIsNull() {
    assertThrows(NullPointerException.class, () -> {
      service.getModel(null, StringUtils.EMPTY);
    });
  }

  @Test
  @DisplayName("when getModel is invoked with null path NPE is thrown")
  void testGetModelWhenPathIsNull(AemContext context) {
    assertNotNull(service);
    assertNotNull(context);
    assertThrows(NullPointerException.class, () -> {
      service.getModel(context.resourceResolver(), null);
    });
  }

  @Test
  @DisplayName("when getModel is invoked for non-existing resource null is returned")
  void testGetModelWhenPathDoesNotResolve(AemContext context) {
    Utils.setInternalState(service, "modelFactory", modelFactory);
    assertNotNull(service);
    service.getModel(context.resourceResolver(), StringUtils.EMPTY);
  }

  @Test
  @DisplayName("when getModel is invoked for existing resource and model is not registered null "
                   + "is returned")
  void testGetModelWhenAbsolutePathResolvesToResource(AemContext context) {
    initMocks(context, false);
    Object model = service.getModel(context.resourceResolver(), ABSOLUTE_JCR_CONTENT_PATH);
    assertNull(model);
  }

  @Test
  void testGetModelWhenAbsolutePathResolvesToPage(AemContext context) {
    initMocks(context, true);
    Object model = service.getModel(context.resourceResolver(), ABSOLUTE_PAGE_PATH);
    assertEquals("Trust me, I am a model!", model);
  }

  @Test
  @DisplayName("when getModel is invoked for existing resource and model is registered not null "
                   + "is returned")
  void testGetModelWhenAbsolutePathResolvesToResourceWithModel(AemContext context) {
    initMocks(context, true);
    Object model = service.getModel(context.resourceResolver(), ABSOLUTE_JCR_CONTENT_PATH);
    assertEquals(FAKE_MODEL, model);
  }

  @Test
  @DisplayName("Test exportResource method when modelFactory throws ExportException")
  void testExportResourceWhenExportException(AemContext context)
      throws ExportException, MissingExporterException {
    initMocks(context, true);
    when(modelFactory.exportModel(eq(FAKE_MODEL), eq(ExporterConstants.SLING_MODEL_EXPORTER_NAME)
        , eq(String.class), any())).thenThrow(new ExportException("Oops!"));
    assertEquals(StringUtils.EMPTY, service.exportResource(context.resourceResolver(),
        ABSOLUTE_JCR_CONTENT_PATH));
  }

  @Test
  @DisplayName("Test exportResource method when modelFactory throws MissingExporterException")
  void testExportResourceWhenMissingExporterException(AemContext context)
      throws ExportException, MissingExporterException {
    initMocks(context, true);
    when(modelFactory.exportModel(eq(FAKE_MODEL), eq(ExporterConstants.SLING_MODEL_EXPORTER_NAME)
        , eq(String.class), any())).thenThrow(new MissingExporterException("Oops!", String.class));
    assertEquals(StringUtils.EMPTY, service.exportResource(context.resourceResolver(),
        ABSOLUTE_JCR_CONTENT_PATH));
  }

  @Test
  @DisplayName("Test exportResource method when modelFactory returns a value")
  void testExportResourceWhenExported(AemContext context)
      throws ExportException, MissingExporterException {
    initMocks(context, true);
    when(modelFactory.exportModel(eq(FAKE_MODEL), eq(ExporterConstants.SLING_MODEL_EXPORTER_NAME)
        , eq(String.class), any())).thenReturn(FAKE_MODEL);
    assertEquals(FAKE_MODEL, service.exportResource(context.resourceResolver(),
        ABSOLUTE_JCR_CONTENT_PATH));
  }

  void initMocks(AemContext context, boolean mockModel) {
    context.load().json("/pages/app-location-page.json", ABSOLUTE_PAGE_PATH);
    Utils.setInternalState(service, "modelFactory", modelFactory);
    if (mockModel) {
      when(modelFactory.getModelFromResource(any(Resource.class))).thenReturn(FAKE_MODEL);
    }
  }

}