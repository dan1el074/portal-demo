import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CiFormComponent } from './ci-form.component';

describe('CiFormComponent', () => {
  let component: CiFormComponent;
  let fixture: ComponentFixture<CiFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CiFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CiFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
