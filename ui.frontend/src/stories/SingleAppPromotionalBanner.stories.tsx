import React from "react";
import { ComponentStory, ComponentMeta } from "@storybook/react";
import SingleAppPromotionalBanner from "../main/components/page-components/SingleAppPromotionalBanner/SingleAppPromotionalBanner";


export default {
    title: "VisitSaudi/SingleAppPromotionalBanner",
    component: SingleAppPromotionalBanner,
} as ComponentMeta<typeof SingleAppPromotionalBanner>;

const Template: ComponentStory<typeof SingleAppPromotionalBanner> = (args: any) => (
    <SingleAppPromotionalBanner {...args} />
);

export const appPromotionalBanner = Template.bind({});
appPromotionalBanner.args = {
    title: "For Umrah visit",
    description: "Nusuk is your app to perform Umrah or Ziyarah with the ability to pre-plan, book, and support your rituals.",
    link: {
        text: "nusuk.sa",
        url: "https://www.nusuk.sa/",
        icon: "https://www.haj.gov.sa/assets/images/qrcode.png",
        targetInNewWindow:true
    },
    stores: [
        {
            text: "Google Play",
            icon:  "https://www.nusuk.sa/svg/google-play-store-icon.svg",
            url: "https://play.google.com/store/apps/details?id=com.sejel.eatamrna&hl=en&gl=US",
        },
        {
            text: "App Store",
            icon:"https://www.nusuk.sa/svg/apple-store-icon.svg",
            url: "https://apps.apple.com/sa/app/%D9%86%D8%B3%D9%83-%D8%A7%D8%B9%D8%AA%D9%85%D8%B1%D9%86%D8%A7-%D8%B3%D8%A7%D8%A8%D9%82%D8%A7/id1532669630",
        },
        {
            text: "App Gallery",
            icon: "https://www.nusuk.sa/svg/huawei-app-gallery.svg",
            url: "https://appgallery.huawei.com/app/C103004637",
        },
    ],
    image: {
        fileReference: "https://i.ibb.co/WpjCsM2/Image-placeholder.png",
        mobileImageReference: "https://i.ibb.co/WpjCsM2/Image-placeholder.png",
        s7fileReference: "https://i.ibb.co/WpjCsM2/Image-placeholder.png",
        s7mobileImageReference: "https://i.ibb.co/WpjCsM2/Image-placeholder.png",
    },
};