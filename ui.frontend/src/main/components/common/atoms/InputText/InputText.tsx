import React, { FC, ReactNode } from "react";

interface InputTextProps {
    label?: string;
    value: string;
    onChange?: any;
    onKeyPress?: any;
    onBlur?: any;
    htmlFor: string;
    id: string;
    placeHolder?: string;
    style?: string;
    labelStyle?: string;
    containerStyle?: string;
    type?: string;
    minLength?: number;
    maxLength?: number;
    checked?: boolean;
    prefixIcon?: ReactNode;
}

const InputText: FC<InputTextProps> = ({
    label,
    value,
    onChange,
    onBlur,
    htmlFor,
    id,
    placeHolder,
    style,
    labelStyle,
    containerStyle,
    type,
    minLength,
    maxLength,
    onKeyPress,
    prefixIcon: PrefixComponent,
    checked,
}) => {
    return (
        <>
            <div className={`relative ${containerStyle}`}>
                {label ? (
                    <label className={labelStyle} htmlFor={htmlFor}>
                        {label}
                    </label>
                ) : null}

                {PrefixComponent && PrefixComponent}

                <input
                    type={type}
                    id={id}
                    value={value}
                    onChange={onChange}
                    onKeyPress={onKeyPress}
                    onBlur={onBlur}
                    placeholder={placeHolder}
                    className={style}
                    maxLength={maxLength}
                    minLength={minLength}
                    checked={checked}
                ></input>
            </div>
        </>
    );
};

export default InputText;

