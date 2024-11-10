package com.saudi.tourism.core.servlets;

import com.drew.lang.annotations.NotNull;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Newsletter Email Subscription.
 */
@Component(service = Servlet.class,
      property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Newsletter Email Subscription Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_POST,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/v1/newsletter"
  })
@Slf4j
public class NewsLetterEmailSubsServlet extends SlingAllMethodsServlet {
  /**
   * Regex for email matching.
   */
  private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

  /**
   * seconds for timeout.
   */
  private static final int FIVE_SECONDS = 5;


  /**
   * serviceResponse.
   */
  private Response serviceResponse;


  @Override
  protected void doPost(@NotNull SlingHttpServletRequest request,
                        @NotNull SlingHttpServletResponse response) throws IOException {

    String email = request.getParameter("email");

    if (StringUtils.isNotBlank(email)) {
      if (!validate(email)) {
        CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
              new ResponseMessage(StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
            MessageType.ERROR.getType(), "emailNotValid"));
      } else {
        try {
          OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(FIVE_SECONDS, TimeUnit.SECONDS)
                .build();
          StringBuilder url = new StringBuilder()
                .append("https://visitsaudi.us10.list-manage.com/subscribe/post")
                .append("?u=c6e8e1050e368e828ef37ff59&amp%3Bid=e7a06d67c8&EMAIL=" + email);
          Request requestService = new Request.Builder()
                .url(url.toString())
                .build();
          serviceResponse = client.newCall(requestService).execute();
          if (serviceResponse.code() == HttpStatus.SC_OK) {
            CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
                  new ResponseMessage(StatusEnum.SUCCESS.getValue(),
                MessageType.SUCCESS.getType(), "Success"));
          } else {
            CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
                  new ResponseMessage(StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
                MessageType.ERROR.getType(), "Error"));
          }
        } catch (Exception e) {
          LOGGER.error(e.getMessage());
        } finally {
          serviceResponse.close();
        }
      }

    } else {
      CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
            new ResponseMessage(StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType(), "Empty Email"));
    }

  }
  /**
   * @param emailStr email.
   * @return true or false.
   */
  private static boolean validate(String emailStr) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
    return matcher.find();
  }
}
