/* eslint-disable max-len */

// Stylesheets
//import "./styles/main.scss";

import "./styles/main.css";

import { Bootstrapper, ContainerBuilder } from "react-habitat";
import {
    EventsCtaEventData,
    FormsFilters,
    PackagesSignupFormData,
    PackageTourEventData,
    SearchFilterEventData,
    SearchForm,
    TabsEventData,
    TripActivitiesEventData,
    TripCreationFlowEventData,
    TripPlanEventData,
    UserLogin,
    UserLogout,
    UserRegistered,
    UserUpdateProfile,
    VideoEventData,
} from "src/main/type/eventDataLayer";

import { gtmLinkClick, gtmPageMetaData } from "../util/googleTagManager";
import { getLanguage } from "../util/getLanguage";
import { uniqueId } from "lodash";
import { getAllLinks } from "../util/getAllLinks";
import MainMenu from "../components/page-components/Menu/MainMenu";
import PromotionalBanner from "../components/page-components/PromotionalBanner/PromotionalBanner";
import BannerSlider from "../components/page-components/BannerSlider/BannerSlider";
// import NavWrapper from "../components/page-components/navigation/NavWrapper";

declare global {
    interface Window {
        readonly appEventData: Array<
            | TabsEventData
            | SearchFilterEventData
            | VideoEventData
            | PackagesSignupFormData
            | PackageTourEventData
            | FormsFilters
            | SearchForm
            | UserUpdateProfile
            | UserLogout
            | UserLogin
            | UserRegistered
            | TripPlanEventData
            | TripActivitiesEventData
            | TripCreationFlowEventData
            | EventsCtaEventData
        >;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        readonly Granite: any;
        readonly pageinfo: any;
        readonly adobeSettingScript: any;
    }
}

