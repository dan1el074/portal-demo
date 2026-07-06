import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepFlowInputOffcanvasComponent } from './step-flow-input-offcanvas.component';

describe('StepFlowInputOffcanvasComponent', () => {
  let component: StepFlowInputOffcanvasComponent;
  let fixture: ComponentFixture<StepFlowInputOffcanvasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StepFlowInputOffcanvasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StepFlowInputOffcanvasComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
