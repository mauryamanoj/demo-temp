import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";

import TextImageSection from "../main/components/page-components/TextImageSection/TextImageSection";

export default {
    title: "VisitSaudi/TextImageSection",
    component: TextImageSection,
    argTypes: {},
} as ComponentMeta<typeof TextImageSection>;

const Template: ComponentStory<typeof TextImageSection> = (args: any) => (
    <TextImageSection {...args} />
);

export const WithImage = Template.bind({});
export const WithoutImage = Template.bind({});

WithImage.args = {
    title: "this is title text",
    description:
        "Lorem ipsum dolor sit amet consectetur adipisicing elit. Mollitia hic tempore dolore iure iusto, rerum, doloribus fuga repudiandae libero quo sed debitis modi, incidunt ratione voluptatem porro ipsam amet esse!",
    image: {
        alt: "alt image",
        s7fileReference: "https://i.ibb.co/WpjCsM2/Image-placeholder.png",
        fileReference: "https://i.ibb.co/WpjCsM2/Image-placeholder.png",
    },
    logo: {
        alt: "alt image",
        s7fileReference: "https://i.ibb.co/WpjCsM2/Image-placeholder.png",
        fileReference: "https://i.ibb.co/WpjCsM2/Image-placeholder.png",
        isTransparent: true,
    },
};
WithoutImage.args = {
    title: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Soluta, officia?",
    description: `Lorem ipsum dolor sit amet consectetur adipisicing elit. Mollitia hic tempore dolore iure iusto,
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Mollitia hic tempore dolore iure iusto,
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Mollitia hic tempore dolore iure iusto,
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Mollitia hic tempore dolore iure iusto,
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Mollitia hic tempore dolore iure iusto,
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Mollitia hic tempore dolore iure iusto,
         rerum, doloribus fuga repudiandae libero quo sed debitis modi, incidunt ratione voluptatem porro ipsam amet esse`,
};

