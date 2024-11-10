import { linkType } from "../../common/CommonTypes";

export interface FaqProps {
  headline: string;
  componentId: string;
  link?: linkType;
  faqCategoriesEndpoint: string;
  faqArticlesEndpoint: string;
}
export interface TabItems {
  Name: string;
  ArabicName: string;
  ChineseName: string;
  Value: number;
}
export interface QnTypes {
  AppRecordUrl: string;
  knowledgeArticleGuid: string;
  Content: string;
  Title: string;
  Order: number;
}
