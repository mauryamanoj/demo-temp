function isEmpty(value: any) {
    if ((typeof value === 'string' && value !== null && value.trim() === '') || value === null) {
        return true;
    }
    return false;
}

export const ValidateEmail = (email: string): boolean => {
    var emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
    return email.match(emailRegex) !== null;
};

export function isValidEmail(email:any, isRequired:any) {
    if (isRequired && isEmpty(email.trim())) {
        return {
            isValid: false,
            errorKey: 'emailaddressempty',
        };
    }

    const re =
        // eslint-disable-next-line max-len
        /(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/;
    const result = re.exec(email);

    let validLength = true;

    const tokens = email.split('@');

    if (tokens.length === 2) {
        const emailPrefixPart = tokens[0];
        const emailSuffixPart = tokens[1];

        const prefixRegex = /^[0-9A-Za-z_.-]+$/;

        validLength = emailPrefixPart.length <= 64 && emailSuffixPart.length <= 255;
        if (prefixRegex.exec(emailPrefixPart) === null) {
            validLength = false;
        }
    } else {validLength = false;}

    if (result !== null && validLength) {
        return {
            isValid: true,
        };
    }

    return {
        isValid: false,
        errorKey: 'emailaddressinvalid',
    };
}

export function isValidFirstName(name: any, isRequired: any) {
    if (isRequired && isEmpty(name)) {
        return {
            isValid: false,
            errorKey: 'firstnameempty',
        };
    }

    if ((!/^[\u0621-\u064A A-Za-z]+$/.test(name))) {
        return {
            isValid: false,
            errorKey: 'firstnameinvalid',
        };
    }

    return {
        isValid: true,
    };
}

export function isValidLastName(name: any, isRequired: any) {
    if (isRequired && isEmpty(name)) {
        return {
            isValid: false,
            errorKey: 'lastnameempty',
        };
    }

    if ((!/^[\u0621-\u064A A-Za-z]+$/.test(name))) {
        return {
            isValid: false,
            errorKey: 'lastnameinvalid',
        };
    }

    return {
        isValid: true,
    };
}

export function isValidPhoneNumber(phone: any, isRequired: any) {
    if (isRequired && isEmpty(phone)) {
        return {
            isValid: false,
            errorKey: 'phonenumberempty',
        };
    }

    if (phone.length < 7) {
        return {
            isValid: false,
            errorKey: 'phonenumberinvalid', // "Error.Phone.InCorrectLength10Digit"
        };
    }

    return {
        isValid: true,
    };
}

export function isValidMessage(message: any, isRequired: any) {
    if (isRequired && isEmpty(message)) {
        return {
            isValid: false,
            errorKey: 'messageempty',
        };
    }

    return {
        isValid: true,
    };
}
