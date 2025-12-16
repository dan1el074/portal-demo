import { Component, Input, OnInit } from '@angular/core';
import { ButtonCloseDirective, ButtonDirective, FormControlDirective, FormDirective, FormTextDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, ModalToggleDirective } from '@coreui/angular';
import { cilPencil } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RawMaterialsTable } from '../../../app/interface/raw-materials.interface';

@Component({
  selector: 'app-raw-material-modal',
  imports: [
    IconDirective,
    ButtonDirective,
    ButtonCloseDirective,
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    ModalToggleDirective,
    ReactiveFormsModule,
    FormsModule,
    FormDirective,
    FormControlDirective,
    FormTextDirective,
    ButtonDirective
  ],
  templateUrl: './raw-material-modal.component.html',
  styleUrl: './raw-material-modal.component.scss',
})
export class RawMaterialModalComponent implements OnInit {
  @Input() item!: RawMaterialsTable;
  protected icons = { cilPencil };
  protected visible = false;
  protected value!: number;

  ngOnInit(): void {
    this.value = this.item.currentStorage;
  }

  toggleLiveDemo() {
    this.visible = !this.visible;
  }

  handleLiveDemoChange(event: any) {
    this.visible = event;
  }

  increment(): void {
    this.value++;
  }

  decrement(): void {
    if (this.value > 0) {
      this.value--;
    }
  }
}
