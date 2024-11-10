import React from 'react';

export const Checkbox: React.FC<any> = (props) => {
    const {
        handleChange,
        onClick,
        item,
        style,
        labelStyle,
        name,
        labelText,
        checked,
        defaultChecked,
        type = "checkbox"
    } = props;

    return (
        <>
            {item?.value || labelText ?
                <>
                <input
                    onClick={onClick ? (e)=>onClick(e, name):()=>{console.log()}}
                    onChange={(e: any) => handleChange(e, name)}
                    className={style}
                    type={type}
                    name={name}
                    defaultChecked={defaultChecked}
                    checked={checked}
                    value={name}
                    id={name || labelText} />
                <label
                    className={labelStyle}
                    htmlFor={name || labelText}>
                    {props.children}
                    {item?.value || labelText}
                </label></> : null}

        </>
    );
};
