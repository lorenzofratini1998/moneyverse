import {Component, computed, inject, viewChild} from '@angular/core';
import {TagFormDialogComponent} from './components/tag-form-dialog/tag-form-dialog.component';
import {TagTableComponent} from './components/tag-table/tag-table.component';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {AuthService} from '../../../../core/auth/auth.service';
import {TagStore} from './services/tag.store';
import {Tag} from '../../transaction.model';
import {TagFormData} from "./models/form.model";
import {ManagementComponent, ManagementConfig} from '../../../../shared/components/management/management.component';
import {TranslationService} from '../../../../shared/services/translation.service';

@Component({
  selector: 'app-tag-management',
  imports: [
    TagFormDialogComponent,
    TagTableComponent,
    ManagementComponent
  ],
  templateUrl: './tag-management.component.html'
})
export class TagManagementComponent {

  protected readonly authService = inject(AuthService);
  protected readonly tagStore = inject(TagStore);
  private readonly translateService = inject(TranslationService);

  tagFormDialog = viewChild.required(TagFormDialogComponent);

  managementConfig = computed<ManagementConfig>(() => {
    this.translateService.lang();
    return {
      title: this.translateService.translate('app.manageTags'),
      actions: [
        {
          icon: IconsEnum.REFRESH,
          variant: 'text',
          severity: 'secondary',
          action: () => this.tagStore.loadTags(true)
        },
        {
          icon: IconsEnum.PLUS,
          label: this.translateService.translate('app.actions.newTag'),
          action: () => this.tagFormDialog().open()
        }
      ]
    }
  })

  submit(formData: TagFormData) {
    const tagId = formData.tagId;
    if (tagId) {
      this.tagStore.updateTag({
        tagId,
        request: {...formData}
      });
    } else {
      this.tagStore.createTag({
        ...formData,
        userId: this.authService.user().userId
      });
    }
  }

  deleteTag(tag: Tag) {
    this.tagStore.deleteTag(tag.tagId);
  }
}
