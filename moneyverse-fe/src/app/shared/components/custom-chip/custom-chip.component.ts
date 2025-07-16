import {Component, inject, input} from '@angular/core';
import {Chip} from "primeng/chip";
import {SvgComponent} from "../svg/svg.component";
import {ColorService} from '../../services/color.service';
import {Style} from '../../models/common.model';

@Component({
  selector: 'app-custom-chip',
  imports: [
    Chip,
    SvgComponent
  ],
  templateUrl: './custom-chip.component.html',
  styleUrl: './custom-chip.component.scss'
})
export class CustomChipComponent {

  style = input.required<Style>();
  message = input.required<string>();

  protected readonly colorService = inject(ColorService);
}
