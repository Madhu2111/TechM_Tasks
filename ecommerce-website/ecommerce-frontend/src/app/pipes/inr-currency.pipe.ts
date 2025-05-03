import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'inrCurrency'
})
export class InrCurrencyPipe implements PipeTransform {
  // Convert USD to INR (approximate exchange rate)
  private exchangeRate = 83;

  transform(value: number, showDiscount: boolean = false): string {
    if (showDiscount) {
      // Show original price with 20% markup for discount display
      return '₹' + Math.round(value * this.exchangeRate * 1.2).toLocaleString('en-IN');
    }
    return '₹' + Math.round(value * this.exchangeRate).toLocaleString('en-IN');
  }
}