import {Component, inject, output, ViewChild} from '@angular/core';
import {IconsEnum} from '../../../../../../../shared/models/icons.model';
import {ColorService} from '../../../../../../../shared/services/color.service';
import {ConfirmationService, MessageService} from 'primeng/api';
import {TransactionStore} from '../../../../../transaction.store';
import {TagFormDialogComponent} from '../tag-form-dialog/tag-form-dialog.component';
import {Tag, TagForm, TagFormData} from '../../../../../transaction.model';
import {TableModule} from 'primeng/table';
import {Chip} from 'primeng/chip';
import {SvgComponent} from '../../../../../../../shared/components/svg/svg.component';
import {ButtonDirective} from 'primeng/button';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {Toast} from 'primeng/toast';

@Component({
  selector: 'app-tag-table',
  imports: [
    TableModule,
    Chip,
    SvgComponent,
    ButtonDirective,
    TagFormDialogComponent,
    ConfirmDialog,
    Toast
  ],
  templateUrl: './tag-table.component.html',
  styleUrl: './tag-table.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class TagTableComponent {
  protected readonly IconsEnum = IconsEnum;
  protected readonly colorService = inject(ColorService);
  protected readonly transactionStore = inject(TransactionStore);
  private readonly confirmationService = inject(ConfirmationService);

  deleted = output<Tag>();
  edited = output<TagForm>();

  @ViewChild(TagFormDialogComponent) tagForm!: TagFormDialogComponent;

  onDelete(event: Event, tag: Tag) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Are you sure that you want to proceed?',
      header: 'Delete tag',
      rejectLabel: 'Cancel',
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true,
        rounded: true
      },
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger',
        rounded: true
      },
      accept: () => {
        this.deleted.emit(tag);
      },
      reject: () => {
      }
    });
  }

  onEdit(formData: TagFormData) {
    this.edited.emit({
      tagId: this.tagForm.tagToEdit()?.tagId,
      formData: formData
    });
  }

  protected readonly Icons = IconsEnum;
}
