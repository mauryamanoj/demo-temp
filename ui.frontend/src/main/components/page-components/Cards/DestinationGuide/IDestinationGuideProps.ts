import { TemperatureIcon } from "src/main/util/getWeatherIcon";

interface Link {
  hideMap: boolean;
  url: string;
  urlWithExtension: string;
  urlSlingExporter: string;
  targetInNewWindow: boolean;
  text?: string;
  appType?: string;
  copy?: string;
  icon?: string;
}

interface Logo {
  fileReference: string;
  mobileImageReference: string;
  s7fileReference: string;
  s7mobileImageReference: string;
  desktopImage: string;
  mobileImage: string;
}

interface Item {
  logo: Logo;
  link: Link;
  title: string;
  subTitle: string;
}
interface WeatherItem {
  title?: string;
  lowTemp?: string;
  lowTempIcon?: TemperatureIcon;
  highTemp?: string;
  highTempIcon?: TemperatureIcon;
}
export interface IDestinationGuideCardProps {
  styles?: string;
  type: string;
  title?: string;
  link?: Link;
  logo?: Logo;
  items?: Item[] | WeatherItem[];
  description?: string;
  weather?: string;
  temp?: string;
  icon?: TemperatureIcon;
  overallWeatherLabel?: string;
}

export interface IDestinationGuideProps {
  type: string;
  title: string;
  subTitle?:string;
  link: Link;
  cards: IDestinationGuideCardProps[];
  background?:Logo;
  haveBackgroundImage?: boolean;
}
