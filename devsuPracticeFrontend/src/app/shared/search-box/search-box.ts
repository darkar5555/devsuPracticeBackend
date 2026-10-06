import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-search-box',
  templateUrl: './search-box.html',
  styleUrl: './search-box.css',
})
export class SearchBox {
  readonly placeholder = input('Buscar');
  readonly search = output<string>();

  protected onInput(event: Event) {
    this.search.emit((event.target as HTMLInputElement).value);
  }
}
