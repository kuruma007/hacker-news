package com.example.hackerNews.controller;

import com.example.hackerNews.entity.User;
import com.example.hackerNews.service.UserService;
import com.example.hackerNews.userDefinedException.UserNotFoundException;
import com.example.hackerNews.utility.Utility;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/forgotPassword")
    public String showForgotPasswordForm(Model model){
        model.addAttribute("pageTitle", "Forgot Password");
        return "forgotPasswordForm";
    }

    @PostMapping("/forgotPassword")
    public  String processForgotPasswordForm(HttpServletRequest request, Model model){
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/resetPassword?token=" + token;

            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "Reset Password Link sent, please check your e-mail");
        } catch (UserNotFoundException e) {
           model.addAttribute("error", e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
           model.addAttribute("error", "error while sending mail");
        }
        model.addAttribute("pageTitle", "Forgot Password");
        return "forgotPasswordForm";
    }

    private void sendEmail(String email, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("tarunsinghrawat007@gmail.com", "Hacker News Clone Support");
        helper.setTo(email);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + resetPasswordLink + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        System.out.println(resetPasswordLink);

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/resetPassword")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model){
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if(user == null){
         //   model.addAttribute("message", "Invalid Token");
            return "message";
        }
        model.addAttribute("token", token);
      //  model.addAttribute("pageTitle", "Reset Your Password");

        return "resetPasswordForm";
    }

    @PostMapping("/resetPassword")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (user == null) {
           // model.addAttribute("message", "Invalid Token");
          //  return "message";
        } else {
            userService.updatePassword(user, password);
          //  model.addAttribute("message", "You have successfully changed your password.");
        }
        return "login";
    }
}
