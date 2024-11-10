export enum TicketType {
  GetTicket = "getTicket",
  NoTicket = "noTicket",
  BookTicket = "bookTicket",
}

export interface PriceWidgetProps {
  ticketType: TicketType;
  price: string;
  priceSuffix: string;
  text: string;
  buttonLabel: string;
  url: string;
}
