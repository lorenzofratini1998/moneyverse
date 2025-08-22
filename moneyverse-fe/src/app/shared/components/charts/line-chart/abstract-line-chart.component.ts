import {Directive, Signal} from '@angular/core';
import {AbstractChartComponent} from '../abstract-chart.component';
import {LineChartOptions} from "../../../models/chart.model";

@Directive()
export abstract class AbstractLineChartComponent<T> extends AbstractChartComponent<T> {
  abstract options: Signal<LineChartOptions>;
}
