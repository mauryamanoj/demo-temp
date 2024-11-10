import React, { useRef } from "react";
import Icon from "../Icon/Icon";
import Button from "../Button/Button";
import Text from "../Text/Text";
import { PopupType } from "src/main/components/page-components/Popup/IPopup";

export type ILoginModalProps = {
    title: string;
    paragraph: string;
    button: {
        copy: string;
        link: string;
    };
};

const LoginModal = ({ title, paragraph, button }: ILoginModalProps) => {
    const modalRef = useRef<HTMLDivElement>(null);
    const toggleModalVisibility = () => {
        if (modalRef.current) {
            modalRef.current?.classList.add("hidden");
            modalRef.current?.classList.add("invisible");
            document.body.style.overflow = "visible";
        }
    };

    const iconName = (elementType: PopupType): string => {
        return `popup_${elementType}_icon`;
    };
    return (
        <>
            <div
                id="login-modal-component"
                className="fixed inset-0 flex items-center justify-center backdrop-blur-0.5 w-screen invisible"
                style={{ zIndex: 99999999999999 }}
                ref={modalRef}
            >
                <div
                    className="w-[353px] md:w-[510px] min-h-fit p-4 md:p-8 bg-white rounded-2xl
                               flex-col justify-center items-center shadow-md"
                >
                    <div>
                        <div onClick={toggleModalVisibility}>
                            <Icon
                                name="close"
                                svgClass="ml-auto rtl:mr-auto rtl:ml-0 cursor-pointer"
                            />
                        </div>
                        <div className="grid gap-4">
                            <Icon
                                name={iconName(PopupType.Default)}
                                svgClass="mx-auto"
                                wrapperClass="fill-themed"
                            />

                            <div
                                className="self-stretch flex-col justify-center items-center
                                                     gap-0 md:gap-0 flex"
                            >
                                <Text
                                    styles="text-xl md:text-3.5xl font-primary-bold capitalize"
                                    text={title}
                                />

                                <Text
                                    styles="text-sm md:text-base text-center text-neutral-500
                                                     h-full mt-2 md:mt-4"
                                    text={paragraph}
                                />
                            </div>
                        </div>

                        <div className="justify-center items-center inline-flex gap-2 md:gap-6 mt-12 w-full">
                            <Button
                                title={button.copy}
                                size={"big"}
                                styles="justify-center items-center inline-flex w-full"
                                arrows={false}
                            />
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default LoginModal;

