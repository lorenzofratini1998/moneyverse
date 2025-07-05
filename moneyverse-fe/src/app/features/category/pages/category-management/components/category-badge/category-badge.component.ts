import {Component, input} from '@angular/core';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {NgStyle} from '@angular/common';

@Component({
  selector: 'app-category-badge',
  imports: [
    SvgComponent,
    NgStyle
  ],
  templateUrl: './category-badge.component.html'
})
export class CategoryBadgeComponent {
  backgroundColor = input.required<string>();
  textColor = input.required<string>();
  icon = input.required<string>();
  text = input.required<string>();
}
