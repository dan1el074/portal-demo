import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonCloseDirective, ButtonDirective, OffcanvasBodyComponent, OffcanvasComponent, OffcanvasHeaderComponent, OffcanvasTitleDirective } from '@coreui/angular';
import { TrelloIntegrationRecord } from '../../../app/interface/trello-integration.interface';
import { BackNavigationService } from '../../../app/services/back-navigation.service';

@Component({
  selector: 'app-trello-integration-offcanvas',
  imports: [
    CommonModule,
    ButtonDirective,
    ButtonCloseDirective,
    OffcanvasComponent,
    OffcanvasHeaderComponent,
    OffcanvasBodyComponent,
    OffcanvasTitleDirective,
  ],
  templateUrl: './trello-integration-offcanvas.component.html',
  styleUrl: './trello-integration-offcanvas.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrelloIntegrationOffcanvasComponent {
  @Input() resending = false;
  @Output() resend = new EventEmitter<TrelloIntegrationRecord>();

  protected visible = false;
  protected record: TrelloIntegrationRecord | null = null;
  private historyActive = false;

  constructor(
    private backNav: BackNavigationService,
    private cdr: ChangeDetectorRef,
  ) {}

  public open(record: TrelloIntegrationRecord): void {
    this.record = record;
    this.visible = true;

    if (!this.historyActive) {
      this.historyActive = true;
      this.backNav.register(() => {
        this.historyActive = false;
        this.visible = false;
        this.cdr.detectChanges();
      });
    }

    this.cdr.detectChanges();
  }

  public update(record: TrelloIntegrationRecord): void {
    this.record = record;
    this.cdr.detectChanges();
  }

  protected close(): void {
    this.visible = false;
    if (this.historyActive) {
      this.historyActive = false;
      this.backNav.unregister();
    }
    this.cdr.detectChanges();
  }

  protected onVisibleChange(visible: boolean): void {
    if (visible === this.visible) return;
    if (!visible) {
      this.close();
      return;
    }
    this.visible = true;
  }

  protected resendEmail(): void {
    if (this.record) this.resend.emit(this.record);
  }
}
