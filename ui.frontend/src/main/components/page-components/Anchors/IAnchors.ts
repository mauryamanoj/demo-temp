export interface AnchorsProps {
  title: string;
  links: Array<Item>;
  line?: boolean;
  showInResponsive?: boolean;
  componentId: string;
}
export interface Item {
  number: string;
  text: string;
  hideMap: string;
  url: string;
  urlWithExtension: string;
  urlSlingExporter: string;
  targetInNewWindow: boolean;
  appType: string;
  scroll: boolean;
}
