// import { DeepReadonly } from 'ts-essentials';
// import { DataImage, InputField } from './general';
// import { Option } from 'src/main/components/common/NativeSelect/NativeSelect';
// import { GenericInput } from 'src/main/components/common/form/FormCreator/FormCreator';

// export type UserCMSData = DeepReadonly<{
//   heroData: {
//     image: DataImage;
//   };
//   userApi: UserApi;
//   userBox: UserBox;
//   preferenceBox: PreferenceBox;
// }>;

// export type UserApi = DeepReadonly<{
//   path: string;
//   getTripPlansEndpoint: string;
//   getProfile: string;
//   updateProfile: string;
//   domain: string;
//   clientId: string;
//   ssidLoginUrl: string;
//   ssidLogoutUrl: string;
// }>;

// export type UserBox = DeepReadonly<{
//   yourFavorites: string;
//   favoritesPath: string;
//   yourTrips: string;
//   tripsPath: string;
//   changePassword?: string;
//   changePasswordUrl?: string;
//   profilePreference: string;
//   logOut: string;
//   logOutUrl: string;
//   avatar: {
//     desktopImage: string;
//     mobileImage: string;
//     alt: string;
//   };
// }>;

// export type PreferenceBox = DeepReadonly<{
//   sectionTitle: string;
//   profileDetails: string;
//   resetButtonText: string;
//   saveButtonText: string;
//   promptText?: string;
//   toastMessages: Array<ToastMessage>;
//   inputs: {
//     firstName: GenericInput;
//     lastName: GenericInput;
//     genderCode: InputFieldWithOptions;
//     ageRange: InputFieldWithOptions;
//     travelPartner: InputFieldWithOptionsWithImages;
//     interests: InputFieldWithOptions;
//   };
// }>;

// export type InputFieldWithOptions = DeepReadonly<{
//   options: Array<Option>;
// }> &
//   InputField;

// export type InputFieldWithOptionsWithImages = DeepReadonly<{
//   options: Array<OptionsWithImages>;
// }> &
//   InputField;

// export type OptionsWithImages = DeepReadonly<
//   Option & {
//     image: DataImage;
//   }
// >;

// export type ToastMessage = DeepReadonly<{
//   type: ToastMessageType;
//   copy: string;
// }>;

// export type ToastMessageType = DeepReadonly<'error' | 'success' | 'unsaved'>;

// export type User = DeepReadonly<{
//   firstName: string;
//   lastName: string;
//   languageCode?: string;
//   birthDate?: string;
//   genderCode: string;
//   ageRange: string;
//   email: string;
//   visaNumber?: string;
//   travelPartner: string;
//   interests: Array<string>;
//   tripCount: number;
//   favoritesCount: number;
//   mobileNumber?: string | null;
//   halaYallaUserId?: string | null;
//   isMobileVerified?: boolean | null;
// }>;
