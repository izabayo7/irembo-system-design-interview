import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as SendGrid from '@sendgrid/mail';

@Injectable()
export class SendGridService {
  constructor(private readonly configService: ConfigService) {
    SendGrid.setApiKey(
      // process.env.SENDGRID_API_KEY
      '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'
    );
  }

  async send(mail: SendGrid.MailDataRequired) {
    const transport = await SendGrid.send(mail);
    return transport;
  }
}
