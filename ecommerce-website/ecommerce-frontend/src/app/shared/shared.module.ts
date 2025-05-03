import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InrCurrencyPipe } from '../pipes/inr-currency.pipe';

@NgModule({
  declarations: [
    InrCurrencyPipe
  ],
  imports: [
    CommonModule
  ],
  exports: [
    InrCurrencyPipe
  ]
})
export class SharedModule { }