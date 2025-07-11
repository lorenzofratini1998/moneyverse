import {Component, inject} from '@angular/core';
import {Fieldset} from "primeng/fieldset";
import {SvgComponent} from "../../../../../../shared/components/svg/svg.component";
import {Tag} from "primeng/tag";
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {AccountStore} from '../../../../account.store';

@Component({
  selector: 'app-account-filter-panel',
  imports: [
    Fieldset,
    SvgComponent,
    Tag
  ],
  templateUrl: './account-filter-panel.component.html',
})
export class AccountFilterPanelComponent {

  protected readonly Icons = IconsEnum;
  protected readonly accountStore = inject(AccountStore);
}
