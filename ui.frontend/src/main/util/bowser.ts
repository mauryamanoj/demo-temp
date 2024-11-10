import bowser from 'bowser';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export default (bowser.getParser(window.navigator.userAgent) as any).parsedResult;

export const isIPad = () => /Macintosh/.test(navigator.userAgent) && 'ontouchend' in document;

export const isAndroid = () => /(android)/i.test(navigator.userAgent);

export const isMobileIOS = () => document.documentElement.getAttribute('data-os') === 'iOS';
