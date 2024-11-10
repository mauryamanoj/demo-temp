import React, { ChangeEvent } from 'react';

interface TextareaProps {
  id: string;
  htmlFor: string;
  placeholder: string;
  label: string;
  value: any;
  labelStyle: string;
  textareaStyle: string;
  onChange: (value: string) => void;
  onBlur:any
}

const Textarea: React.FC<TextareaProps> = ({ id, htmlFor, placeholder, label,
   value, onChange, labelStyle, textareaStyle, onBlur }) => {
  const handleTextareaChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    const newValue = e.target.value;
      onChange(newValue);
  
  };

  return (
    <div className="textarea-wrapper">
      <label   className={labelStyle} htmlFor={htmlFor}>{label}</label>
      <textarea
        className={textareaStyle}
        id={id}
        placeholder={placeholder}
        value={value}
        onChange={handleTextareaChange}
        rows={4}
        cols={40}
        onBlur={onBlur}
      />
    </div>
  );
};

export default Textarea;
