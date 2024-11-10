/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx,html}",
  ],
  theme: {
    extend: {
      screens: {
        'xs': '520px',
        // => @media (min-width: 520px) { ... }
      },
      colors: {
        'gray': 'rgb(var(--color-gray) / <alpha-value>)',
        'gray-100': 'rgb(var(--color-gray-100) / <alpha-value>)',
        'gray-25': 'rgb(var(--color-gray-25) / <alpha-value>)',
        'gray-50': 'rgb(var(--color-gray-50) / <alpha-value>)',
        'gray-200': 'rgb(var(--color-gray-200) / <alpha-value>)',
        'gray-300': 'rgb(var(--color-gray-300) / <alpha-value>)',
        'gray-350': 'rgb(var(--color-gray-350) / <alpha-value>)',
        'success' : 'rgb(var(--color-success) / <alpha-value>)',
        'danger' : 'rgb(var(--color-danger) / <alpha-value>)',
        'warning' : 'rgb(var(--color-warning) / <alpha-value>)',
        'info' : 'rgb(var(--color-info) / <alpha-value>)',
        'disabled' : 'rgb(var(--color-disabled) / <alpha-value>)',
        "theme": {
          100: 'rgb(var(--color-theme-100) / <alpha-value>)',
          200: 'rgb(var(--color-theme-200) / <alpha-value>)',
        }
      },
      spacing: {
        '13': '50px',
        '100': '100px',
        '130': '130px',
        '30': '120px'
      },
      fontFamily:{
        'primary-bold': 'var(--font-primary-bold)',
        'primary-regular': 'var(--font-primary-regular)',
        'primary-semibold': 'var(--font-primary-semibold)',

        'secondary-bold': 'var(--font-secondary-bold)',
        'secondary-regular': 'var(--font-secondary-regular)',
        'secondary-semibold': 'var(--font-secondary-semibold)',

        'tertiary': 'var(--font-tertiary)',
        'tertiary-bold': 'var(--font-tertiary-bold)',
        'tertiary-regular': 'var(--font-tertiary-regular)',
        'tertiary-semibold': 'var(--font-tertiary-semibold)',


      },
      fontSize: {
        'xxs': '10px',
        'xs': '12px',
        'sm': '14px',
        'base': '16px',
        'lg': '18px',
        'xl': '20px',
        '1.5xl': '22px',
        '2xl': '24px',
        '3xl': '30px',
        '3.5xl': '32px',
        '4xl': '36px',
        '4.3xl': '40px',
        '4.5xl': '46px',
        '5xl': '48px',
        '5.5xl': '56px',
        '6xl': '60px',
        '7xl': '70px',
        '8xl': '80px',
        '8.5xl': '86px',
      },
    },
  },
  plugins: [
    function ({ addVariant }) {
      addVariant('child', '& > *');
    },
    require('tailwind-scrollbar')

  ],
  variants: {
    scrollbar: ['rounded']
}
};

