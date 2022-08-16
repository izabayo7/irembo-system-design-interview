import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as SendGrid from '@sendgrid/mail';

@Injectable()
export class SendGridService {
  constructor(private readonly configService: ConfigService) {
    SendGrid.setApiKey(
      // process.env.SENDGRID_API_KEY
      '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',
    );
  }

  async send(mail: SendGrid.MailDataRequired) {
    const transport = await SendGrid.send(mail);
    return transport;
  }
}
