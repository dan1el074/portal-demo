import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewMemorandoModalComponent } from './new-memorando-modal.component';

describe('NewMemorandoModalComponent', () => {
  let component: NewMemorandoModalComponent;
  let fixture: ComponentFixture<NewMemorandoModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewMemorandoModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewMemorandoModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
