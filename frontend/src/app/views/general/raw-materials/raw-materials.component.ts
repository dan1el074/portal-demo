import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { AccordionButtonDirective, AccordionComponent, AccordionItemComponent, AlertComponent, CardBodyComponent, CardComponent, CardTitleDirective, Tabs2Module, TemplateIdDirective } from '@coreui/angular';
import { RawMaterialsTableComponent } from '../../../../components/table/raw-materials-table/raw-materials-table.component';
import { cilXCircle, cilCheckCircle, cilWarning } from '@coreui/icons';
import { RawMaterialsTable } from '../../../interface/raw-materials.interface';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-raw-materials',
  imports: [
    CommonModule,
    AlertComponent,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    Tabs2Module,
    RawMaterialsTableComponent,
    AccordionComponent,
    AccordionItemComponent,
    TemplateIdDirective,
    AccordionButtonDirective,
    IconDirective
  ],
  templateUrl: './raw-materials.component.html',
  styleUrl: './raw-materials.component.scss',
})
export class RawMaterialsComponent implements OnInit {
  protected icons = { cilXCircle, cilCheckCircle, cilWarning };
  protected tabs: Array<{name: string, active: boolean}> = [];
  protected disableItems: Array<RawMaterialsTable> = [];
  protected items: Array<Array<RawMaterialsTable>> = [];
  protected allItems: Array<RawMaterialsTable> = [
    {
      id: 1,
      code: '21805',
      name: 'CH A36 #1,55 1200x3000mm GALV. Z275 MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 73,
      currentStorageKg: 3208.79,
      minStorage: 30,
      minStorageKg: 1319.68,
      maxStorage: 45,
      maxStorageKg: 1978.02,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Chapas Finas',
      active: true
    },
    {
      id: 2,
      code: '18912',
      name: 'CH A36 #1,50 1200x3000mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 18,
      currentStorageKg: 791.21,
      minStorage: 7,
      minStorageKg: 307.69,
      maxStorage: 11,
      maxStorageKg: 461.54,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Chapas Finas',
      active: true
    },
    {
      id: 3,
      code: '32641',
      name: 'CH A36 #2,00 1200x1390mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 31,
      currentStorageKg: 827.33,
      minStorage: 25,
      minStorageKg: 667.20,
      maxStorage: 38,
      maxStorageKg: 1000.80,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Chapas Finas',
      active: true
    },
    {
      id: 4,
      code: '32614',
      name: 'CH A36 #2,00 1200x1690mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 114,
      currentStorageKg: 3699.07,
      minStorage: 120,
      minStorageKg: 3893.76,
      maxStorage: 180,
      maxStorageKg: 5840.64,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Chapas Finas',
      active: true
    },
    {
      id: 5,
      code: '18751',
      name: 'CH A36 #2,00 1200x1855mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 66,
      currentStorageKg: 2350.66,
      minStorage: 80,
      minStorageKg: 2849.28,
      maxStorage: 120,
      maxStorageKg: 4273.92,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Chapas Finas',
      active: true
    },
    {
      id: 6,
      code: '28691',
      name: 'CH A36 #2,00 1200x1855mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 63,
      currentStorageKg: 2727.65,
      minStorage: 40,
      minStorageKg: 1731.84,
      maxStorage: 60,
      maxStorageKg: 2597.76,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Chapas Finas',
      active: true
    },
    {
      id: 7,
      code: '21311',
      name: 'CH A36 #2,00 1200x3000mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 20,
      currentStorageKg: 1152.0,
      minStorage: 10,
      minStorageKg: 565.56,
      maxStorage: 15,
      maxStorageKg: 848.34,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Chapas Finas',
      active: true
    },
    {
      id: 8,
      code: '32615',
      name: 'CH A36 #3,00 1200x1690mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 24,
      currentStorageKg: 1168.13,
      minStorage: 40,
      minStorageKg: 1946.88,
      maxStorage: 60,
      maxStorageKg: 2920.32,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Chapas Finas',
      active: true
    },
    {
      id: 9,
      code: '32641',
      name: 'BARRA CHATA #2,00 1200x1390mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 31,
      currentStorageKg: 827.33,
      minStorage: 25,
      minStorageKg: 667.20,
      maxStorage: 38,
      maxStorageKg: 1000.80,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Aços Longos',
      active: true
    },
    {
      id: 10,
      code: '32614',
      name: 'BARRA CHATA #2,00 1200x1690mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 114,
      currentStorageKg: 3699.07,
      minStorage: 120,
      minStorageKg: 3893.76,
      maxStorage: 180,
      maxStorageKg: 5840.64,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Aços Longos',
      active: true
    },
    {
      id: 11,
      code: '18751',
      name: 'BARRA CHATA #2,00 1200x1855mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 66,
      currentStorageKg: 2350.66,
      minStorage: 80,
      minStorageKg: 2849.28,
      maxStorage: 120,
      maxStorageKg: 4273.92,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Aços Longos',
      active: true
    },
    {
      id: 12,
      code: '28691',
      name: 'BARRA CHATA #2,00 1200x1855mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 63,
      currentStorageKg: 2727.65,
      minStorage: 40,
      minStorageKg: 1731.84,
      maxStorage: 60,
      maxStorageKg: 2597.76,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Aços Longos',
      active: true
    },
    {
      id: 13,
      code: '21311',
      name: 'BARRA CHATA #2,00 1200x3000mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 20,
      currentStorageKg: 1152.0,
      minStorage: 10,
      minStorageKg: 565.56,
      maxStorage: 15,
      maxStorageKg: 848.34,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Aços Longos',
      active: true
    },
    {
      id: 14,
      code: '32615',
      name: 'BARRA CHATA #3,00 1200x1690mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 24,
      currentStorageKg: 1168.13,
      minStorage: 40,
      minStorageKg: 1946.88,
      maxStorage: 60,
      maxStorageKg: 2920.32,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Aços Longos',
      active: true
    },
    {
      id: 15,
      code: '32641',
      name: 'BARRA CHATA #2,00 1200x1390mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 31,
      currentStorageKg: 827.33,
      minStorage: 25,
      minStorageKg: 667.20,
      maxStorage: 38,
      maxStorageKg: 1000.80,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Aços Longos',
      active: true
    },
    {
      id: 16,
      code: '32641',
      name: 'BARRA CHATA #2,00 1200x1390mm MP',
      description: 'Chapas de Proteção lateral de NDE e Mesas',
      currentStorage: 31,
      currentStorageKg: 827.33,
      minStorage: 25,
      minStorageKg: 667.20,
      maxStorage: 38,
      maxStorageKg: 1000.80,
      updateAt: '2025-12-10T00:00:00Z',
      user: 'Daniel Vargas',
      type: 'Aços Longos',
      active: true
    },
  ];

  ngOnInit(): void {
    this.updateData();
  }

  updateData() {
    this.allItems.forEach(item => {
      let tabIndex = this.tabs.findIndex(tab => tab.name == item.type);

      if (tabIndex == -1) {
        this.tabs.push({ name: item.type, active: false });
        tabIndex = this.tabs.length - 1;

        this.items.push([]);
      }

      if (!this.tabs[tabIndex].active) {
        if (item.active) this.tabs[tabIndex].active = true;
      }

      if (item.active) {
        this.items[tabIndex].push(item);
      } else {
        this.disableItems.push(item);
      }
    });

    this.tabs.push({ name: 'Desabilitado', active: this.disableItems.length > 0 ? true : false });
  }
}
