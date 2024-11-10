import React from "react";
import Icon from "../../common/atoms/Icon/Icon";

interface AlertProps {
    alert: string;
    alertColor: string;
}

interface IconMap {
    [key: string]: string;
}

const icons: IconMap = {
    Green: "alertSuccessCircle",
    Red: "alertDangerOctagon",
    Yellow: "alertWarningCircle",
    Grey: "alertInfoCircle",
};

const Alert: React.FC<AlertProps> = ({ alert, alertColor }) => {
    const iconName = icons[alertColor] || icons.warning;

    const getBackgroundColor = () => {
        switch (alertColor) {
            case "Green":
                return "bg-success";
            case "Red":
                return "bg-danger";
            case "Grey":
                return "bg-info";
            case "Yellow":
                return "bg-warning";
            default:
                return "bg-info";
        }
    };
    return (
        <>
            {alert && alertColor && (
                <div
                    className={`flex justify-start items-center rounded-2xl px-2 py-4 md:px-4 md:py-6  mb-4 ${getBackgroundColor()}`}
                >
                    <Icon name={iconName} svgClass={"mr-2 md:mr-4"} />
                    <div
                        className={`text-sm md:text-base font-primary-semibold leading-4 md:leading-5 ${
                            alertColor === "Green" || alertColor === "Red"
                                ? "text-white"
                                : ""
                        }`}
                        dangerouslySetInnerHTML={{ __html: alert }}
                    ></div>
                </div>
            )}
        </>
    );
};

export default Alert;

