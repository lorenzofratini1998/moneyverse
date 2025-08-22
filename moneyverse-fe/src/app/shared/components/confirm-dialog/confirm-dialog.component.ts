import {Component, computed, inject, signal} from '@angular/core';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {Confirmation, ConfirmationService} from 'primeng/api';
import {Button} from 'primeng/button';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-confirm-dialog',
  imports: [
    ConfirmDialog,
    Button
  ],
  templateUrl: './confirm-dialog.component.html'
})
export class ConfirmDialogComponent {
  private readonly confirmationService = inject(ConfirmationService);

  private _isVisible = signal(false);
  private _config = signal<Confirmation | null>(null);

  isVisible = this._isVisible.asReadonly();
  config = this._config.asReadonly();

  title = computed(() => this.config()?.header || '');
  message = computed(() => this.config()?.message || '');
  acceptLabel = computed(() => this.config()?.acceptLabel || 'Delete');

  acceptSeverity = computed(() =>
    this.acceptLabel() === 'Delete' ? 'danger' as const : 'primary' as const
  );

  constructor() {
    this.confirmationService.requireConfirmation$
      .pipe(takeUntilDestroyed())
      .subscribe((confirmation: Confirmation) => {
        this._config.set(confirmation);
        this._isVisible.set(true);
      });
  }

  onAcceptClick() {
    const currentConfig = this.config();
    if (currentConfig?.accept) {
      currentConfig.accept();
    }
    this.hide();
  }

  onCancel() {
    const currentConfig = this.config();
    if (currentConfig?.reject) {
      currentConfig.reject();
    }
    this.hide();
  }

  onHide() {
    this.hide();
  }

  private hide() {
    this._isVisible.set(false);
    this._config.set(null);
  }
}

