package com.saudi.tourism.core.models.mobile.components.atoms;

import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Period {
  /**
   * happening now.
   */
  private static final String HAPPENING_NOW = "happening-now";
  /**
   * this week.
   */
  private static final String THIS_WEEK = "this-week";

  /**
   * this month.
   */
  private static final String THIS_MONTH = "this-month";

  /**
   * period.
   */
  @ValueMapValue
  private String period;

  /**
   * Period start date.
   */
  private String startDate;

  /**
   * Period end date.
   */
  private String endDate;

  /**
   * Init method.
   */
  @PostConstruct
  protected void init() {
    if (period.equals(THIS_WEEK)) {
      startDate = CommonUtils.getCurrentDate();
      endDate = CommonUtils.getLastDayOfWeek().toString();
    }

    if (period.equals(THIS_MONTH)) {
      startDate = CommonUtils.getCurrentDate();
      endDate = CommonUtils.getLastDayOfMonth();
    }

    if (period.equals(HAPPENING_NOW)) {
      startDate = CommonUtils.getCurrentDate();
      endDate = CommonUtils.getCurrentDate();
    }
  }
}
