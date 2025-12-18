import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { CardBodyComponent, CardComponent, CardImgDirective, CardTextDirective, CardTitleDirective } from '@coreui/angular';
import { EventCard } from '../../../app/interface/event.interface';

@Component({
  selector: 'app-event',
  imports: [CommonModule, CardComponent, CardImgDirective, CardBodyComponent, CardTitleDirective, CardTextDirective],
  templateUrl: './event.component.html',
  styleUrl: './event.component.scss',
})
export class EventComponent {
  @Input() event!: EventCard;

}
