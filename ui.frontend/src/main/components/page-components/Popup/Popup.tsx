import React, { useEffect, useRef } from "react";
import { IPopup, PopupType } from "./IPopup";
import Button from "../../common/atoms/Button/Button";
import Icon from "../../common/atoms/Icon/Icon";
import Text from "../../../components/common/atoms/Text/Text";
import { useResize } from "../../../util/hooks/useResize";
import ReactDOM from "react-dom";

const Popup = ({
    icon,
    type,
    title,
    text,
    subTitle,
    cat1,
    cat2,
    handleClose = () => null,
}: IPopup) => {
    const { isMobile } = useResize();
    const popupRef = useRef<any>();

    useEffect(() => {
        const handleOutsideClick = (event: any) => {
            // Close the popup if the click is outside the popup content
            if (
                popupRef.current &&
                !popupRef?.current?.contains(event.target) &&
                handleClose
            ) {
                handleClose();
            }
        };

        // Attach the event listener
        document.addEventListener("mousedown", handleOutsideClick);

        // Cleanup the event listener when the component unmounts
        return () => {
            document.removeEventListener("mousedown", handleOutsideClick);
        };
    }, [handleClose]);

    const iconName = (elementType: PopupType): string => {
        return `popup_${elementType}_icon`;
    };

    return (
        <>
            {ReactDOM.createPortal(
                <div className="fixed" style={{ zIndex: 99999999999999 }}>
                    <div className="fixed inset-0 flex items-center justify-center backdrop-blur-0.5 w-screen">
                        <div
                            className="w-[353px] md:w-[510px] min-h-fit p-4 md:p-8 bg-white rounded-2xl
                                       flex-col justify-center items-center shadow-md"
                            ref={popupRef}
                        >
                            <div onClick={handleClose}>
                                <Icon
                                    name="close"
                                    svgClass="ml-auto rtl:mr-auto rtl:ml-0 cursor-pointer"
                                />
                            </div>
                            <div className="grid gap-4">
                                {(icon && (
                                    <Icon name={icon} svgClass="mx-auto" />
                                )) || (
                                    <Icon
                                        name={iconName(type)}
                                        svgClass="mx-auto"
                                        wrapperClass="fill-themed"
                                    />
                                )}
                                <div className="self-stretch flex-col justify-center items-center gap-0 md:gap-0 flex">
                                    <Text
                                        styles="text-xl md:text-3.5xl font-primary-bold capitalize"
                                        text={title}
                                    />
                                    <Text
                                        styles="text-lg md:text-1.5xl font-['Gilroy-SemiBold'] capitalize "
                                        text={subTitle}
                                    />
                                    <Text
                                        styles="text-sm md:text-base text-center text-neutral-500 h-full mt-2 md:mt-4"
                                        text={text}
                                    />
                                </div>
                            </div>
                            {(cat1 || cat2) && (
                                <div className="justify-center items-center inline-flex gap-2 md:gap-6 mt-12 w-full">
                                    {cat1 && (
                                        <Button
                                            title={cat1?.label}
                                            size={!isMobile ? "big" : "small"}
                                            buttonType="type3"
                                            styles="justify-center items-center inline-flex w-full"
                                            arrows={false}
                                        />
                                    )}
                                    {cat2 && (
                                        <Button
                                            title={cat2?.label}
                                            size={!isMobile ? "big" : "small"}
                                            styles="justify-center items-center inline-flex w-full"
                                            arrows={false}
                                        />
                                    )}
                                </div>
                            )}
                        </div>
                    </div>
                </div>,
                document.body
            )}
        </>
    );
};

export default Popup;

