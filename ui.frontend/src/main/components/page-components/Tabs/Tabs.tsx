import React from 'react';
import Tabs from '../../common/atoms/Tab/Tab';


interface TabProps {
  tabTitles: string[];
  tabContents: React.ReactNode[];
}

// To show example i added tabContentDemo and tabTitlesDemo
const tabContentDemo = [
  <div>Lorem ipsum dolor sit amet consectetur adipisicing elit. Animi, labore cumque fuga nam soluta reprehenderit? Dicta veritatis odio officiis fugit nobis necessitatibus dolores ipsum non, consectetur quibusdam doloremque nulla cum. 1</div>,
  <div>Lorem ipsum dolor sit amet consectetur adipisicing elit. Animi, labore cumque fuga nam soluta reprehenderit? Dicta veritatis odio officiis fugit nobis necessitatibus dolores ipsum non, consectetur quibusdam doloremque nulla cum. 2</div>,
  <div>Lorem ipsum dolor sit amet consectetur adipisicing elit. Animi, labore cumque fuga nam soluta reprehenderit? Dicta veritatis odio officiis fugit nobis necessitatibus dolores ipsum non, consectetur quibusdam doloremque nulla cum. 3</div>,
  <div>Lorem ipsum dolor sit amet consectetur adipisicing elit. Animi, labore cumque fuga nam soluta reprehenderit? Dicta veritatis odio officiis fugit nobis necessitatibus dolores ipsum non, consectetur quibusdam doloremque nulla cum. 4</div>,
];
const tabTitlesDemo = ['Tab 1', 'Tab 2', 'Tab 3', 'Tab 4'];


const Tab: React.FC<TabProps> = ({ tabTitles = tabTitlesDemo, tabContents = tabContentDemo }) => {
  return (
    <div className="bg-[#2c2c2c] pb-10 text-sm font-medium text-center bg-dark text-white font-primary-semibold">
      <Tabs tabTitles={tabTitles} tabContents={tabContents} />
    </div>
  );
};



export default Tab;