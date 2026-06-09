import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepFlowComponent } from './step-flow.component';

describe('StepFlowComponent', () => {
  let component: StepFlowComponent;
  let fixture: ComponentFixture<StepFlowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StepFlowComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StepFlowComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
