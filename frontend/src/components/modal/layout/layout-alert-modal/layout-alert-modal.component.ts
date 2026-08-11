import { Component, EventEmitter, Input, Output, ChangeDetectionStrategy } from '@angular/core';
import { ButtonDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent } from '@coreui/angular';
import { PendingIssues } from '../../../../app/interface/user.interface';
import { ModalBackNavigationDirective } from '@app/directive/modal-back-navigation.directive';

@Component({
  selector: 'app-layout-alert-modal',
  imports: [
    ModalComponent,
    ModalBackNavigationDirective,
    ModalHeaderComponent,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonDirective
  ],
  templateUrl: './layout-alert-modal.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './layout-alert-modal.component.scss',
})
export class LayoutAlertModalComponent {
  @Input() visible!: boolean;
  @Input() pendingIssues!: Array<PendingIssues>;
  @Output() closeModal = new EventEmitter<any>();

  protected closeAlertModal(): void {
    this.closeModal.emit();
  }
}
