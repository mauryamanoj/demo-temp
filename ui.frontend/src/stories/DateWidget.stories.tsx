import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";
import DateWidget from "../main/components/page-components/DateWidget/DateWidget";


export default {
    title: "VisitSaudi/DateWidget",
    component: DateWidget,
} as ComponentMeta<typeof DateWidget>;

const Template: ComponentStory<typeof DateWidget> = (args: any) => (
    <DateWidget {...args} />
);

export const multipleDateEvent = Template.bind({});
multipleDateEvent.args = {
    title: "Expired Event",
    startDate: "2023-10-01T00:00:00.000+03:00",
    endDate: "2023-10-15T00:00:00.000+03:00",
    comingSoonLabel: "",
    expiredLabel: "Expired!",
};

export const singleDateEvent = Template.bind({});
singleDateEvent.args = {
    title: "Single Date Event",
    startDate: "2023-10-01T00:00:00.000+03:00",
    endDate: "2023-10-01T00:00:00.000+03:00",
    comingSoonLabel: "",
    expiredLabel: "Expired!",
};

export const comingSoonEvent = Template.bind({});
comingSoonEvent.args = {
    title: "Current Event",
    startDate: "",
    endDate: "",
    comingSoonLabel: "Coming Soon!",
    expiredLabel: "",
};
