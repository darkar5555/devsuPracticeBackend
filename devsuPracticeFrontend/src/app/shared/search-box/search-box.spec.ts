import { TestBed } from '@angular/core/testing';
import { SearchBox } from './search-box';

describe('SearchBox', () => {
  it('emits the typed text on every input', async () => {
    await TestBed.configureTestingModule({ imports: [SearchBox] }).compileComponents();
    const fixture = TestBed.createComponent(SearchBox);
    fixture.detectChanges();
    const emitted: string[] = [];
    fixture.componentInstance.search.subscribe((value) => emitted.push(value));

    const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
    input.value = 'mari';
    input.dispatchEvent(new Event('input'));

    expect(emitted).toEqual(['mari']);
  });
});