class MyApp extends Bootstrapper {
    private contentLinks: Array<HTMLAnchorElement> = [];
    private timeoutId: any;
    constructor() {
        super();

        // Create a new container builder:
        const builder = new ContainerBuilder();

        // Register a component:
        // builder.register(NavWrapper).as('NavWrapper');
        //builder.register(SecondaryHero).as('SecondaryHero');

        // Or register a component to load on demand asynchronously:

        builder.register(BannerSlider).as("BannerSlider");
        builder.register(MainMenu).as("MainMenu");
        builder.register(PromotionalBanner).as("PromotionalBanner");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/AtomsShowcase/AtomsShowcase"
                            )
                        );
                    })
            )
            .as("AtomsShowcase");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Breadcrumbs/Breadcrumbs"
                            )
                        );
                    })
            )
            .as("Breadcrumbs");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Footer/Footer"
                            )
                        );
                    })
            )
            .as("Footer");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Anchors/Anchors"
                            )
                        );
                    })
            )
            .as("Anchors");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/ParagraphPage/ParagraphPage"
                            )
                        );
                    })
            )
            .as("ParagraphPage");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Typography/Typography"
                            )
                        );
                    })
            )
            .as("Typography");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Typography/TypographyArabic"
                            )
                        );
                    })
            )
            .as("TypographyArabic");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import("../components/page-components/Cards/Cards")
                        );
                    })
            )
            .as("Cards");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import("../components/page-components/Tabs/Tabs")
                        );
                    })
            )
            .as("Tab");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/SearchResult/SearchResult"
                            )
                        );
                    })
            )
            .as("SearchResult");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Alerts/Alerts"
                            )
                        );
                    })
            )
            .as("Alert");

            builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Errors/Errors"
                            )
                        );
                    })
            )
            .as("Errors");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/ContactUs/ContactUs"
                            )
                        );
                    })
            )
            .as("ContactUs");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/LatestStories/LatestStories"
                            )
                        );
                    })
            )
            .as("LatestStories");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/HelpWidget/HelpWidget"
                            )
                        );
                    })
            )
            .as("HelpWidget");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/PromotionalSection/PromotionalSection"
                            )
                        );
                    })
            )
            .as("PromotionalSection");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/AllDestinations/AllDestinations"
                            )
                        );
                    })
            )
            .as("AllDestinations");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "src/main/components/page-components/DestinationsAndMap/DestinationsAndMap"
                            )
                        );
                    })
            )
            .as("DestinationsAndMap");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "src/main/components/page-components/AttractionsAndMap/AttractionsAndMap"
                            )
                        );
                    })
            )
            .as("AttractionsAndMap");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "src/main/components/page-components/AboutSection/AboutSection"
                            )
                        );
                    })
            )
            .as("AboutSection");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/InformationSection/InformationSection"
                            )
                        );
                    })
            )
            .as("InformationSection");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/MapWidget/MapWidget"
                            )
                        );
                    })
            )
            .as("MapWidget");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/ReachUs/ReachUs"
                            )
                        );
                    })
            )
            .as("ReachUs");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/HeaderSection/HeaderSection"
                            )
                        );
                    })
            )
            .as("HeaderSection");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import("../components/page-components/Popup/Popup")
                        );
                    })
            )
            .as("Popup");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/TextImageSection/TextImageSection"
                            )
                        );
                    })
            )
            .as("TextImageSection");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/SingleAppPromotionalBanner/SingleAppPromotionalBanner"
                            )
                        );
                    })
            )
            .as("SingleAppPromotionalBanner");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/DateWidget/DateWidget"
                            )
                        );
                    })
            )
            .as("DateWidget");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/SpecialShowWidget/SpecialShowWidget"
                            )
                        );
                    })
            )
            .as("SpecialShowWidget");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Gallery/MediaGallery/MediaGallery"
                            )
                        );
                    })
            )
            .as("MediaGallery");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/PriceWidget/PriceWidget"
                            )
                        );
                    })
            )
            .as("PriceWidget");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Gallery/StoryDetails/StoryDetails"
                            )
                        );
                    })
            )
            .as("StoryDetails");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/ThingsToDoCards/ThingsToDoCards"
                            )
                        );
                    })
            )
            .as("ThingsToDoCards");

        builder
        .registerAsync(
            () =>
                new Promise((resolve) => {
                    resolve(
                        import(
                            "../components/page-components/FilterExplorer/FilterExplorer"
                        )
                    );
                })
        )
        .as("FilterExplorer");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/InformationListWidget/InformationListWidget"
                            )
                        );
                    })
            )
            .as("InformationListWidget");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Cards/AppList/AppListCards"
                            )
                        );
                    })
            )
            .as("AppListCards");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import("../components/page-components/Faq/Faq")
                        );
                    })
            )
            .as("Faq");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/NeighborhoodsSection/NeighborhoodsSection"
                            )
                        );
                    })
            )
            .as("NeighborhoodsSection");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/OffersAndDeals/OffersAndDeals"
                            )
                        );
                    })
            )
            .as("OffersAndDeals");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/Visa/VisaRegulation/VisaRegulation"
                            )
                        );
                    })
            )
            .as("VisaRegulation");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/FilterSection/FilterSection"
                            )
                        );
                    })
            )
            .as("FilterSection");

        builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/common/atoms/LoginModal/LoginModal"
                            )
                        );
                    })
            )
            .as("LoginModal");

            builder
            .registerAsync(
                () =>
                    new Promise((resolve) => {
                        resolve(
                            import(
                                "../components/page-components/LoyaltyEnrollment"
                            )
                        );
                    })
            )
            .as("LoyaltyEnrollment");
        /**
         * GTM analytics starts
         */

        document.addEventListener("DOMContentLoaded", () => {
            this.setGTMPageData();
            this.setGTMLinkClick();
            this.insertAdobeScript();
        });
        window.onscroll = () => {
            clearTimeout(this.timeoutId);
            this.timeoutId = setTimeout(() => {
                this.setGTMLinkClick();
            }, 500);
        };

        /**
         * GTM analytics ends
         */

        // Finally, set the container:
        this.setContainer(builder.build());
        this.dispose();
    }

    private readonly insertAdobeScript = (): void => {
        // document.onreadystatechange = () => {
        if (
            (document.readyState === "interactive" ||
                document.readyState === "complete") &&
            window.adobeSettingScript
        ) {
            try {
                const sTag = document.createElement(
                    "script"
                ) as HTMLScriptElement;
                sTag.src = window.adobeSettingScript;
                //sTag.async = true;
                sTag.id = "AdobeAnalyticsScript";
                document.head.appendChild(sTag);
            } catch (err) {
                console.log("Adobe >> PlugIn Inject Error", err);
            }
        } else {
            // console.log('Adobe >> PlugIn src not available!');
        }
        // };
    };

    private readonly setGTMPageData = (): void => {
        let environment = "production";
        if (process.env.NODE_ENV === "development") {
            environment = "development";
        }

        const url = new URL(window.location.href);

        let campaing = "";
        const campaignValue = url.searchParams.get("utm_campaing");
        if (campaignValue !== null) {
            campaing = campaignValue;
        }

        let language = "";
        const dropdown = getLanguage();
        if (dropdown !== null) {
            language = dropdown;
        }

        let userId;
        const userIdCookie = window.sessionStorage.getItem("userId");
        if (userIdCookie !== null) {
            userId = userIdCookie;
        } else {
            userId = uniqueId("uid_");
            window.sessionStorage.setItem("userId", userId);
        }

        let userVisa = false;
        const userVisaCookie = window.sessionStorage.getItem("userVisa");
        if (userVisaCookie !== null) {
            userVisa = true;
        }

        const pageInfo = document.title.split(" - ");
        const title = pageInfo[0];
        const section = pageInfo[1];

        gtmPageMetaData({
            event: "pageMetaData",
            page_campaign: campaing,
            page_country: "SA",
            page_environment: environment,
            page_language: language,
            page_path: window.location.pathname,
            page_section: section,
            page_title: title,
            user_id: userId,
            user_visa: `${userVisa}`,
        });
    };

    private readonly setGTMLinkClick = (): void => {
        this.contentLinks = getAllLinks();
        this.contentLinks.forEach((link) =>
            link.addEventListener("click", this.onLinkClickHandler, {
                once: true,
            })
        );
    };

    private readonly onLinkClickHandler = (event: Event): void => {
        if (!event.currentTarget) {
            return;
        }

        const componentId =
            (event.currentTarget as HTMLAnchorElement).getAttribute(
                "data-location"
            ) || "";
        const action = (event.currentTarget as HTMLAnchorElement).href;

        gtmLinkClick({
            event: "linkClick",
            component_id: componentId,
            event_action: action,
            event_category: "link",
            event_label: window.location.pathname,
        });
    };

    public dispose = () => {
        this.contentLinks.forEach((link) =>
            link.removeEventListener("click", this.onLinkClickHandler)
        );
    };
}

// Always export a 'new' instance so it immediately evokes:
export default new MyApp();

