const today = new Date();
var day = today.getDay();
var days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

console.log("Today is: " + days[day] + ".");

var hour = today.getHours();
var minute = today.getMinutes();
var second = today.getSeconds();

const ampm = (hour >= 12) ? "PM" : "AM";
hour = hour % 12 || 12;

console.log(`Current time is : ${hour} ${ampm} : ${minute} : ${second}`);