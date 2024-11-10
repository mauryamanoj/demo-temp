import castArray from 'lodash/castArray';
import isString from 'lodash/isString';

// takes care of adding / removing event listeners from multiple elements at once
// automatically assigns an id to the element callback to simplify identification
type IdGenerator = (element: HTMLElement, id: number) => number;

interface EventCallbackMap {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [eventName: string]: (id: number, ...args: Array<any>) => void;
}

const defaultIdGenerator: IdGenerator = (_element, id) => id;

type EventManagerProps = {
  getId?: IdGenerator;
};

export class EventManager {
  private handlers:any = {};
  private readonly getId: IdGenerator;

  public constructor({ getId = defaultIdGenerator }: EventManagerProps = {}) {
    this.getId = getId;
  }

  // Example: addEventListener(elements, {click: (id)=> console.log(id), mouseenter: (id)=> console.log(id)})
  public addEventListeners(
    elements: HTMLElement | Array<HTMLElement>,
    eventCallbackMap: EventCallbackMap,
  ) {
    Object.keys(eventCallbackMap).forEach((eventName) => {
      this.handlers[eventName] = castArray(elements).map((element, i) => {
        const id = this.getId(element, i);
        const cbk = (...args:any) => eventCallbackMap[eventName](id, ...args);
        element.addEventListener(eventName, cbk);
        return { element, callback: cbk };
      });
    });

    return this;
  }

  public removeEventListeners(eventNameOrElements: string | HTMLElement | Array<HTMLElement>) {
    if (isString(eventNameOrElements)) {
      const eventName = eventNameOrElements;
      const eventHandlers = this.handlers[eventName];

      eventHandlers &&
        eventHandlers.forEach((handler:any) => {
          handler.element.removeEventListener(eventName, handler.callback);
        });
    } else {
      const elements = castArray(eventNameOrElements);
      Object.keys(this.handlers).forEach((eventName) => {
        const eventHandlers = this.handlers[eventName];
        eventHandlers.forEach((handler:any) => {
          if (elements.includes(handler.element)) {
            handler.element.removeEventListener(eventName, handler.callback);
          }
        });
      });
    }

    return this;
  }

  public dispose() {
    Object.keys(this.handlers).forEach((handler) => this.removeEventListeners(handler));
  }
}
