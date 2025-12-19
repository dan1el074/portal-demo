import { CardBodyComponent, CardComponent, CardHeaderComponent, CardTextDirective, CardTitleDirective, TabDirective, TabPanelComponent, TabsComponent, TabsContentComponent, TabsListComponent } from '@coreui/angular';
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TodoTableCard } from '../../../app/interface/todo.interface';

@Component({
  selector: 'app-todo',
  imports: [
    CommonModule,
    CardComponent,
    CardHeaderComponent,
    CardBodyComponent,
    CardTitleDirective,
    CardTextDirective,
    TabsListComponent,
    TabDirective,
    TabsContentComponent,
    TabPanelComponent,
    TabsComponent
  ],
  templateUrl: './todo.component.html',
  styleUrl: './todo.component.scss',
})
export class TodoComponent {
  @Input() tabs!: Array<TodoTableCard>;
}
