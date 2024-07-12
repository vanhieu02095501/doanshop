/*
 * (C) Copyright 2022. All Rights Reserved.
 *
 * @author DongTHD
 * @date Mar 10, 2022
*/
package vn.fs.service.implement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import vn.fs.dto.MailInfo;
import vn.fs.service.SendMailService;

@Service
public class SendMailServiceImplement implements SendMailService {

	@Autowired
	JavaMailSender sender;

	List<MailInfo> list = new ArrayList<>();

	@Override
	public void send(MailInfo mail) throws MessagingException, IOException {
		// Tạo message
		MimeMessage message = sender.createMimeMessage();
		// Sử dụng Helper để thiết lập các thông tin cần thiết cho message
		//Tham số true cho phép gửi email dạng multipart (có thể bao gồm cả text và HTML),
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
		helper.setFrom(mail.getFrom());
		helper.setTo(mail.getTo());
		helper.setSubject(mail.getSubject());
		//Thiết lập nội dung của email và chỉ định rằng nội dung này là HTML (thông qua tham số true).
		helper.setText(mail.getBody(), true);
		//Thiết lập địa chỉ mail nhận phản hồi
		helper.setReplyTo(mail.getFrom());

		if (mail.getAttachments() != null) {
			// đại diện cho tệp đính kèm từ đường dẫn tệp đính kèm.
			FileSystemResource file = new FileSystemResource(new File(mail.getAttachments()));
			helper.addAttachment(mail.getAttachments(), file);
		}

		// Gửi message đến SMTP server
		sender.send(message);

	}

	//Danh sách này đại diện cho hàng đợi email cần được gửi.
	@Override
	public void queue(MailInfo mail) {
		list.add(mail);
	}

	@Override
	public void queue(String to, String subject, String body) {
		//Tạo một đối tượng MailInfo mới với các tham số được truyền vào và gọi phương thức queue(MailInfo mail) để thêm đối tượng này vào danh sách list.
		queue(new MailInfo(to, subject, body));
	}
	//để Spring Scheduler sẽ thực thi nó định kỳ mỗi 5000 milliseconds (5 giây).
	@Override
	@Scheduled(fixedDelay = 5000)
	public void run() {
		// kiểm tra ds hàng đợi
		//
		while (!list.isEmpty()) {
			//Sau khi lấy phần tử đầu tiên, remove(0) cũng loại bỏ phần tử đó khỏi danh sách
			MailInfo mail = list.remove(0);
			try {
				this.send(mail);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
