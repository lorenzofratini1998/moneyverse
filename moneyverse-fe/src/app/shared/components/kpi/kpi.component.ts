import {Component, input} from '@angular/core';

@Component({
  selector: 'app-kpi',
  imports: [],
  templateUrl: './kpi.component.html',
  styleUrl: './kpi.component.scss'
})
export class KpiComponent {
  title = input.required<string>();
  currentValue = input.required<number | string>();
  previousValue = input<number | string>();
}
