start = 2014;
end = 2050;

for (let year = start; year <= end; year++) {
  const date = new Date(year, 0, 1);
  const dayOfWeek = date.getDay();

  if (dayOfWeek === 0) {
    console.log(`January 1st, ${year} is a Sunday.`);
  }
}