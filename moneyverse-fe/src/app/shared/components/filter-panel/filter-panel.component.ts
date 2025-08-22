import {Component, input, output} from '@angular/core';
import {Fieldset} from "primeng/fieldset";
import {OverlayBadge} from 'primeng/overlaybadge';
import {SvgComponent} from '../svg/svg.component';
import {IconsEnum} from '../../models/icons.model';
import {Button} from 'primeng/button';
import {AbstractFormComponent} from '../forms/abstract-form.component';

@Component({
  selector: 'app-filter-panel',
  imports: [
    Fieldset,
    OverlayBadge,
    SvgComponent,
    Button
  ],
  templateUrl: './filter-panel.component.html'
})
export class FilterPanelComponent<T, D> {
  form = input.required<AbstractFormComponent<T, D>>();
  activeFiltersCount = input<number>(0);

  onReset = output<void>();

  protected readonly icons = IconsEnum;
}
