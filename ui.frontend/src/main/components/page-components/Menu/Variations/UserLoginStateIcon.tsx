import React from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import Picture from "src/main/components/common/atoms/Picture/Picture";
import { trackAnalytics, trackingEvent, extractPageInfoFromEventProperties } from "src/main/util/updateAnalyticsData";

type Props = {
    scrolled: any;
    registerLabel: string;
    handleLogin: () => void;
    handleMenu: () => void;
    user: any;
};
const UserLoginStateIcon = ({
    scrolled,
    registerLabel,
    handleLogin,
    handleMenu,
    user,
}: Props) => {
    return (
        <>
            {!user ? (
                <div
                    className={`${scrolled
                        ? "bg-theme-100 hover:bg-theme-200 text-white"
                        : "bg-white hover:bg-gray-200 text-gray"
                        } py-3 px-5 rounded-full cursor-pointer transition-all duration-300
                    line-clamp truncate h-fit w-40 text-center text-sm font-primary-semibold`}
                    onClick={() => {

                        const pageInfo = extractPageInfoFromEventProperties();
                        trackingEvent({
                            event_name: "header_navigation",
                            title: registerLabel,
                            navigation_name: registerLabel,
                            page_category: "navigation",
                            page_subcategory: "",
                            city: pageInfo?.city,
                            event_category: "navigation"
                        });

                        trackAnalytics({
                            trackEventname: "header_navigation",
                            trackName: "dl_push",
                            title: registerLabel,
                            navigation_name: registerLabel,
                            page_category: "navigation",
                            page_subcategory: "",
                            city: pageInfo?.city,
                            event_category: "navigation"
                        });
                        setTimeout(() => {
                            handleLogin();
                        }, 400);
                    }}
                >
                    {registerLabel}
                </div>
            ) : user && !user?.pictureUrl ? (
                <div className="user-icon-blk" onClick={() => handleMenu()}>
                    <Icon
                        name="user-icon"
                        wrapperClass="inline-block w-10 h-10 my-auto rounded-full cursor-pointer"
                    />
                </div>
            ) : user && user?.pictureUrl ? (
                <span onClick={() => handleMenu()} className="cursor-pointer">
                    <Picture
                        image={user?.pictureUrl}
                        imageClassNames="!rounded-full !w-full !h-full"
                        containerClassName="!w-10 !h-10 !my-auto"
                    />
                </span>
            ) : null}
        </>
    );
};

export default UserLoginStateIcon;

