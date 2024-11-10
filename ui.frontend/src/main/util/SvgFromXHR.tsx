const isValidSVG = (data: string): boolean => {
    // Check if the data starts with "<svg" to validate it as an SVG
    return data.trim().startsWith('<svg');
};

export const fetchSvgFromXHR = async (url: string) => {
    try {
        const response = await fetch(url);
        const svgText = await response.text();
        const success = response.ok && isValidSVG(svgText);
        return { success, svgText };
    } catch (error) {
        console.error('Error fetching SVG:', error);
        return { success: false, svgText: '' };
    }
};

export const fetchColorsFile = async () => {
    const domain = window.location.hostname == "localhost" ? 'https://qa-revamp.visitsaudi.com' : '';
    const url = `${domain}/content/dam/sauditourism/tag-icons/categories/category-icon-colors.json`;
    // const url = '/bin/api/v1/colors';
    const response: any = await fetch(url);
    const colorsMapping = await response.json();
    const success = response.ok;
    return { colorsMapping, success };

}

