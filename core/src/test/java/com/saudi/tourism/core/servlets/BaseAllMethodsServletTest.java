package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test for BaseAllMethodsServlet class.
 */
class BaseAllMethodsServletTest {

  private MockSlingHttpServletRequest request;
  private MockSlingHttpServletResponse response;

  private BaseAllMethodsServlet testServlet;

  @BeforeEach
  void setUp() {
    AemContext context = new AemContext();
    request = context.request();
    response = context.response();

    testServlet = mock(BaseAllMethodsServlet.class, CALLS_REAL_METHODS);
  }

  @Test
  void checkNecessaryParameters_attributes_ok() throws IOException {
    final String[] notEmptyValue = new String[] {"not-empty-value"};
    final Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("existing1", notEmptyValue);
    parameterMap.put("existing2", notEmptyValue);
    parameterMap.put("existing3", notEmptyValue);

    request.setParameterMap(parameterMap);

    // Check iterated
    testServlet.checkNecessaryParameters(request, response, "existing1", "existing2");
    verify(testServlet, times(0)).outError(any(), any(), any(), anyString(), any());
  }

  @Test
  void checkNecessaryParameters_map_ok() throws IOException {
    final String notEmptyValue = "not-empty-value";

    // Check using map
    final Map<String, Object> paramsMap = new HashMap<>();
    paramsMap.put("existing1", notEmptyValue);
    paramsMap.put("existing2", notEmptyValue);
    paramsMap.put("existing3", notEmptyValue);

    testServlet.checkNecessaryParameters(request, response, paramsMap, "existing1", "existing2");
    verify(testServlet, times(0)).outError(any(), any(), any(), anyString(), any());
  }

  @Test
  void checkNecessaryParameters_attributes_fail() {
    final String existing = "existing";
    final String notExisting = "not-existing";
    final String[] notEmptyValue = new String[] {"not-empty-value"};
    final Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(existing, notEmptyValue);
    parameterMap.put(notExisting, new String[] {});
    request.setParameterMap(parameterMap);

    final String expectedErrorMessage =
        MessageFormat.format(Constants.ERR_MISSING_PARAMETERS, "[" + notExisting + "]");

    // Check request params
    final Throwable exception = assertThrows(IllegalArgumentException.class,
        () -> testServlet.checkNecessaryParameters(request, response, existing, notExisting));
    assertEquals(expectedErrorMessage, exception.getMessage());
  }

  @Test
  void checkNecessaryParameters_map_fail() {
    final String existing = "existing";
    final String notExisting1 = "not-existing1";
    final String notExisting2 = "not-existing2";

    final String expectedErrorMessage = MessageFormat
        .format(Constants.ERR_MISSING_PARAMETERS, "[" + notExisting1 + ", " + notExisting2 + "]");

    // Check using map
    final Map<String, Object> paramsMap = new HashMap<>();
    paramsMap.put(existing, "not-empty-value");

    final Throwable exception = assertThrows(IllegalArgumentException.class, () -> testServlet
        .checkNecessaryParameters(request, response, paramsMap, existing, notExisting1,
            notExisting2));
    assertEquals(expectedErrorMessage, exception.getMessage());
  }

  @Test
  void outErrorTest_noException() throws IOException {
    testServlet.outError(request, response, null, "message: {0}", "param");

    assertTrue(StringUtils.contains(response.getOutputAsString(), "message: param"));
  }

  @Test
  void outErrorTest_Exception_empty() throws IOException {
    final Exception ex = new Exception();
    testServlet.outError(request, response, ex, "message: {0}", "param");

    final String output = response.getOutputAsString();
    assertTrue(StringUtils.contains(output, "message: param"));
    assertTrue(StringUtils.contains(output, ex.getClass().getSimpleName()));
  }

  @Test
  void outErrorTest_Exception_message() throws IOException {
    final String exceptionMessage = "someException";
    // Check another exception class here
    final Exception ex = new IllegalArgumentException(exceptionMessage);
    testServlet.outError(request, response, ex, "message: {0}", "param");

    final String output = response.getOutputAsString();
    assertTrue(StringUtils.contains(output, "message: param"));
    assertTrue(StringUtils.contains(output, exceptionMessage));
    assertTrue(StringUtils.contains(output, ex.getClass().getSimpleName()));
  }
}
