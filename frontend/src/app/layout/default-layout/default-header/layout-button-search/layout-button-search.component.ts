import { Component, EventEmitter, HostListener, Output } from '@angular/core';

@Component({
  selector: 'app-layout-button-search',
  imports: [],
  templateUrl: './layout-button-search.component.html',
  styleUrl: './layout-button-search.component.scss',
})
export class LayoutButtonSearchComponent {
  @Output()
  protected openModal = new EventEmitter<any>();

  protected openSearchProjectModal(): void {
    this.openModal.emit();
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.ctrlKey && event.key.toLowerCase() === 'k') {
      event.preventDefault();
      this.openSearchProjectModal();
    }
  }
}
