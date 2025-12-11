import {Component, input} from '@angular/core';
import {Chip} from "primeng/chip";
import {SvgComponent} from "../svg/svg.component";

@Component({
  selector: 'app-chip',
  imports: [
    Chip,
    SvgComponent
  ],
  templateUrl: './chip.component.html'
})
export class ChipComponent {

  color = input.required<string>();
  icon = input.required<string>();
  message = input.required<string>();

}
