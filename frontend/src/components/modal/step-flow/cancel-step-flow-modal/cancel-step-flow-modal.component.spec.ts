import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelStepFlowModalComponent } from './cancel-step-flow-modal.component';

describe('CancelStepFlowModalComponent', () => {
  let component: CancelStepFlowModalComponent;
  let fixture: ComponentFixture<CancelStepFlowModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelStepFlowModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancelStepFlowModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
