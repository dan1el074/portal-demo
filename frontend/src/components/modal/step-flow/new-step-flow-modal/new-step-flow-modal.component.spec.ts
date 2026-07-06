import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewStepFlowModalComponent } from './new-step-flow-modal.component';

describe('NewStepFlowModalComponent', () => {
  let component: NewStepFlowModalComponent;
  let fixture: ComponentFixture<NewStepFlowModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewStepFlowModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewStepFlowModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
