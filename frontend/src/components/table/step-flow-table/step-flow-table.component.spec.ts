import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepFlowTableComponent } from './step-flow-table.component';

describe('StepFlowTableComponent', () => {
  let component: StepFlowTableComponent;
  let fixture: ComponentFixture<StepFlowTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StepFlowTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StepFlowTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
