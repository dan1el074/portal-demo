import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RawMaterialModalComponent } from './raw-material-modal.component';

describe('RawMaterialModalComponent', () => {
  let component: RawMaterialModalComponent;
  let fixture: ComponentFixture<RawMaterialModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RawMaterialModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RawMaterialModalComponent);
    component = fixture.componentInstance;
    component.item = { currentStorage: 0 } as any;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
