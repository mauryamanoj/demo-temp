import React from "react";

interface PillProps {
  className?: string;
  uppercase?: boolean;
}
const Pill: React.FC<PillProps> = ({ className, uppercase = false, children }) => {

  return (
    <div className={`${uppercase ? 'uppercase' : ''} z-10 text-xs px-2 py-1
    rounded-lg bg-black/50 text-white ${className}`}>
      {children}
    </div>
  );
};

export default Pill;
