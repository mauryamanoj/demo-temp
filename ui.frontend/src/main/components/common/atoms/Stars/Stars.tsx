import React, { useCallback } from "react";
import Icon from 'src/main/components/common/atoms/Icon/Icon';

interface StarsProps {
  count: 1 | 2 | 3 | 4 | 5;
  className?:string;
}
const Stars = ({ count = 1,className = "" }: StarsProps) => {

  const Loop = useCallback((count) => {
    const HTML = [];

    for(let i = 1; i<= count; i++){
      HTML.push(<Icon name="star" svgClass="text-theme-100" />);
    }
    return HTML;
  }, [count]);

  return (
    <div className={`flex gap-1 text-theme-100 ${className}`}>
      {Loop(count)}
    </div>
  );
};

export default Stars;
