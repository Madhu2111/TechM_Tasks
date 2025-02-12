const today = new Date();
let currentYear = today.getFullYear();
let christmas = new Date(currentYear, 11, 25);

if (today > christmas) {
  currentYear++;
  christmas = new Date(currentYear, 11, 25);
}

const daysLeft = Math.ceil((christmas - today) / (1000 * 3600 * 24));
console.log(`Days left before Christmas: ${daysLeft} days.`);