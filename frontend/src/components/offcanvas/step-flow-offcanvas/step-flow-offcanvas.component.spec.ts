import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepFlowOffcanvasComponent } from './step-flow-offcanvas.component';

describe('StepFlowOffcanvasComponent', () => {
  let component: StepFlowOffcanvasComponent;
  let fixture: ComponentFixture<StepFlowOffcanvasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StepFlowOffcanvasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StepFlowOffcanvasComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
