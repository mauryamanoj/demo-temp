import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";

import Alert from "../main/components/page-components/Alerts/Alerts";

export default {
    title: "VisitSaudi/Alert",
    component: Alert,
} as ComponentMeta<typeof Alert>;

const Template: ComponentStory<typeof Alert> = (args: any) => <Alert {...args} />;

export const yellowAlert = Template.bind({});
yellowAlert.args = {
    alert: "Due to heavy rain all trips to Farsan islands will be suspended",
    alertColor: "Yellow",
};

export const greenAlert = Template.bind({});
greenAlert.args = {
    alert: "Due to heavy rain all trips to Farsan islands will be suspended",
    alertColor: "Green",
};
export const redAlert = Template.bind({});
redAlert.args = {
    alert: "Due to heavy rain all trips to Farsan islands will be suspended",
    alertColor: "Red",
};
export const greyAlert = Template.bind({});
greyAlert.args = {
    alert: "Due to heavy rain all trips to Farsan islands will be suspended",
    alertColor: "Grey",
};
