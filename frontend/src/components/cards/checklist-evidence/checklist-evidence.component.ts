import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnChanges, inject } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import QRCode from 'qrcode';
import { Evidence } from '../../../app/interface/checklist.interface';
import { ChecklistMediaPreviewService } from '../../../app/services/checklist-media-preview.service';
import { ChecklistIconComponent } from '../../icons/checklist-icon/checklist-icon.component';
import { environment } from '../../../environments/environment';

interface PresentedEvidence extends Evidence {
  source?: string;
  safePublicUrl?: SafeResourceUrl;
  qrCode?: string;
}

@Component({
  selector: 'app-checklist-evidence',
  imports: [ChecklistIconComponent],
  templateUrl: './checklist-evidence.component.html',
  styleUrl: './checklist-evidence.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChecklistEvidenceComponent implements OnChanges {
  @Input() media: Evidence[] = [];
  protected presented: PresentedEvidence[] = [];
  private readonly previews = inject(ChecklistMediaPreviewService);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnChanges(): void {
    this.presented = this.media.map((item) => {
      const publicUrl = item.publicUrl?.startsWith('/') ? environment.apiUrl + item.publicUrl : item.publicUrl;
      return ({
      ...item, publicUrl,
      source: item.dataUrl || this.previews.get(item.id),
      safePublicUrl: publicUrl
        ? this.sanitizer.bypassSecurityTrustResourceUrl(publicUrl)
        : undefined,
    }); });
    void this.createQrCodes();
  }

  protected isImage(item: Evidence): boolean {
    return item.type.startsWith('image/');
  }

  protected isVideo(item: Evidence): boolean {
    return item.type.startsWith('video/');
  }

  protected openImage(item: PresentedEvidence): void {
    const url = item.source || item.publicUrl;
    if (!url || !/^(blob:|data:image\/|https?:\/\/)/i.test(url)) return;
    const imageWindow = window.open(url, '_blank', 'noopener,noreferrer');
    if (imageWindow) imageWindow.opener = null;
  }

  private async createQrCodes(): Promise<void> {
    await Promise.all(this.presented.map(async (item) => {
      if (!this.isVideo(item) || !item.publicUrl) return;
      item.qrCode = await QRCode.toDataURL(item.publicUrl, {
        errorCorrectionLevel: 'M',
        margin: 1,
        width: 220,
        color: { dark: '#111111', light: '#ffffff' },
      });
    }));
    this.cdr.markForCheck();
  }
}
