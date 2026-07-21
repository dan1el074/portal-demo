import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

type CloseHandler = () => void;

@Injectable({ providedIn: 'root' })
export class BackNavigationService {

  private readonly platformId = inject(PLATFORM_ID);

  private stack: CloseHandler[] = [];
  private listenerAttached = false;

  register(onBack: CloseHandler): void {
    if (!isPlatformBrowser(this.platformId)) return;

    if (!this.listenerAttached) {
      window.addEventListener('popstate', this.onPopState);
      this.listenerAttached = true;
    }

    history.pushState({ ngOverlay: true }, '');
    this.stack.push(onBack);
  }

  unregister(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    if (!this.stack.length) return;

    history.back();
  }

  private onPopState = (): void => {
    const onBack = this.stack.pop();
    if (onBack) onBack();
  };
}
