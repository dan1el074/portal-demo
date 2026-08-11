import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

type CloseHandler = () => void;

@Injectable({ providedIn: 'root' })
export class BackNavigationService {

  private readonly platformId = inject(PLATFORM_ID);

  private stack: Array<CloseHandler> = [];
  private afterCloseActions: Array<() => void> = [];
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

  runAfterOverlayClose(action: () => void): void {
    if (!isPlatformBrowser(this.platformId) || !this.stack.length) {
      action();
      return;
    }

    this.afterCloseActions.push(action);
  }

  private onPopState = (): void => {
    const onBack = this.stack.pop();
    if (onBack) onBack();

    const afterClose = this.afterCloseActions.shift();
    if (afterClose) setTimeout(afterClose);
  };
}
