import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";

import MapWidget from "../main/components/page-components/MapWidget/MapWidget";

export default {
    title: "VisitSaudi/MapWidget",
    component: MapWidget,
} as ComponentMeta<typeof MapWidget>;

const Template: ComponentStory<typeof MapWidget> = (args: any) => (
    <MapWidget {...args} />
);

export const meetingPoint = Template.bind({});
meetingPoint.args = {
    mapApiKey: "pk.eyJ1Ijoic3RlcGFubiIsImEiOiJjanpianFwN28wMDVyM21sNzZpZnU2MGlpIn0.ZfNlZo3jpVmAEmvnzTQFbQ",
    title: "Meeting point",
    locationLabel: "Location:",
    locationValue: "riyadh",
    latitude: "24.774265",
    longitude: "46.738586",
    ctaLabel: "Get directions",
};

export const directionWidget = Template.bind({});
directionWidget.args = {
    mapApiKey: "pk.eyJ1Ijoic3RlcGFubiIsImEiOiJjanpianFwN28wMDVyM21sNzZpZnU2MGlpIn0.ZfNlZo3jpVmAEmvnzTQFbQ",
    latitude: "24.774265",
    longitude: "46.738586",
    ctaLabel: "Get directions",
};
