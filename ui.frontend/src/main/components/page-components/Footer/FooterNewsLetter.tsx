/* eslint-disable max-len */
import React, { useEffect, useRef, useState } from "react";
import { FragmentNewsletter } from "./IFooter";

import Button from "src/main/components/common/atoms/Button/Button";
import Icon from "src/main/components/common/atoms/Icon/Icon";
import api from "src/main/util/api";

import { ValidateEmail } from "src/main/util/validations";

const FooterNewsLetter: React.FC<FragmentNewsletter> = (props) => {
    const {
        title,
        placeholder,
        ctaLabel,
        invalidEmailMessage,
        successMessage,
        failureMessage,
        apiUrl,
    } = props;

    const [isLoading, setIsLoading] = useState(false);
    const [infoMsg, setInfoMsg] = useState<string | undefined>();
    const emailInputRef: any = useRef();

    const handleSubscribe = () => {
        setInfoMsg(undefined);
        const emailValue = emailInputRef.current.value;

        if (ValidateEmail(emailValue)) {
            const url = `${apiUrl}`;
            //url = window.location.hostname != 'localhost' ? url : `https://qa-oci.visitsaudi.com${url}`;

            setIsLoading(true);
            api.post({
                url,
                data: {
                    email: emailValue,
                    url: encodeURIComponent(window.location.href),
                },
            }).then(
                (res: any) => {
                    setIsLoading(false);
                    if (res.status == 200) {
                        if (res.data.message == "success") {
                            emailInputRef.current.value = "";
                            setInfoMsg("success");
                        } else {
                            setInfoMsg("faliur");
                        }
                    } else {
                        setInfoMsg("faliur");
                    }
                },
                (err) => {
                    setIsLoading(false);
                    setInfoMsg("faliur");
                }
            );
        } else {
            emailInputRef.current.focus();
            setInfoMsg("invalidEmail");
        }
    };

    useEffect(() => {
        const timer = setTimeout(() => {
            setInfoMsg(undefined);
        }, 10000); // 10 seconds

        return () => {
            clearTimeout(timer);
        };
    }, [infoMsg]);

    return (
        <div className="flex flex-col w-full md:w-[279px] relative">
            {isLoading && (
                <div className="absolute h-full w-full flex justify-end bg-white/30 z-[11]">
                    <span className="relative flex h-5 w-5 opacity-75">
                        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-theme-100 opacity-75"></span>
                        <span className="relative inline-flex rounded-full h-5 w-5 bg-theme-200"></span>
                    </span>
                </div>
            )}

            <p className="select-none font-primary-bold mb-2">{title}</p>
            <div className="h-10 rounded-lg border border-gray-50 flex justify-between items-center px-2 gap-2 bg-white">
                <input
                    type="text"
                    className="text-base h-full outline-none w-full ml-2 rtl:mr-2 placeholder:font-primary-regular placeholder:text-[#4B4B4B] placeholder:text-base"
                    placeholder={placeholder}
                    ref={emailInputRef}
                />
                <Button
                    onclick={handleSubscribe}
                    title={ctaLabel}
                    arrows={false}
                    size="small"
                    styles="max-w-[110px] pt-0.5 pb-0.5"
                    spanStyle="font-primary-semibold truncate text-ellipsis !px-0"
                />
            </div>
            <div
                className={`mt-2  ${
                    infoMsg ? "h-10" : "h-0"
                } overflow-hidden duration-300 transition-all md:absolute md:top-full`}
            >
                {infoMsg == "success" && (
                    <div className="text-success flex gap-1 items-start">
                        <Icon name="check" svgClass="mt-[3px]" />
                        <span className="line-clamp-1">{successMessage}</span>
                    </div>
                )}
                {infoMsg == "faliur" && (
                    <div className="text-danger flex gap-1 items-start">
                        <Icon name="x-circled" svgClass="mt-[3px]" />
                        <span className="line-clamp-1">{failureMessage} </span>
                    </div>
                )}
                {infoMsg == "invalidEmail" && (
                    <div className="text-danger flex gap-1 items-start">
                        <Icon name="x-circled" svgClass="mt-[3px]" />
                        <span className="line-clamp-1">
                            {invalidEmailMessage}{" "}
                        </span>
                    </div>
                )}
            </div>
        </div>
    );
};

export default FooterNewsLetter;

