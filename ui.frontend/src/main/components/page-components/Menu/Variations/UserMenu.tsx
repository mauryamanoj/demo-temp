/* eslint-disable max-len */
import React, {  useRef } from "react";
import Icon from "src/main/components/common/atoms/Icon/Icon";

const UserMenu: React.FC<any> = (props) => {
    const { userMenu, handleClose } = props;
    const { subMenu, signOutLabel, welcomeLabel } = userMenu;
    const ref = useRef<any>();
    var profileData: any = sessionStorage.getItem("profileData");
    if (profileData) {
        profileData = JSON.parse(profileData);
    }

    // todo: try to find other solution
    function handleIconSpaces(len: any, index: any) {
        if (index == 0) {
            return "ltr:ml-2 rtl:mr-2";
        }
        if (index == 1 || index == 2) {
            return "ltr:ml-[8px] rtl:mr-[8px]";
        }

        if (len - 1 == index) {
            return "ltr:ml-[8px] rtl:mr-[8px]";
        }

        return "ltr:ml-[10px] rtl:mr-[10px]";
    }

    function handleSignOut() {
        props.handleLogout();
    }

    return (
        <>
            <div
                className="fixed top-0 left-0 w-full h-screen z-[50]"
                onClick={(e) => {
                    handleClose();
                    e.stopPropagation();
                }}
            />
            <div
                id="userMenuComponent"
                ref={ref}
                className="menu-blk relative ltr:lg:ml-[-1.55%] rtl:lg:mr-[-1.55%] z-[60]"
            >
                <div className="absolute text-base max-w-[302px] ltr:right-0 rtl:left-0 top-12 bg-white text-black rounded-2xl overflow-hidden">
                    <div>
                        <Icon name="menuFrame" />
                    </div>
                    <div>
                        {/* start user info */}
                        <ul className="p-4">
                            <li className="font-primary-regular">
                                {welcomeLabel ? <>{welcomeLabel},</> : null}{" "}
                                <span className="font-primary-semibold">
                                    {profileData?.firstName}
                                </span>
                            </li>
                            <li className="text-xs text-[#4B4B4B] font-primary-regular">
                                {profileData?.email}
                            </li>
                        </ul>
                        {/* end user info */}

                        {/* start menu personal info... */}
                        <ul className="font-primary-regular">
                            {/* start user name and email */}
                            {subMenu.map(
                                (item: any, index: any) =>
                                    item.label &&
                                    item.url &&
                                    item.iconName && (
                                        <li className="flex items-center lg:hover:bg-[#F2F2F2] px-4 py-[13.5px] cursor-pointer border-b-[1px] border-t-[1px] border-[#F2F2F2]">
                                            <a
                                                href={item?.url}
                                                className="flex items-center"
                                            >
                                                <Icon
                                                    name={item?.iconName}
                                                    wrapperClass="fill-themed"
                                                />
                                                <span
                                                    className={handleIconSpaces(
                                                        subMenu?.length,
                                                        index
                                                    )}
                                                >
                                                    {item?.label}
                                                </span>
                                            </a>
                                        </li>
                                    )
                            )}
                            {/* end user name and email */}

                            {/* start Sign out  */}
                            {signOutLabel ? (
                                <li
                                    onClick={handleSignOut}
                                    className="flex items-center lg:hover:bg-[#F2F2F2] px-4 py-[13.5px] cursor-pointer"
                                >
                                    <Icon
                                        name="signoutMenu"
                                        wrapperClass="fill-themed"
                                    />
                                    <span className="ltr:ml-[4px] rtl:mr-[4px] text-sm font-primary-bold themed">
                                        {signOutLabel}
                                    </span>
                                </li>
                            ) : null}
                            {/* end Sign out  */}
                        </ul>
                        {/* end menu personal info... */}
                    </div>
                </div>
            </div>
        </>
    );
};
export default UserMenu;

