import {Component, computed, inject, output, viewChild} from '@angular/core';
import {LucideAngularModule} from 'lucide-angular';
import {Button} from 'primeng/button';
import {DialogComponent} from '../../../../../../shared/components/dialogs/dialog/dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {CategoryDefaultGridComponent} from '../category-default-grid/category-default-grid.component';
import {CategoryStore} from '../../../../services/category.store';
import {TranslationService} from '../../../../../../shared/services/translation.service';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-category-default-dialog',
  imports: [
    LucideAngularModule,
    Button,
    DialogComponent,
    CategoryDefaultGridComponent,
    TranslatePipe
  ],
  templateUrl: './category-default-dialog.component.html'
})
export class CategoryDefaultDialogComponent {
  onSubmit = output<any>();

  protected dialog = viewChild.required<DialogComponent>(DialogComponent);
  protected categoryStore = inject(CategoryStore);
  private readonly translateService = inject(TranslationService);

  config = computed<DynamicDialogConfig>(() => {
    this.translateService.lang();
    return {
      header: this.translateService.translate('app.dialog.category.defaultCategory')
    }
  });

  open(): void {
    this.dialog().open();
  }

  confirm(): void {
    this.onSubmit.emit({});
    this.dialog().close();
  }
}
