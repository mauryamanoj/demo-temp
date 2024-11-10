import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";

import PriceWidget from "../main/components/page-components/PriceWidget/PriceWidget";
import { TicketType } from "../main/components/page-components/PriceWidget/IPriceWidget";

// More on default export: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
export default {
    title: "VisitSaudi/PriceWidget",
    component: PriceWidget,

    // More on argTypes: https://storybook.js.org/docs/react/api/argtypes
    // argTypes: {
    //     backgroundColor: { control: "color" },
    // },
} as ComponentMeta<typeof PriceWidget>;

// More on component templates: https://storybook.js.org/docs/react/writing-stories/introduction#using-args
const Template: ComponentStory<typeof PriceWidget> = (args: any) => (
    <PriceWidget {...args} />
);

export const bookTicket = Template.bind({});
export const getTicket = Template.bind({});
export const noTicket = Template.bind({});

bookTicket.args = {
    ticketType: TicketType.BookTicket,
    price: "1000 SAR",
    priceSuffix: "/ per person",
    text: "<p>Minumul number of 3 person</p>\n",
    buttonLabel: "Buy Now",
    url: "/content/sauditourism/app.html",
};

getTicket.args = {
    ticketType: TicketType.GetTicket,
    price: "1000 SAR",
    priceSuffix: "/ per person",
    text: "<p>Tickets information are available on the event main website</p>\n",
    buttonLabel: "Get Ticket",
    url: "/content/sauditourism/app.html",
};

noTicket.args = {
    ticketType: TicketType.NoTicket,
    price: "1000 SAR",
    priceSuffix: "/ per person",
    text: "<p>You don’t need tickets to enter Jazan Islands</p>\n",
    buttonLabel: "Buy Now",
    url: "/content/sauditourism/app.html",
};

