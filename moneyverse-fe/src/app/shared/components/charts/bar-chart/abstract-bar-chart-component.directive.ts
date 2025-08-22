import {AbstractChartComponent} from '../abstract-chart.component';
import {Directive, Signal} from '@angular/core';

import {BarChartOptions} from "../../../models/chart.model";

@Directive()
export abstract class AbstractBarChartComponent<T> extends AbstractChartComponent<T> {
  abstract options: Signal<BarChartOptions>
}
