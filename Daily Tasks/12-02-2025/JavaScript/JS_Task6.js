function leapyear(year) {
    return (year % 100 === 0) ? (year % 400 === 0) : (year % 4 === 0);
}

console.log(`Is 2024 a leap year? ${leapyear(2024)}`);
console.log(`Is 2012 a leap year? ${leapyear(2012)}`);