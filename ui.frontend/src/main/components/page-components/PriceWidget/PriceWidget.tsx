import React from "react";
import Wrapper from "../../common/HOC/Wrapper/Wrapper";
import { TicketType } from "./IPriceWidget";
const PriceWidgetComp = React.lazy(() => import("./PriceWidgetComp"));

const PriceWidget = (props: any) => {
    const hasNonWhiteSpaceContent = (str: string) => {
        // Replace &nbsp; with regular space and then trim
        const cleanedStr = str
            ?.replace(/&nbsp;/g, " ")
            ?.replace(/<[^>]*>/g, "")
            ?.trim();
        return str && cleanedStr !== "";
    };
    const { ticketType, price, text, buttonLabel, url } = props;

    const isBookTicket =
        ticketType === TicketType.BookTicket &&
        hasNonWhiteSpaceContent(price) &&
        hasNonWhiteSpaceContent(buttonLabel) &&
        hasNonWhiteSpaceContent(url);
    const isGetTicket =
        ticketType === TicketType.GetTicket &&
        hasNonWhiteSpaceContent(buttonLabel) &&
        hasNonWhiteSpaceContent(url);
    const isNoTicket =
        ticketType === TicketType.NoTicket && hasNonWhiteSpaceContent(text);

    const isExist = isBookTicket || isGetTicket || isNoTicket;

    return (
        <Wrapper componentId={props.componentId} className={isExist ? "min-h-[160px] !mb-4" : "min-h-0 !mb-0"}>
            {isExist && <PriceWidgetComp {...props} />}
        </Wrapper>
    );
};

export default PriceWidget;

