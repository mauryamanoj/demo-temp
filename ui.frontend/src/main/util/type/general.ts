import { DeepReadonly } from 'ts-essentials';
// import { Inputmode } from 'src/main/components/common/form/SimpleFormInput/SimpleFormInput';
// import { DependentOption } from 'src/main/components/common/form/FormCreator/FormCreator';

export type HorizontalDirection = 'left' | 'right';
export type VerticalDirection = 'top' | 'bottom';

// export type InputField = DeepReadonly<{
//   name: string;
//   label: string;
//   placeholder: string;
//   type: string;
//   lang?: string;
//   inputName: string;
//   inputmode?: Inputmode;
// }>;

// export type SelectField = DeepReadonly<{
//   name: string;
//   label: string;
//   placeholder: string;
//   inputName?: string;
//   options: Array<DependentOption>;
//   dependsOn?: string;
// }>;

// this is the data for the image on heroData
export type DataImage = DeepReadonly<{
  mobileImage: string;
  desktopImage: string;
  alt: string;
}>;
