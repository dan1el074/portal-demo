import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { MemorandoComponent } from './memorando.component';
import { MemorandoService } from '../../../services/memorando.service';

describe('MemorandoComponent', () => {
  let component: MemorandoComponent;
  let fixture: ComponentFixture<MemorandoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemorandoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemorandoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('keeps the backend descending order after the CoreUI workaround', () => {
    const service = TestBed.inject(MemorandoService);
    const first = { id: 1 };
    const second = { id: 2 };
    spyOn(service, 'findAll').and.returnValue(of({
      content: [first, second],
      totalElements: 2,
      totalPages: 1,
    } as any));
    (component as any).currentSort = { column: 'id', state: 'desc' };

    (component as any).loadMemorandos();

    expect((component as any).memorandos.map((item: { id: number }) => item.id)).toEqual([2, 1]);
  });

  it('ignores repeated table state events', () => {
    const loadSpy = spyOn<any>(component, 'loadMemorandos');
    (component as any).activeItemKey = 0;
    (component as any).currentSort = { column: 'client', state: 'asc' };
    (component as any).fullText = false;

    (component as any).onTabChange(0);
    (component as any).onSorterChange({ column: 'client', state: 'asc' });
    (component as any).onFullTextChange(false);
    (component as any).filterByStatus(undefined);

    expect(loadSpy).not.toHaveBeenCalled();
  });

  it('loads once when the table sort changes', () => {
    const loadSpy = spyOn<any>(component, 'loadMemorandos');

    (component as any).onSorterChange({ column: 'client', state: 'desc' });

    expect(loadSpy).toHaveBeenCalledTimes(1);
  });
});
