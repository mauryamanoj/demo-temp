/*
 * Be very careful when changing this file. Make sure the project compiles.
 * Restart the server, make changes to any file and verify everything is still ok
 * */

// declaration.d.ts
declare module '*.json';
declare module '*.yml';
declare module '*.scss';
declare module '*.png';
declare module '*.jpg';
declare module '*.jpeg';
declare module '*.svg';

declare module '*.module.scss' {
    const content: { [className: string]: string };
    export = content;
}

declare module 'gsap/ModifiersPlugin' {
    export default class ModifiersPlugin {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        public static create: any;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        [key: string]: any;
    }
}

declare module 'gsap/Draggable' {
    export default class Draggable {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        public static create: any;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        [key: string]: any;
    }
}

declare module 'gsap/ScrollToPlugin' {
    export default class ScrollToPlugin { }
}

interface DraggableBounds {
    minX: number;
    maxX: number;
}

declare module 'framer-motion' {
    export * from 'framer-motion/dist/index';
}

declare module '@adobe/target-nodejs-sdk';

