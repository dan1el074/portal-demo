import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InternalCommunicationComponent } from './internal-communication.component';

describe('InternalCommunicationComponent', () => {
  let component: InternalCommunicationComponent;
  let fixture: ComponentFixture<InternalCommunicationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InternalCommunicationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InternalCommunicationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
