import React from 'react';
import { noop } from 'lodash';

export type FormProps = React.HTMLProps<HTMLFormElement>;
export const Form: React.FC<FormProps> = ({ onSubmit = noop, children, ...props }) => {
  return (
    <form
      {...props}
      onSubmit={(event) => {
        event.preventDefault();
        onSubmit(event);
      }}
    >
      {children}
    </form>
  );
};
