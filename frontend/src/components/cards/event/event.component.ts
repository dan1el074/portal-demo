import { CommonModule, registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { Component, EventEmitter, Input, LOCALE_ID, Output, ChangeDetectionStrategy } from '@angular/core';
import { ButtonDirective, CardBodyComponent, CardComponent, CardImgDirective, CardTextDirective, CardTitleDirective, DropdownComponent, DropdownItemDirective, DropdownMenuDirective, DropdownToggleDirective } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { cilPencil, cilTrash } from '@coreui/icons';
import { EventCard } from '../../../app/interface/event.interface';
import { environment } from '../../../environments/environment';

registerLocaleData(localePt);

@Component({
  selector: 'app-event',
  imports: [CommonModule, CardComponent, CardImgDirective, CardBodyComponent, CardTitleDirective, CardTextDirective, ButtonDirective, DropdownComponent, DropdownToggleDirective, DropdownMenuDirective, DropdownItemDirective, IconDirective],
  providers: [{ provide: LOCALE_ID, useValue: 'pt-BR' }],
  templateUrl: './event.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './event.component.scss'
})
export class EventComponent {
  @Input() event!: EventCard;
  @Input() canEdit = false;
  @Input() compact = false;
  @Output() editTask = new EventEmitter<EventCard>();
  @Output() deleteTask = new EventEmitter<number>();
  protected apiUrl = environment.apiUrl;
  protected icons = { cilPencil, cilTrash };

  protected monthLabel(date: string): string {
    const month = new Intl.DateTimeFormat('pt-BR', {
      month: 'short',
      timeZone: 'America/Sao_Paulo'
    }).format(new Date(date)).replace('.', '');

    return month.charAt(0).toUpperCase() + month.slice(1);
  }
}
