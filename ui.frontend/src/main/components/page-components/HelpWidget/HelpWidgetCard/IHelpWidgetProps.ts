export interface IHelpWidgetProps {
  title: string;
  description: string;
  cta: Cta;
  componentId:string;
}

export interface Cta {
  url: string;
  text: string;
}
