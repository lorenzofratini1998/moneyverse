import {Component, input} from '@angular/core';
import {PercentPipe} from '@angular/common';

@Component({
  selector: 'app-kpi',
  imports: [
    PercentPipe
  ],
  templateUrl: './kpi.component.html',
  styleUrl: './kpi.component.scss'
})
export class KpiComponent {
  label = input.required<string>();
  value = input.required<number | string>();
  variation = input<number>();
}
