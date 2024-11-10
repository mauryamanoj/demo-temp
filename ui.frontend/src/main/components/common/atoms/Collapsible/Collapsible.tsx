import React, { useState } from "react";

interface CollapsibleProps {
    title: string | React.ReactNode;
    className?: string;
    childrenClass?: string;
    collapseSectionHeight?: string;
    collapsed?: boolean;
    onToggle?: () => void;
    isFooter?: boolean;
}

const Collapsible: React.FC<CollapsibleProps> = ({
    title,
    className,
    childrenClass = "",
    collapseSectionHeight = "",
    children,
    collapsed = false,
    onToggle,
    isFooter = false
}) => {
    const [isCollapsed, setIsCollapsed] = useState(collapsed);

    const handleToggle = () => {
        setIsCollapsed(!isCollapsed);
        if (onToggle) {
            onToggle();
        }
    };

    return (
        <div>
            <div
                className="flex justify-between items-center cursor-pointer"
                onClick={() => handleToggle()}
            >
                <div className={`${className} uppercase w-11/12`}>{title}</div>
                <svg
                    className={`${isFooter ? "w-[23.5px] h-[23.5px]" : "w-[26px] h-[26px]"} transform ${
                        isCollapsed ? "rotate-0" : "rotate-180"
                    } transition-transform duration-300`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M19 9l-7 7-7-7"
                    />
                </svg>
            </div>
            <div
                className={`text-md text-gray-400 overflow-hidden
        ${
            collapseSectionHeight
                ? isCollapsed
                    ? " h-0"
                    : " h-fit"
                : ` transition-max-h duration-300 ease-in-out
        ${isCollapsed ? " max-h-0" : " max-h-screen"}`
        }`}
            >
                <div className={childrenClass}>{children}</div>
            </div>
        </div>
    );
};

export default Collapsible;

