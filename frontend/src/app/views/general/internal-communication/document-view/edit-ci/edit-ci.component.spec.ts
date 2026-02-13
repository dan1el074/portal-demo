import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCiComponent } from './edit-ci.component';

describe('EditCiComponent', () => {
  let component: EditCiComponent;
  let fixture: ComponentFixture<EditCiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditCiComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditCiComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
