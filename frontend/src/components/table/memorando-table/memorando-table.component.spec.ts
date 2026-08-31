import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemorandoTableComponent } from './memorando-table.component';

describe('MemorandoTableComponent', () => {
  let component: MemorandoTableComponent;
  let fixture: ComponentFixture<MemorandoTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemorandoTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemorandoTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('does not echo filter values received from the parent', () => {
    component.searchValue = 'MT-101513';
    const filterSpy = spyOn(component.filterChange, 'emit');

    (component as any).onFilterValueChange('MT-101513');
    (component as any).onFilterValueChange('MT-101514');

    expect(filterSpy).toHaveBeenCalledOnceWith('MT-101514');
  });

  it('does not echo sorter values received from the parent', () => {
    component.sorterValue = { column: 'client', state: 'desc' };
    const sorterSpy = spyOn(component.sorterChange, 'emit');

    (component as any).onSorterValueChange({ column: 'client', state: 'desc' });

    expect(sorterSpy).not.toHaveBeenCalled();
  });
});
