import {Component, inject, ViewChild} from '@angular/core';
import {Button} from 'primeng/button';
import {SvgComponent} from '../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../shared/models/icons.model';
import {ConfirmationService, MessageService} from 'primeng/api';
import {TagFormDialogComponent} from './components/tag-form-dialog/tag-form-dialog.component';
import {Tag, TagForm, TagFormData, TagRequest} from '../../../transaction.model';
import {AuthService} from '../../../../../core/auth/auth.service';
import {TransactionService} from '../../../transaction.service';
import {switchMap, take} from 'rxjs';
import {Toast} from 'primeng/toast';
import {TagTableComponent} from './components/tag-table/tag-table.component';
import {TransactionStore} from '../../../transaction.store';

@Component({
  selector: 'app-tag-management',
  imports: [
    Button,
    SvgComponent,
    TagFormDialogComponent,
    Toast,
    TagTableComponent
  ],
  templateUrl: './tag-management.component.html',
  styleUrl: './tag-management.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class TagManagementComponent {

  protected readonly IconsEnum = IconsEnum;
  private readonly messageService = inject(MessageService);
  protected readonly authService = inject(AuthService);
  protected readonly transactionService = inject(TransactionService);
  protected readonly transactionStore = inject(TransactionStore);

  @ViewChild(TagFormDialogComponent) tagForm!: TagFormDialogComponent;

  createTag(formData: TagFormData) {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => this.transactionService.createTag(this.createTagRequest(userId, formData)))
    ).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Tag created successfully.'
        });
        this.transactionStore.refreshTags();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the tag creation.'
        });
      }
    })
  }

  private createTagRequest(userId: string, formData: TagFormData): TagRequest {
    return {
      userId: userId,
      tagName: formData.tagName,
      description: formData.description,
      style: {
        color: formData.style.color,
        icon: formData.style.icon
      }
    }
  }

  onEdit(tagForm: TagForm) {
    this.transactionService.updateTag(tagForm.tagId!, this.createTagUpdateRequest(tagForm.formData)).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Tag updated successfully.'
        });
        this.transactionStore.refreshTags();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the tag update.'
        });
      }
    })
  }

  private createTagUpdateRequest(formData: TagFormData) {
    const request: Partial<TagRequest> = {};
    request.tagName = formData.tagName;
    request.description = formData.description;
    request.style = {
      color: formData.style.color,
      icon: formData.style.icon
    };
    return request;
  }

  onDelete(tag: Tag) {
    this.transactionService.deleteTag(tag.tagId!).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Tag deleted successfully.'
        });
        this.transactionStore.refreshTags();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the tag deletion.'
        });
      }
    })
  }
}
