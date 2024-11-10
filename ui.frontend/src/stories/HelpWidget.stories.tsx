import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";

import HelpWidget from "../main/components/page-components/HelpWidget/HelpWidget";

export default {
    title: "VisitSaudi/HelpWidget",
    component: HelpWidget,
} as ComponentMeta<typeof HelpWidget>;

const Template: ComponentStory<typeof HelpWidget> = (args: any) => (
    <HelpWidget {...args} />
);

export const helpWidget = Template.bind({});
helpWidget.args = {
    title: "Do you need help?",
    description: "<p>Do you have a question or need more information?</p>",
    cta: {
        url: "/content/sauditourism/en.html",
        text: "Get More Help",
    },
};
