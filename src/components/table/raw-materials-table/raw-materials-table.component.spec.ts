import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RawMaterialsTableComponent } from './raw-materials-table.component';

describe('RawMaterialsTableComponent', () => {
  let component: RawMaterialsTableComponent;
  let fixture: ComponentFixture<RawMaterialsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RawMaterialsTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RawMaterialsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
