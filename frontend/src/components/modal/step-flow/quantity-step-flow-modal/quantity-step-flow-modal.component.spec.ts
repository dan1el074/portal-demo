import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuantityStepFlowModalComponent } from './quantity-step-flow-modal.component';

describe('QuantityStepFlowModalComponent', () => {
  let component: QuantityStepFlowModalComponent;
  let fixture: ComponentFixture<QuantityStepFlowModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuantityStepFlowModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuantityStepFlowModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
