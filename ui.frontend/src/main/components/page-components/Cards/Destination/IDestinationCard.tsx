export interface DestinationCardProps {
  title: string;
  subTitle: string;
  bannerImage: {
    fileReference: string;
    s7fileReference: string;
    desktopImage: string;
    mobileImage: string;
    breakpoints: {
      srcset: string;
      media: string;
    }[];
  };
  popUpImage: {
    fileReference: string;
    s7fileReference: string;
    desktopImage: string;
    mobileImage: string;
    breakpoints: {
      srcset: string;
      media: string;
    }[];
  };

  latitude: string;
  longitude: string;
  id: string;
  categories: {
    title: string;
    image: {
      fileReference: string;
      s7fileReference: string;
    };
    icon: string;
  }[];
  weather: WeatherProps;
  pagePath?: {
    hideMap: boolean;
    url: string;
    targetInNewWindow: boolean;
  }
  className?: string;
}

export interface WeatherProps {
  icon: "Clouds" | "Clear" | "Rain" | "Snow" | "Thunderstorm" | "Haze";
  weather: string;
  temp: string;
  city: string;
  cityId: string;
}
