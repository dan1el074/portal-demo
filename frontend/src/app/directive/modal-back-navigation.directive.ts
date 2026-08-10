import { Directive, effect, OnDestroy, untracked } from '@angular/core';
import { ModalComponent } from '@coreui/angular';
import { BackNavigationService } from '../services/back-navigation.service';

@Directive({
  selector: '[modalBackNavigation]',
  standalone: true,
})
export class ModalBackNavigationDirective implements OnDestroy {
  private historyRegistered = false;

  constructor(
    private modal: ModalComponent,
    private backNavigation: BackNavigationService
  ) {
    effect(() => {
      const visible = this.modal.visible();

      untracked(() => {
        if (visible) {
          this.register();
        } else {
          this.unregister();
        }
      });
    });
  }

  public ngOnDestroy(): void {
    this.unregister();
  }

  private register(): void {
    if (this.historyRegistered) return;

    this.historyRegistered = true;
    this.backNavigation.register(() => {
      this.historyRegistered = false;
      this.modal.visible.set(false);
    });
  }

  private unregister(): void {
    if (!this.historyRegistered) return;

    this.historyRegistered = false;
    this.backNavigation.unregister();
  }
}
