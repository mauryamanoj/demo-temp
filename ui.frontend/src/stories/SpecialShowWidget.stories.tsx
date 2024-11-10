import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";
import SpecialShowWidget from "../main/components/page-components/SpecialShowWidget/SpecialShowWidget";



export default {
    title: "VisitSaudi/SpecialShowWidget",
    component: SpecialShowWidget,
} as ComponentMeta<typeof SpecialShowWidget>;

const Template: ComponentStory<typeof SpecialShowWidget> = (args: any) => (
    <SpecialShowWidget {...args} />
);

export const specialShow = Template.bind({});
specialShow.args = {
    title: "Special Shows",

    data: [
        {
            key: "Monday",
            value: "Saudi Aud night",
        },
        {
            key: "Sunday",
            value: "Cultural dance",
        },
    ],
};
