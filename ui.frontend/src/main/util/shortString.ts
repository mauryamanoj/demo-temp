export const shortString = (
    inputString: string,
    characterLimit: number
): string => {
    if (inputString.length > characterLimit) {
        return inputString.slice(0, characterLimit) + "...";
    }
    return inputString;
};

