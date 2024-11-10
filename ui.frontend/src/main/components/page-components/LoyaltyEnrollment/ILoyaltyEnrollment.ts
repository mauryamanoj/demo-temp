import { ILoginModalProps } from "../../common/atoms/LoginModal/LoginModal";


export interface LoyaltyEnrollmentProps {
  loginModal: ILoginModalProps;
  queryParameter: {
    name: string;
    value: string;
  };
  enrollmentModal: {
    alreadyEnrolledMessage: string;
    successfullyEnrolledMessage: string;
    unsuccessfullyEnrolledMessage: string;
    copy: string;
    apiEndpoint: string;
  };
}
