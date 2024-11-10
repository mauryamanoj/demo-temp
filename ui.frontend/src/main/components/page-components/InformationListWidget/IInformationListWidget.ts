export interface InformationListWidgetProps {
  informationLabel: string;
  locationLabel: string;
  durationLabel: string;
  agesLabel: string;
  typeLabel: string;
  accessibilityLabel: string;
  languageLabel: string;
  locationValue: string;
  durationValue: string;
  agesValue: string;
  typeValue: string;
  accessibilityValue: string[];
  languageValue: string[];
  openingHoursLabel: string;
  openingHoursValue: OpeningHour[];
  openNowLabel: string;
  closedNowLabel: string;
  toLabel: string;
  idealForLabel: string;
  categoriesTags: {
    title: string;
    icon: string;
  }[];
  sameTimeAcrossWeek: boolean;
  startDate?: string;
  endDate?: string;
}

export interface FieldProps {
  label: string;
  value: string | string[];
  icon: string;
  isMulti?: boolean;
  suffix?: string;
}

export interface OpeningHour {
  dayLabel: string;
  startTimeLabel: string;
  endTimeLabel: string;
}

