import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";

import HeaderSection from "../main/components/page-components/HeaderSection/HeaderSection";

export default {
    title: "VisitSaudi/HeaderSection",
    component: HeaderSection,
    argTypes: {},
} as ComponentMeta<typeof HeaderSection>;

const Template: ComponentStory<typeof HeaderSection> = (args: any) => (
    <HeaderSection {...args} />
);

export const WithImage = Template.bind({});
export const WithoutImage = Template.bind({});
export const WithoutSubtitle = Template.bind({});
export const WithoutAuthor = Template.bind({});

WithImage.args = {
    title: "this is hidden title",
    byLabel: "By",
    titleImage: {
        alt: "alt image",
        s7fileReference:
            "https://s7e5a.scene7.com/is/image/scthacc/Cirque-du-soleil-logo",
        desktopImage:
            "https://s7e5a.scene7.com/is/image/scthacc/Cirque-du-soleil-logo:crop-590x100?defaultImage=Cirque-du-soleil-logo",
        mobileImage:
            "https://s7e5a.scene7.com/is/image/scthacc/Cirque-du-soleil-logo:crop-375x280?defaultImage=Cirque-du-soleil-logo",
    },
    author: {
        authorText: "Author Text",
        image: {
            alt: "alt image",
            s7fileReference:
                "https://s7e5a.scene7.com/is/image/scth/16545149221184857235",
            desktopImage:
                "https://s7e5a.scene7.com/is/image/scth/16545149221184857235:crop-660x337?defaultImage\u003d16545149221184857235",
            mobileImage:
                "https://s7e5a.scene7.com/is/image/scth/16545149221184857235:crop-375x280?defaultImage\u003d16545149221184857235",
        },
        authorCtaLink: "Author Cta Link",
    },
};
WithoutImage.args = {
    title: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Soluta, officia?",
    subtitle: " Lorem dolor",
    byLabel: "By",
    author: {
        authorText: "Author Text",
        image: {
            alt: "alt image",
            s7fileReference:
                "https://s7e5a.scene7.com/is/image/scth/16545149221184857235",
            desktopImage:
                "https://s7e5a.scene7.com/is/image/scth/16545149221184857235:crop-660x337?defaultImage\u003d16545149221184857235",
            mobileImage:
                "https://s7e5a.scene7.com/is/image/scth/16545149221184857235:crop-375x280?defaultImage\u003d16545149221184857235",
        },
        authorCtaLink: "Author Cta Link",
    },
};

WithoutSubtitle.args = {
    title: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Soluta, officia?",
    byLabel: "By",
    author: {
        authorText: "Author Text",
        image: {
            alt: "alt image",
            s7fileReference:
                "https://s7e5a.scene7.com/is/image/scth/16545149221184857235",
            desktopImage:
                "https://s7e5a.scene7.com/is/image/scth/16545149221184857235:crop-660x337?defaultImage\u003d16545149221184857235",
            mobileImage:
                "https://s7e5a.scene7.com/is/image/scth/16545149221184857235:crop-375x280?defaultImage\u003d16545149221184857235",
        },
        authorCtaLink: "Author Cta Link",
    },
};
WithoutAuthor.args = {
    title: "Lorem ipsum dolor sit amet consectetur adipisicing elit. Soluta, officia?",
    subtitle: "amet consectetur adipisicing elit",
};

