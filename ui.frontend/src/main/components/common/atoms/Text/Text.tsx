import React from "react";

interface DynamicElementProps {
    element: keyof JSX.IntrinsicElements;
    content: string;
    className?: string; // Optional class name prop
}

const Text: React.FC<any> = ({
    text,
    styles,
    type = 'p',
    isTitle
}) => {

    // Reusable component that receives an element and content as props
    const DynamicElement: React.FC<DynamicElementProps> = ({ element, content, className }) => {
        // Use the received element as a JSX tag and render the content
        return React.createElement(element, { className }, content);
    };
    const titleClass = 'text-3xl md:text-5xl mb-6 font-primary-bold';
    styles = isTitle ? `${styles} ${titleClass}` : styles;

    return (
        <>
            <DynamicElement element={type} content={text} className={styles} />
        </>
    );
};

export default Text;
