/* eslint-disable max-len */
import React from 'react';


const Overlay: React.FC<any> = ({classNames, contentStyles}) => {
    return (
       <div className={classNames} style={{height:'460px'}}>
             <div className={"bg-gradient-to-t  from-[#000000] via-[#00000046] to-[#00000000] opacity-50 h-full "+contentStyles}></div>
       </div>
    );
};

export default Overlay;
