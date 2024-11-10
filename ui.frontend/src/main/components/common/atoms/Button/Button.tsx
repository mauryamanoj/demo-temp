/* eslint-disable max-len */
import React, { ReactFragment } from 'react';

import Icon from '../Icon/Icon';

type ButtonProps = {
    title: any;
    styles?: string;
    disabled?: 'default' | 'active' | 'disabled';
    transparent?: boolean;
    size?: 'big' | 'medium' | 'small';
    buttonType?: string;
    onclick?: () => void;
    arrows?: boolean;
    rightArrowOnly?:boolean;
    children?: ReactFragment
    spanStyle?: string
    type?: any
};

const Button: React.FC<ButtonProps> = ({ title, styles, buttonType = 'type1', disabled, transparent = false, size = 'medium', onclick, arrows = true, rightArrowOnly, children, spanStyle, type }) => {

    let backgroundColor = 'bg-theme-100 hover:bg-theme-200';
    let borderColor = 'border border-theme-100 hover:border-theme-200';
    let opacity = 'opacity-1';

    let textColor = 'text-white';
    let cursor = 'cursor-pointer';

    let fontSize = 'text-sm';
    let padding = 'py-1.5 px-4';
    let textPadding = 'px-3';


    if (size === 'big') {
        fontSize = 'text-base';
        padding = 'py-2.5 px-6';
        textPadding = 'px-4';
    } else if (size === 'small') {
        fontSize = 'text-xs';
        padding = 'py-1.5 px-2';
        textPadding = 'px-1.5';
    }

    if (buttonType === 'type2') {
        backgroundColor = 'bg-transparent';
        textColor = 'text-black';
    } else if (buttonType === 'type3') {
        backgroundColor = 'bg-transparent  hover:bg-transparent';
        borderColor = 'none';
        textColor = 'text-theme-100 hover:text-theme-200';
    }

    if (transparent) {
        backgroundColor = 'bg-transparent';
        borderColor = 'border border-white hover:border-gray-300';
        textColor = 'text-white';
    }

    if (disabled) {
        cursor = 'cursor-default';
        opacity = 'opacity-50';
    }
    return (
        <button
            className={`m-auto rounded-lg flex items-center transition-all duration-300 ${backgroundColor} ${borderColor} ${textColor} ${padding} ${fontSize} ${cursor} ${opacity} ${styles ? styles : ''}`}
            disabled={disabled === 'disabled'}
            onClick={onclick}
            // title={title} if title props  is not string it will be [object object] the output
            type={type}
        >
            {children}
            {arrows && <Icon name="chevron" svgClass={`w-3 h-3 rotate-180 rtl:rotate-0`} />}

            <span className={`${textPadding ? textPadding : ''} ${spanStyle && spanStyle}`}>{typeof title === "string" ?  title?.substring(0,30): title}</span>

            {(arrows || rightArrowOnly) && <Icon name="chevron" svgClass={`w-3 h-3  rtl:rotate-180`} />}

        </button>
    );
};

export default Button;
