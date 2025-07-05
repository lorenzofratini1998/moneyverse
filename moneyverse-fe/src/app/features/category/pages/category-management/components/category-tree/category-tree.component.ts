import {Component, input} from '@angular/core';
import {Category} from '../../../../category.model';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {CategoryBadgeComponent} from '../category-badge/category-badge.component';

@Component({
  selector: 'app-category-tree',
  imports: [
    SvgComponent,
    CategoryBadgeComponent
  ],
  templateUrl: './category-tree.component.html',
  styleUrl: './category-tree.component.scss'
})
export class CategoryTreeComponent {
  protected readonly IconsEnum = IconsEnum;
  category = input.required<Category>();
  level = input<number>(0);
}
