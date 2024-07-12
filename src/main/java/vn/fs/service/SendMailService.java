/*
 * (C) Copyright 2022. All Rights Reserved.
 *
 * @author DongTHD
 * @date Mar 10, 2022
*/
package vn.fs.service;

import java.io.IOException;

import javax.mail.MessagingException;

import vn.fs.dto.MailInfo;

public interface SendMailService {

	void run();
	//nhận ba tham số: địa chỉ email người nhận (to), tiêu đề email (subject), và nội dung email (body).
	void queue(String to, String subject, String body);

	//MailInfo có thể là một lớp chứa thông tin về email (như địa chỉ người nhận, tiêu đề, và nội dung). Phương thức này cũng có thể được sử dụng để đưa email vào hàng đợi.
	void queue(MailInfo mail);
	//Phương thức này có trách nhiệm gửi email ngay lập tức và xử lý các ngoại lệ có thể xảy ra trong quá trình gửi
	void send(MailInfo mail) throws MessagingException, IOException;

}
