import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";

import Popup from "../main/components/page-components/Popup/Popup";

export default {
    title: "VisitSaudi/Popup",
    component: Popup,
    decorators: [(Story) => <Story />],
    argTypes: {},
} as ComponentMeta<typeof Popup>;

const Template: ComponentStory<typeof Popup> = (args: any) => (
    <Popup {...args} />
);

export const defaultVariation = Template.bind({});

defaultVariation.args = {
    title: "default popup",
    subTitle: "default subtitle",
    text: "popup text",
    cat1: { label: "Cancel" },
    cat2: { label: "Refresh" },
};

