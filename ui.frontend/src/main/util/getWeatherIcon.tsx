export const TEMPERATURE_ICON = {
  Fallback: 'weather-default',
  Clear: { day: 'weather-clear-day', night: 'weather-moon' },
  Thunderstorm: 'weather-cloud-storm',
  Drizzle: { day: 'weather-cloud-rain', night: 'weather-moon-rain' },
  Rain: 'weather-cloud-rain-heavy',
  Snow: { day: 'weather-cloud-snow', night: 'weather-moon-snow' },
  Clouds: 'weather-clouds',
  Mist: 'weather-mist',
  Smoke: 'weather-smoke',
  Haze: 'weather-haze',
  Dust: 'weather-dust',
  Fog: 'weather-fog',
  Sand: 'weather-dust',
  Ash: 'weather-dust',
  Squall: 'weather-cloud-windy',
  Tornado: 'weather-tornado',
};

export const getWeatherIcon = (
  icon?: TemperatureIcon,
  shouldShowNightIcon: boolean | undefined = false,
) => {
  const iconsWithNightVariant = ['Clear', 'Drizzle', 'Snow'];

  if (!icon || !TEMPERATURE_ICON[icon]) {
    // eslint-disable-next-line no-console
    console.error(`The icon with name ${icon} doesn't exist, please add it.`);
  }

  if (icon && iconsWithNightVariant.includes(icon)) {
    // This type cohersion is to 'Clear' but with the includes I check every icon with night version
    return TEMPERATURE_ICON[icon as 'Clear'][shouldShowNightIcon ? 'night' : 'day'];
  } else {
    // This type cohersion is to 'Rain' but this else only works when the icon doesn't have night version
    return TEMPERATURE_ICON[icon as 'Rain'] || TEMPERATURE_ICON['Fallback'];
  }
};

export type TemperatureIcon = keyof typeof TEMPERATURE_ICON;
