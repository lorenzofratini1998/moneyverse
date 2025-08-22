import {Directive, Signal} from '@angular/core';
import {AbstractChartComponent} from '../abstract-chart.component';
import {PieChartOptions} from "../../../models/chart.model";

@Directive()
export abstract class AbstractPieChartComponent<T> extends AbstractChartComponent<T> {
  abstract pieChartOptions: Signal<PieChartOptions>;
}
