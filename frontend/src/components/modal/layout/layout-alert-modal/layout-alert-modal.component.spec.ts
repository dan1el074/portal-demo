import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutAlertModalComponent } from './layout-alert-modal.component';

describe('LayoutAlertModalComponent', () => {
  let component: LayoutAlertModalComponent;
  let fixture: ComponentFixture<LayoutAlertModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutAlertModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutAlertModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
