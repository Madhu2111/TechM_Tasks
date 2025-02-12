var today = new Date();
var dd = today.getDate()
var mm = today.getMonth() + 1
var yyyy = today.getFullYear();

if (dd < 10) {
    dd = '0' + dd;
} 
if (mm < 10) {
    mm = '0' + mm;
} 

console.log(`mm-dd-yyyy: ${mm}-${dd}-${yyyy}, mm/dd/yyyy: ${mm}/${dd}/${yyyy}`);
console.log('\t \t (or)');
console.log(`dd-mm-yyyy: ${dd}-${mm}-${yyyy}, dd/mm/yyyy: ${dd}/${mm}/${yyyy}`);